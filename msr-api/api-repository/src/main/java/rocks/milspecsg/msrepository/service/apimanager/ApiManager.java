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
import rocks.milspecsg.msrepository.api.config.ConfigKeys;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;
import rocks.milspecsg.msrepository.api.manager.Manager;
import rocks.milspecsg.msrepository.api.manager.annotation.H2Repo;
import rocks.milspecsg.msrepository.api.manager.annotation.JsonRepo;
import rocks.milspecsg.msrepository.api.manager.annotation.MariaRepo;
import rocks.milspecsg.msrepository.api.manager.annotation.MongoRepo;
import rocks.milspecsg.msrepository.api.storageservice.DataStorageService;
import rocks.milspecsg.msrepository.model.data.dbo.ObjectWithId;

import java.util.Objects;

public abstract class ApiManager<T extends ObjectWithId<?>, R extends DataStorageService<?, T>> implements Manager<T, R> {

    protected ConfigurationService configurationService;

    public ApiManager(ConfigurationService configurationService) {
        this.configurationService = configurationService;
        configurationService.addConfigLoadedListener(this::configLoaded);
    }

    @Inject(optional = true)
    @H2Repo
    private R h2Service;

    @Inject(optional = true)
    @JsonRepo
    private R jsonService;

    @Inject(optional = true)
    @MariaRepo
    private R mariaService;

    @Inject(optional = true)
    @MongoRepo
    private R mongoService;

    private R currentService = null;

    private void configLoaded(Object plugin) {
        String dataStoreName = configurationService.getConfigString(ConfigKeys.DATA_STORE_NAME);
        try {
            switch (dataStoreName.toLowerCase()) {
                case "h2":
                    currentService = Objects.requireNonNull(h2Service);
                    break;
                case "json":
                    currentService = Objects.requireNonNull(jsonService);
                    break;
                case "mariadb":
                    currentService = Objects.requireNonNull(mariaService);
                    break;
                case "mongodb":
                    currentService = Objects.requireNonNull(mongoService);
                    break;
                default:
                    throw new IllegalStateException("Invalid dataStoreName");
            }
        } catch (Exception e) {
            System.err.println("MSRepository: Could not find requested data store: \"" + dataStoreName + "\". Did you bind it correctly?");
            throw e;
        }
    }

    @Override
    public R getPrimaryStorageService() {
        try {
            return Objects.requireNonNull(currentService);
        } catch (Exception e){
            System.err.println("MSRepository: DataStoreName has not been loaded yet!");
            throw e;
        }
    }
}
