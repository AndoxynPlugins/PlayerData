/*
 * Copyright (C) 2013-2014 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.playerdata.commands;

import java.util.concurrent.Callable;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.commandexecutorbase.filters.ArgumentFilter;
import net.daboross.bukkitdev.playerdata.api.PlayerDataPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * @author daboross
 */
public class SaveAllCommand extends SubCommand {

    private final PlayerDataPlugin playerDataPlugin;

    public SaveAllCommand(PlayerDataPlugin playerDataPlugin) {
        super("save-all", true, "playerdata.admin", "Save All PlayerDatas");
        addCommandFilter(new ArgumentFilter(ArgumentFilter.ArgumentCondition.EQUALS, 0, ColorList.ERR + "Too many arguments"));
        this.playerDataPlugin = playerDataPlugin;
    }

    @Override
    public void runCommand(final CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        sender.sendMessage(ColorList.REG + "Saving data");
        Bukkit.getScheduler().runTaskAsynchronously(playerDataPlugin, new Runnable() {
            @Override
            public void run() {
                playerDataPlugin.getHandler().saveAllData();
                Bukkit.getScheduler().callSyncMethod(playerDataPlugin, new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        sender.sendMessage(ColorList.REG + "Done saving data");
                        return null;
                    }
                });
            }
        });
    }
}
