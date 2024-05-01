package org.example.config;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationUtil {
    static final String CONFIGURATION_FILE = "application-%s.properties";
    static final String ACTIVE_PROFILE = "profiles.active";
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationUtil.class);

    public static ParameterTool loadConfigurations(ParameterTool fromArgs) {
        String activeProfile;
        activeProfile = fromArgs.get(ACTIVE_PROFILE);
        if (StringUtils.isEmpty(activeProfile)) {
            ParameterTool fromSystemProperties = ParameterTool.fromSystemProperties();
            activeProfile = fromSystemProperties.get(ACTIVE_PROFILE);
        }
        if (StringUtils.isEmpty(activeProfile)) {
            logger.error("Not found profiles.active from args and property.");
            throw new ConfigurationException("profiles.active not found");
        }
        String fileName = String.format(CONFIGURATION_FILE, activeProfile);

        InputStream configInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
        if (configInputStream == null) {
            logger.error("The configuration file: {} not found.", fileName);
            throw new ConfigurationException("not found configuration file");
        }

        try {
            return ParameterTool.fromPropertiesFile(configInputStream);
        } catch (IOException e) {
            logger.error("Failed while read the configuration file: {}", fileName);
            throw new ConfigurationException(e);
        }
    }
}

