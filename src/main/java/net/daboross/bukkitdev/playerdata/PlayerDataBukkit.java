/*
 * Copyright (C) 2013 Dabo Ross <http://www.daboross.net/>
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
package net.daboross.bukkitdev.playerdata;

import java.io.IOException;
import java.util.logging.Level;
import net.daboross.bukkitdev.playerdata.api.PlayerDataPlugin;
import net.daboross.bukkitdev.playerdata.api.PlayerDataStatic;
import net.daboross.bukkitdev.playerdata.api.PlayerHandler;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

public final class PlayerDataBukkit extends JavaPlugin implements PlayerDataPlugin {

    private PlayerHandlerImpl playerHandler;
    private boolean permissionLoaded = false;
    private Permission permissionHandler;
    private boolean enabledSuccessfully = true;

    @Override
    public void onEnable() {
        PlayerDataStatic.setPlayerDataPlugin(this);
        MetricsLite metrics = null;
        try {
            metrics = new MetricsLite(this);
        } catch (IOException ex) {
            getLogger().log(Level.WARNING, "Unable to create Metrics", ex);
        }
        if (metrics != null) {
            metrics.start();
        }
        PluginManager pm = this.getServer().getPluginManager();
        if (pm.isPluginEnabled("Vault")) {
            RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
            permissionHandler = rsp.getProvider();
            if (permissionHandler == null) {
                getLogger().log(Level.INFO, "Vault found. Permission not found.");
            } else {
                permissionLoaded = true;
                getLogger().log(Level.INFO, "Vault found. Permission found.");
            }
        } else {
            getLogger().log(Level.INFO, "Vault not found.");
        }
        playerHandler = new PlayerHandlerImpl(this);
        enabledSuccessfully = playerHandler.init();
        if (!enabledSuccessfully) {
            pm.disablePlugin(this);
            PlayerDataStatic.setPlayerDataPlugin(null);
            permissionHandler = null;
            playerHandler = null;
            permissionLoaded = false;
            return;
        }
        PluginCommand playerdata = getCommand("pd");
        if (playerdata != null) {
            new PlayerDataCommandHandler(this, playerHandler).registerCommand(playerdata);
        }
        PluginCommand getusername = getCommand("gu");
        if (getusername != null) {
            getusername.setExecutor(new GetUsernameCommand(playerHandler));
        }
        pm.registerEvents(new PlayerDataEventListener(playerHandler), this);
        getLogger().info("PlayerData Load Sucessful");
    }

    @Override
    public void onDisable() {
        if (enabledSuccessfully) {
            playerHandler.endServer();
            playerHandler.saveAllData();
        }
        PlayerDataStatic.setPlayerDataPlugin(null);
        permissionHandler = null;
        playerHandler = null;
        permissionLoaded = false;
    }

    @Override
    public PlayerHandler getHandler() {
        return playerHandler;
    }

    @Override
    public boolean isPermissionLoaded() {
        return permissionLoaded;
    }

    @Override
    public Permission getPermission() {
        return permissionHandler;
    }

    @Override
    public int getAPIVersion() {
        return PlayerDataStatic.getAPIVersion();
    }

    @Override
    public boolean isEnabledSuccessfully() {
        return enabledSuccessfully;
    }
}