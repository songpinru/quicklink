import com.prdi.di.processor.DiAnnotationProcess;

/**
 * @author pinru
 * @version 1.0
 * @date 2024/5/7
 */
open module com.prdi.di.processor {
    requires java.base;
    requires transitive java.compiler;
    requires transitive com.prdi.di;
    exports com.prdi.di.processor;
    provides javax.annotation.processing.Processor with DiAnnotationProcess;

}