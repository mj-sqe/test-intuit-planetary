package com.intuit.test.planetary.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.intuit.test.planetary.utils.LogUtils;


// TODO: Auto-generated Javadoc
/**
 * A utility class to extract parameters from the projects config.properties file
 * for the automation framework. 
 */
public class Config {

    /**
     * An object to hold the ConfigHolder instance.
     */
    private static final class ConfigHolder { 

        /**
         * A variable to hold the singleton instance of the Config class.
         */
        private static final Config INSTANCE = new Config();

    }

    /**
     * The partial name of the property in the configuration file to use for the
     * IDX API base URL.
     */
    private static final String PARAM_API_BASE_URL = ".api.base.url";
    
    /**
     * The name of the property in the configuration file to use for the API key.
     */
    private static final String PARAM_API_KEY = "api.key";

    /**
     * The name of the property in the configuration file to use for the proxy
     * host.
     */
    private static final String PARAM_PROXY_HOST = "proxy.host";

    /**
     * The name of the property in the configuration file to use for the proxy
     * port.
     */
    private static final String PARAM_PROXY_PORT = "proxy.port";

    /**
     * The location of the configuration properties file.
     */
    private static final String CONFIG_FILE_DIR = "src" + File.separator + "test" + File.separator + "resources"
            + File.separator;

    /**
     * The name of the configuration properties file.
     */
    private static final String CONFIG_PROPERTIES_FILE_NAME = "config.properties";

    /**
     * The full path location of the configuration properties file name.
     */
    private static final String CONFIG_FILE_NAME = CONFIG_FILE_DIR + CONFIG_PROPERTIES_FILE_NAME;

    /**
     * Returns the singleton instance of the Config object.
     *
     * @return The singleton Config instance.
     */
    public static Config getInstance() {
        return ConfigHolder.INSTANCE;
    }

    /**
     * The API base URL to be used in the test.
     */
    private String apiBaseUrl = null;
    
    /**
     * The API key to be used in the test.
     */
    private String apiKey = null;

    /**
     * The proxy host to be used in the test.
     */
    private String proxyHost = null;

    /**
     * The proxy port to be used in the test.
     */
    private String proxyPort = null;

    /**
     * The environment to be used in the test.
     */
    private String envPrefix = null;

    /** A properties object to hold the configuration settings. */
    private Properties configProperties = null;

    /** A flag to determine whether the configuration file has already been loaded into memory. */
    private boolean isConfigLoaded = false;

    /**
     * Constructor to prevent instantiation from other classes.
     */
    private Config() {
    }

    /**
     * Gets the api base url.
     *
     * @return the api base url
     */
    public String getApiBaseUrl() {
        return verifyIfPropertyExistsAndReturn(apiBaseUrl, true,
                getEnvPrefix().toLowerCase().trim() + PARAM_API_BASE_URL);
    }
    
    
    /**
     * Gets the api key.
     *
     * @return the api key
     */
    public String getApiKey() {
        return verifyIfPropertyExistsAndReturn(apiKey, true, PARAM_API_KEY);
    }

    /**
     * Gets the env prefix.
     *
     * @return the env prefix
     */
    public String getEnvPrefix() {
        return envPrefix;
    }

    /**
     * Gets the property.
     *
     * @param configFileName the config file name
     * @param propertyName the property name
     * @return the property
     */
    public String getProperty(String configFileName, String propertyName) {
        loadConfigSilent(configFileName);
        String property = null;
        if (configProperties != null) {
            property = configProperties.getProperty(propertyName);
            if (property == null || property.isEmpty()) {
                LogUtils.logWarningMessage("The property '" + propertyName + "' was not found in properties file.");
            }
        } else {
            LogUtils.logWarningMessage("properties file does not exist.  Therefore, cannot obtain requested "
                    + "property '" + propertyName + "'.");
        }

        return property;
    }

    /**
     * Gets the proxy host.
     *
     * @return the proxy host
     */
    public String getProxyHost() {
        return verifyIfPropertyExistsAndReturn(proxyHost, false, PARAM_PROXY_HOST);
    }

    /**
     * Gets the proxy port.
     *
     * @return the proxy port
     */
    public String getProxyPort() {
        return verifyIfPropertyExistsAndReturn(proxyPort, false, PARAM_PROXY_PORT);
    }

    /**
     * Gets the required property.
     *
     * @param configFileName the config file name
     * @param propertyName the property name
     * @return the required property
     */
    public String getRequiredProperty(String configFileName, String propertyName) {
        loadConfigSilent(configFileName);
        String property = null;
        if (configProperties != null) {
            property = configProperties.getProperty(propertyName);
            if (property == null || property.isEmpty()) {
                LogUtils.logSevereMessageThenFail(
                        "The required property '" + propertyName + "' was not found in properties file.");
            }
        } else {
            LogUtils.logWarningMessage("properties file does not exist.  Therefore, cannot obtain requested "
                    + "property '" + propertyName + "'.");
        }

        return property;
    }

    /**
     * Load config.
     *
     * @param configFileName the config file name
     */
    public void loadConfig(String configFileName) {
        if (!isConfigLoaded) {
            File configFile = new File(configFileName);
            if (configFile.exists()) {
                configProperties = new Properties();
                InputStream input = null;

                try {
                    input = new FileInputStream(configFile);
                    configProperties.load(input);
                } catch (FileNotFoundException e) {
                    LogUtils.logSevereMessage(
                            "The " + configFileName + " file was not found in project location: " + configFileName);
                } catch (IOException e) {
                    LogUtils.logSevereMessage("loadConfig(): Loading the " + configFileName
                            + " could not be succeeded.  IOException : " + e.getMessage());
                } finally {
                    if (input != null) {
                        try {
                            input.close();
                        } catch (IOException e) {
                            LogUtils.logSevereMessage("loadConfig(): Failed closing FileInputStream.  Exception: "
                                    + LogUtils.convertStackTraceToString(e));
                        }
                    }
                }
                isConfigLoaded = true;
            }

        }
    }

    /**
     * Load config silent.
     *
     * @param configFileName the config file name
     */
    public void loadConfigSilent(String configFileName) {
        try {
            loadConfig(configFileName);
        } catch (Exception e) {
            LogUtils.logWarningMessage("Exception while loading " + configFileName + ".  Exception: " + e);
        }
    }

    /**
     * Sets the env prefix.
     *
     * @param env the new env prefix
     */
    public void setEnvPrefix(String env) {
        if (envPrefix == null) {
            envPrefix = env;
        }
    }

    /**
     * Verify if property exists and return.
     *
     * @param propVar the prop var
     * @param required the required
     * @param propertyName the property name
     * @return the string
     */
    private String verifyIfPropertyExistsAndReturn(String propVar, boolean required, String propertyName) {
        // Check if the property value is blank or null.
        String propVarResult = propVar;
        if (propVar == null || propVar.isEmpty()) {
            if (required) {
                // Get the property value in the "required" state.
                propVarResult = getRequiredProperty(CONFIG_FILE_NAME, propertyName);
            } else {
                // Get the property value in the "not required" state.
                propVarResult = getProperty(CONFIG_FILE_NAME, propertyName);
            }
        }
        // Return the property value.
        return propVarResult;
    }

}