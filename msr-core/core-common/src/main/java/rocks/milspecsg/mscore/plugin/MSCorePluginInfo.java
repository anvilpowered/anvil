/*
 *   MSRepository - MilSpecSG
 *   Copyright (C) 2019 Cableguy20
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

package rocks.milspecsg.mscore.plugin;

import com.google.inject.Inject;
import rocks.milspecsg.msrepository.PluginInfo;
import rocks.milspecsg.msrepository.api.tools.resultbuilder.StringResult;

public class MSCorePluginInfo<TString, TCommandSource> implements PluginInfo<TString> {
    public static final String id = "mscore";
    public static final String name = "MSCore";
    public static final String version = "$modVersion";
    public static final String description = "Core plugin";
    public static final String url = "https://github.com/MilSpecSG/MSRepository";
    public static final String authors = "Cableguy20";
    public TString pluginPrefix;

    @Inject
    public void setPluginPrefix(StringResult<TString, TCommandSource> stringResult) {
        pluginPrefix = stringResult.builder().blue().append("[").aqua().append(name).blue().append("] ").build();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getURL() {
        return url;
    }

    @Override
    public String getAuthors() {
        return authors;
    }

    @Override
    public TString getPrefix() {
        return pluginPrefix;
    }
}
