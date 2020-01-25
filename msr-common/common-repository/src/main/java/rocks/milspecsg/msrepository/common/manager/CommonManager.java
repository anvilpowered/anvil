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

package rocks.milspecsg.msrepository.common.manager;

import com.google.inject.Inject;
import rocks.milspecsg.msrepository.api.MSRepository;
import rocks.milspecsg.msrepository.api.component.Component;
import rocks.milspecsg.msrepository.api.data.key.Keys;
import rocks.milspecsg.msrepository.api.data.registry.Registry;
import rocks.milspecsg.msrepository.api.manager.Manager;
import rocks.milspecsg.msrepository.api.manager.annotation.MariaDBComponent;
import rocks.milspecsg.msrepository.api.manager.annotation.MongoDBComponent;
import rocks.milspecsg.msrepository.api.manager.annotation.XodusComponent;

import java.util.Locale;
import java.util.Objects;

public abstract class CommonManager<C extends Component<?, ?>> implements Manager<C> {

    protected Registry registry;

    protected CommonManager(Registry registry) {
        this.registry = registry;
        registry.addRegistryLoadedListener(this::registryLoaded);
    }

    @Inject(optional = true)
    @MariaDBComponent
    private C mariaComponent;

    @Inject(optional = true)
    @MongoDBComponent
    private C mongoComponent;

    @Inject(optional = true)
    @XodusComponent
    private C xodusComponent;

    private C currentComponent;

    private void registryLoaded(Object plugin) {
        currentComponent = null;
    }

    private void loadComponent() {
        String dataStoreName = MSRepository.resolveForSharedEnvironment(Keys.DATA_STORE_NAME, registry);
        try {
            switch (dataStoreName.toLowerCase(Locale.ENGLISH)) {
                case "mariadb":
                    currentComponent = Objects.requireNonNull(mariaComponent, "MariaDB component not bound for manager " + getClass().getName());
                    break;
                case "mongodb":
                    currentComponent = Objects.requireNonNull(mongoComponent, "MongoDB component not bound for manager " + getClass().getName());
                    break;
                case "xodus":
                    currentComponent = Objects.requireNonNull(xodusComponent, "Xodus component not bound for manager " + getClass().getName());
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
            if (currentComponent == null) {
                loadComponent();
            }
            return currentComponent;
        } catch (RuntimeException e) {
            String message = "MSRepository: DataStoreName has not been loaded yet! Make sure your Registry and ConfigurationService implementations are annotated with @Singleton!";
            System.err.println(message);
            throw new IllegalStateException(message, e);
        }
    }
}
