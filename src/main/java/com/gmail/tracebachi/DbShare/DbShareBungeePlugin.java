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

import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author GeeItsZee (tracebachi@gmail.com)
 */
public class DbShareBungeePlugin extends Plugin
{
  private Configuration config;

  @Override
  public void onEnable()
  {
    // Reload the config
    reloadConfig();

    List<DataSourceDetails> dataSourceDetailsList = new ArrayList<>();
    Configuration section = config.getSection("Databases");
    Collection<String> sourceNames = section.getKeys();

    // Read the new data source details
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

  private void reloadConfig()
  {
    try
    {
      File file = saveResource(this, "config.yml", "config.yml");
      config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);

      if (config == null)
      {
        saveResource(this, "config.yml", "config.yml");
      }
    }
    catch (IOException e)
    {
      getLogger().severe("Failed to load configuration file.");
      e.printStackTrace();
    }
  }

  /**
   * Loads the resource from the JAR and saves it to the destination under the plugin's
   * data folder. By default, the destination file will not be replaced if it exists.
   * <p>
   * Source for the majority of this method can be found at:
   * https://www.spigotmc.org/threads/bungeecords-configuration-api.11214/#post-119017
   * <p>
   * Originally authored by: vemacs, Feb 15, 2014
   *
   * @param plugin Plugin that contains the resource in it's JAR.
   * @param resourceName Filename of the resource.
   * @param destinationName Filename of the destination.
   *
   * @return Destination File.
   */
  private static File saveResource(Plugin plugin, String resourceName, String destinationName)
  {
    File folder = plugin.getDataFolder();
    if (!folder.exists() && !folder.mkdir())
    {
      return null;
    }

    File destinationFile = new File(folder, destinationName);
    try
    {
      if (!destinationFile.exists())
      {
        if (destinationFile.createNewFile())
        {
          try (InputStream in = plugin.getResourceAsStream(resourceName);
            OutputStream out = new FileOutputStream(destinationFile))
          {
            ByteStreams.copy(in, out);
          }
        }
        else
        {
          return null;
        }
      }
      return destinationFile;
    }
    catch (IOException e)
    {
      e.printStackTrace();
      return null;
    }
  }
}
