/*
 *     MSRepository - MilSpecSG
 *     Copyright (C) 2019 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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

public abstract class ApiManager<T extends ObjectWithId<?>, R extends Repository<?, T, ?>> implements Manager<T, R> {

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
        } catch (ProvisionException | NullPointerException e) {
            System.err.println("MSRepository: could not find requested data store: \"" + ds + "\". Did you bind it correctly?");
            throw e;
        }
    }
}
