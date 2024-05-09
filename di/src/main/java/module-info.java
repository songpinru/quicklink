/**
 * @author pinru
 * @version 1.0
 */
module com.prdi.di {
    requires transitive com.prdi.di.processor;
    requires org.slf4j;

    exports com.prdi.di;
    exports com.prdi.di.api;
 }