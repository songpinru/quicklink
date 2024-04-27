package org.example.util;

import org.apache.commons.lang3.ClassPathUtils;
import org.apache.flink.shaded.guava30.com.google.common.reflect.ClassPath;

import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ClassUtils {
    public static final String FILE = "file";

    public static List<Class<?>> getClasses(String packageName, boolean recursive) {

        String packageDir = packageName.replace('.', File.separatorChar);
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try {
            Enumeration<URL> pkgDirs = classLoader.getResources(packageDir);
            while (pkgDirs.hasMoreElements()) {
                URL url = pkgDirs.nextElement();
                String protocol = url.getProtocol();
                Path path = null;
                try {
                    path = Paths.get(url.toURI());
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
                ClassPath.from(classLoader).getAllClasses();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return null;
    }
    public static Collection<Class> getClasses(final String pack) throws Exception {
        final StandardJavaFileManager fileManager = ToolProvider.getSystemJavaCompiler().getStandardFileManager(null, null, null);
        return StreamSupport.stream(fileManager.list(StandardLocation.CLASS_PATH, pack, Collections.singleton(JavaFileObject.Kind.CLASS), false).spliterator(), false)
                .map(javaFileObject -> {
                    try {
                        final String[] split = javaFileObject.getName()
                                .replace(".class", "")
                                .replace(")", "")
                                .split(Pattern.quote(File.separator));

                        final String fullClassName = pack + "." + split[split.length - 1];
                        return Class.forName(fullClassName);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

}
