package project_biu.configs;

/**
 * Interface defining the configuration settings for a system or application component.
 * Provides methods for creating a configuration, retrieving the configuration name and version, and closing the configuration.
 */
public interface Config {

    /**
     * Creates the configuration with necessary initial setup.
     * @throws Exception if there is an error during the creation process.
     */
    void create() throws Exception;

    /**
     * Retrieves the name of the configuration.
     * @return The name of the configuration.
     */
    String getName();

    /**
     * Retrieves the version number of the configuration.
     * @return The version number of the configuration.
     */
    int getVersion();

    /**
     * Closes the configuration and releases any resources associated with it.
     */
    void close();
}
