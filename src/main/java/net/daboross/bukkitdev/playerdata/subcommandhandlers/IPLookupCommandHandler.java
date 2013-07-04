/*
 * Author: Dabo Ross
 * Website: www.daboross.net
 * Email: daboross@daboross.net
 */
package net.daboross.bukkitdev.playerdata.subcommandhandlers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.SubCommandHandler;
import net.daboross.bukkitdev.playerdata.api.LoginData;
import net.daboross.bukkitdev.playerdata.api.PlayerData;
import net.daboross.bukkitdev.playerdata.api.PlayerHandler;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.ArrayHelpers;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author daboross
 */
public class IPLookupCommandHandler implements SubCommandHandler {

    private final PlayerHandler playerHandler;

    public IPLookupCommandHandler(PlayerHandler playerHandler) {
        this.playerHandler = playerHandler;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, SubCommand subCommand, String subCommandLabel, String[] subCommandArgs) {
        if (subCommandArgs.length < 1) {
            sender.sendMessage(ColorList.ERR + "Please specify a player");
            sender.sendMessage(subCommand.getHelpMessage(baseCommandLabel, subCommandLabel));
            return;
        }
        String playerNameGiven = ArrayHelpers.combinedWithSeperator(subCommandArgs, " ");
        PlayerData pd = playerHandler.getPlayerDataPartial(playerNameGiven);
        if (pd == null) {
            sender.sendMessage(ColorList.ERR + "Player '" + ColorList.ERR_ARGS + playerNameGiven + ColorList.ERR + "' not found");
            return;
        }
        List<? extends LoginData> logins = pd.getAllLogins();
        if (logins.isEmpty()) {
            sender.sendMessage(ColorList.ERR + "No know IPs for " + ColorList.ERR_ARGS + pd.getUsername());
        } else if (logins.size() == 1) {
            sender.sendMessage(ColorList.REG + "Different IPs used by " + pd.getUsername());
            sender.sendMessage(ColorList.DATA + logins.get(0).getIP());
        } else {
            Set<String> ipList = new HashSet<String>();
            for (LoginData ld : logins) {
                String ip = ld.getIP();
                String[] ipSplit = ip.split(":")[0].split("/");
                ip = ipSplit[ipSplit.length - 1];
                if (!ip.equals("Unknown")) {
                    if (!ipList.contains(ip)) {
                        ipList.add(ip);
                        sender.sendMessage(ColorList.DATA + ip);
                    }
                }
            }
        }
    }
}
