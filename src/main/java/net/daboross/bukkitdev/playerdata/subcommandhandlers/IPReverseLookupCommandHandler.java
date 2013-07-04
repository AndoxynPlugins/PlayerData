/*
 * Author: Dabo Ross
 * Website: www.daboross.net
 * Email: daboross@daboross.net
 */
package net.daboross.bukkitdev.playerdata.subcommandhandlers;

import java.util.ArrayList;
import java.util.List;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.SubCommandHandler;
import net.daboross.bukkitdev.playerdata.api.LoginData;
import net.daboross.bukkitdev.playerdata.api.PlayerData;
import net.daboross.bukkitdev.playerdata.api.PlayerHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author daboross
 */
public class IPReverseLookupCommandHandler implements SubCommandHandler {

    private final PlayerHandler playerHandler;

    public IPReverseLookupCommandHandler(PlayerHandler playerHandler) {
        this.playerHandler = playerHandler;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, SubCommand subCommand, String subCommandLabel, String[] subCommandArgs) {
        if (subCommandArgs.length < 1) {
            sender.sendMessage(ColorList.ERR + "Please specify an IP");
            sender.sendMessage(subCommand.getHelpMessage(baseCommandLabel, subCommandLabel));
            return;
        }
        if (subCommandArgs.length > 1) {
            sender.sendMessage(ColorList.ERR + "To many arguments");
            sender.sendMessage(subCommand.getHelpMessage(baseCommandLabel, subCommandLabel));
        }
        List<String> usersList = new ArrayList<String>();
        for (PlayerData player : playerHandler.getAllPlayerDatas()) {
            for (LoginData login : player.getAllLogins()) {
                String ip = login.getIP();
                String[] ipSplit = ip.split(":")[0].split("/");
                ip = ipSplit[ipSplit.length - 1];
                if (ip.equalsIgnoreCase(subCommandArgs[0])) {
                    usersList.add(player.getUsername());
                    break;
                }
            }
        }
        if (usersList.isEmpty()) {
            sender.sendMessage(ColorList.ERR + "No players found who have used the IP " + ColorList.ERR_ARGS + subCommandArgs[0]);
        } else {
            StringBuilder builder = new StringBuilder(usersList.get(0));
            for (int i = 1; i < usersList.size(); i++) {
                builder.append(", ").append(usersList.get(i));
            }
            sender.sendMessage(ColorList.TOP_SEPERATOR + " -- " + ColorList.TOP + "players who have used the IP '" + ColorList.DATA + subCommandArgs[0] + ColorList.TOP + "'" + ColorList.TOP_SEPERATOR + " --");
            sender.sendMessage(builder.toString());
        }
    }
}
