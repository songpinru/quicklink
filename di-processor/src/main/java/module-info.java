import com.prdi.di.annotation.processor.Configuration;
import com.prdi.di.annotation.processor.Service;

/**
 * @author pinru
 * @version 1.0
 * @date 2024/5/7
 */
open module com.prdi.di.processor {
    requires java.base;
    requires transitive java.compiler;
    exports com.prdi.di.annotation.processor;
    provides javax.annotation.processing.Processor with com.prdi.di.annotation.processor.DiAnnotationProcess;
    uses Configuration;
    uses Service;
}