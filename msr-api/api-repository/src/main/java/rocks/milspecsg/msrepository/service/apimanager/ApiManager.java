package rocks.milspecsg.msrepository.service.apimanager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import rocks.milspecsg.msrepository.api.config.ConfigKeys;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;
import rocks.milspecsg.msrepository.api.manager.Manager;
import rocks.milspecsg.msrepository.api.manager.annotation.H2Repo;
import rocks.milspecsg.msrepository.api.manager.annotation.JsonRepo;
import rocks.milspecsg.msrepository.api.manager.annotation.MariaRepo;
import rocks.milspecsg.msrepository.api.manager.annotation.MongoRepo;
import rocks.milspecsg.msrepository.api.repository.Repository;
import rocks.milspecsg.msrepository.model.data.dbo.ObjectWithId;

public abstract class ApiManager<T extends ObjectWithId<?>, R extends Repository<?, T>> implements Manager<T, R> {

    protected ConfigurationService configurationService;

    private String dataStoreName = "";

    public ApiManager(ConfigurationService configurationService) {
        this.configurationService = configurationService;
        configurationService.addConfigLoadedListener(this::configLoaded);
    }

    protected final String getDataStoreName() {
        return dataStoreName;
    }

    private void configLoaded(Object plugin) {
        dataStoreName = configurationService.getConfigString(ConfigKeys.DATA_STORE_NAME);
    }

    @Inject(optional = true)
    @H2Repo
    private Provider<R> h2RepositoryProvider;

    @Inject(optional = true)
    @JsonRepo
    private Provider<R> jsonRepositoryProvider;

    @Inject(optional = true)
    @MariaRepo
    private Provider<R> mariaRepositoryProvider;

    @Inject(optional = true)
    @MongoRepo
    private Provider<R> mongoRepositoryProvider;

    @Override
    public R getPrimaryRepository() {
        final String ds = dataStoreName.toLowerCase();
        try {
            switch (ds) {
                case "h2":
                    return h2RepositoryProvider.get();
                case "json":
                    return jsonRepositoryProvider.get();
                case "mariadb":
                    return mariaRepositoryProvider.get();
                case "mongodb":
                    return mongoRepositoryProvider.get();
                default:
                    throw new IllegalStateException("Invalid dataStoreType");
            }
        } catch (ProvisionException e) {
            System.err.println("MSRepository: could not find requested data store: \"" + ds + "\". Did you bind it correctly?");
            throw e;
        }
    }
}
