/**
 * @author pinru
 * @version 1.0
 */
module com.prdi.di {
    requires org.slf4j;

    exports com.prdi.di;
    exports com.prdi.di.api;
    uses com.prdi.di.api.ConfigurationProvider;
    uses com.prdi.di.api.ServiceProvider;
}