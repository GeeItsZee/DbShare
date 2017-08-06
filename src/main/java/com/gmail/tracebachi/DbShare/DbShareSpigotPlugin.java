/*
 * DbShare - Multiple HikariDataSource manager for Spigot
 * Copyright (C) 2017 tracebachi@gmail.com (GeeItsZee)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.gmail.tracebachi.DbShare;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author GeeItsZee (tracebachi@gmail.com)
 */
public class DbShareSpigotPlugin extends JavaPlugin
{
  @Override
  public void onEnable()
  {
    saveDefaultConfig();
    reloadConfig();

    List<DataSourceDetails> dataSourceDetailsList = new ArrayList<>();
    ConfigurationSection section = getConfig().getConfigurationSection("Databases");
    Set<String> sourceNames = section.getKeys(false);

    for (String sourceName : sourceNames)
    {
      String username = section.getString(sourceName + ".Username");
      String password = section.getString(sourceName + ".Password");
      String url = section.getString(sourceName + ".URL");

      dataSourceDetailsList.add(new DataSourceDetails(sourceName, username, password, url));
    }

    // Create the data sources
    DbShare instance = new DbShare();
    instance.createDataSources(dataSourceDetailsList, getLogger());

    // Set the DbShare instance
    DbShare.setInstance(instance);
  }

  @Override
  public void onDisable()
  {
    DbShare instance = DbShare.instance();
    if (instance != null)
    {
      // Unset the DbShare instance
      DbShare.setInstance(null);

      // Close and remove the data sources
      instance.closeAndRemoveDataSources(getLogger());
    }
  }
}
