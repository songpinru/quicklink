package com.demo.annotation.processor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * 通过注解发现Class，把有Service注解(同一层需要ServiceProvider注解)的Class通过spi注册，
 *
 * @author pinru
 * @version 1.0
 * @date 2024/5/3
 */
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class DiAnnotationProcess extends AbstractProcessor {

    private final Map<String, Set<String>> annotationsMap = new HashMap<>();
    private final Map<String, String> providerMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        getSupportedAnnotationTypes().forEach(a -> annotationsMap.putIfAbsent(a, new HashSet<>()));
        getSupportedAnnotationTypes().forEach(a -> providerMap.putIfAbsent(a, a + "Provider"));
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            generateConfigFile();
        } else {
            for (final TypeElement annotation : annotations) {
                processAnnotation(roundEnv, annotation);
            }
        }
        return true;
    }

    private void generateConfigFile() {

        final Set<String> annotations = getSupportedAnnotationTypes();
        for (String annotation : annotations) {
            final String fileName = "META-INF/services/" +providerMap.get(annotation);
            Set<String> existServices = loadExistServices(fileName);
            info("Generate resource file:" + fileName);
            if (existServices.addAll(annotationsMap.get(annotation))) {
                generateNewConfig(fileName, existServices);
            } else {
                info("No new entries of %s being added.".formatted(annotation));
            }
        }
    }

    private void generateNewConfig(String fileName, Set<String> allServices) {
        try (final BufferedWriter writer = new BufferedWriter(processingEnv
                .getFiler()
                .createResource(StandardLocation.CLASS_OUTPUT, "", fileName)
                .openWriter())) {
            for (final String service : allServices) {
                writer.write(service);
                writer.newLine();
            }
            writer.flush();
        } catch (IOException e) {
            error("Unable to write into %s ,".formatted(fileName), e);
        }
    }

    private Set<String> loadExistServices(String fileName) {
        final HashSet<String> res = new HashSet<>();
        try (final BufferedReader reader = new BufferedReader(processingEnv.getFiler().getResource(StandardLocation.CLASS_OUTPUT, "", fileName).openReader(true))) {
            reader.lines().forEach(res::add);
        } catch (IOException e) {
            info("Resource file:%s did not already exist.".formatted(fileName));
        }
        return res;
    }

    private void processAnnotation(RoundEnvironment roundEnv, TypeElement annotation) {
        info("process annotation: %s".formatted(annotation));
        final Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
        for (final TypeElement typeElement : ElementFilter.typesIn(elements)) {
            final String packageName = typeElement.getEnclosingElement().asType().toString();
            final String fullClassName = typeElement.getQualifiedName().toString() + annotation.getSimpleName();
            final String className = typeElement.getSimpleName().toString() + annotation.getSimpleName();
            annotationsMap.get(annotation.getQualifiedName().toString()).add(fullClassName);

            try (final Writer writer = processingEnv.getFiler().createSourceFile(fullClassName).openWriter()) {
                final String source = """
                        package %s;
                        public class %s implements %s {
                            @Override
                            public Class<?> get() {
                                return %s.class;
                            }
                        }
                        """.formatted(packageName, className, annotation.getQualifiedName() + "Provider", typeElement.getQualifiedName());
                writer.write(source);
                writer.flush();
            } catch (IOException e) {
                error("Unable to create source file.", typeElement, e);
            }
        }
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(Service.class.getName(), Configuration.class.getName());
    }

    private void info(String msg) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, msg);
    }

    private void warning(String msg) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, msg);
    }

    private void error(String msg, Exception e) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg + e);
    }

    private void error(String msg, Element element, Exception e) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg + e, element);
    }
}
