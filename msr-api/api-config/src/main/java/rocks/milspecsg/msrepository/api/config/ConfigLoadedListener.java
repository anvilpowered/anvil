package rocks.milspecsg.msrepository.api.config;

@FunctionalInterface
public interface ConfigLoadedListener {
    /**
     * Called from {@link ConfigurationService} after the config has finished loading from the file
     */
    void loaded();
}
