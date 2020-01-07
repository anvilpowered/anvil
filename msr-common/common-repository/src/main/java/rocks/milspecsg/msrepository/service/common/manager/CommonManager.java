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

package rocks.milspecsg.msrepository.service.common.manager;

import com.google.inject.Inject;
import rocks.milspecsg.msrepository.api.component.Component;
import rocks.milspecsg.msrepository.api.config.ConfigKeys;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;
import rocks.milspecsg.msrepository.api.manager.Manager;
import rocks.milspecsg.msrepository.api.manager.annotation.H2Component;
import rocks.milspecsg.msrepository.api.manager.annotation.JsonComponent;
import rocks.milspecsg.msrepository.api.manager.annotation.MariaDBComponent;
import rocks.milspecsg.msrepository.api.manager.annotation.MongoDBComponent;
import rocks.milspecsg.msrepository.api.manager.annotation.NitriteComponent;
import rocks.milspecsg.msrepository.api.manager.annotation.XodusComponent;

import java.util.Locale;
import java.util.Objects;

public abstract class CommonManager<C extends Component<?, ?, ?>> implements Manager<C> {

    protected ConfigurationService configurationService;

    protected CommonManager(ConfigurationService configurationService) {
        this.configurationService = configurationService;
        configurationService.addConfigLoadedListener(this::configLoaded);
    }

    @Inject(optional = true)
    private C defaultComponent;

    @Inject(optional = true)
    @H2Component
    private C h2Component;

    @Inject(optional = true)
    @JsonComponent
    private C jsonComponent;

    @Inject(optional = true)
    @MariaDBComponent
    private C mariaComponent;

    @Inject(optional = true)
    @MongoDBComponent
    private C mongoComponent;

    @Inject(optional = true)
    @NitriteComponent
    private C nitriteComponent;

    @Inject(optional = true)
    @XodusComponent
    private C xodusComponent;

    private C currentComponent;

    private void configLoaded(Object plugin) {
        if (defaultComponent != null) {
            currentComponent = defaultComponent;
            return;
        }
        String dataStoreName = configurationService.getConfigString(ConfigKeys.DATA_STORE_NAME);
        try {
            switch (dataStoreName.toLowerCase(Locale.ENGLISH)) {
                case "h2":
                    currentComponent = Objects.requireNonNull(h2Component);
                    break;
                case "json":
                    currentComponent = Objects.requireNonNull(jsonComponent);
                    break;
                case "mariadb":
                    currentComponent = Objects.requireNonNull(mariaComponent);
                    break;
                case "mongodb":
                    currentComponent = Objects.requireNonNull(mongoComponent);
                    break;
                case "nitrite":
                    currentComponent = Objects.requireNonNull(nitriteComponent);
                    break;
                case "xodus":
                    currentComponent = Objects.requireNonNull(xodusComponent);
                    break;
                default:
                    throw new IllegalStateException("Invalid DataStoreName");
            }
        } catch (IllegalStateException e) {
            String message = "MSRepository: Could not find requested data store: \"" + dataStoreName + "\". Did you bind it correctly?";
            System.err.println(message);
            throw new IllegalStateException(message, e);
        }
    }

    @Override
    public C getPrimaryComponent() {
        try {
            return Objects.requireNonNull(currentComponent);
        } catch (RuntimeException e){
            String message = "MSRepository: DataStoreName has not been loaded yet!";
            System.err.println(message);
            throw new IllegalStateException(message, e);
        }
    }
}
