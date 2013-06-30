package net.daboross.bukkitdev.playerdata.subcommandhandlers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.commandexecutorbase.SubCommandHandler;
import net.daboross.bukkitdev.playerdata.IPLogin;
import net.daboross.bukkitdev.playerdata.PData;
import net.daboross.bukkitdev.playerdata.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author daboross
 */
public class IPLookupCommandHandler implements SubCommandHandler {

    private final PlayerData playerDataMain;

    public IPLookupCommandHandler(PlayerData playerDataMain) {
        this.playerDataMain = playerDataMain;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, SubCommand subCommand, String subCommandLabel, String[] subCommandArgs) {
        if (subCommandArgs.length < 1) {
            sender.sendMessage(ColorList.ERR + "Please specify a player");
            sender.sendMessage(subCommand.getHelpMessage(baseCommandLabel, subCommandLabel));
            return;
        }
        String playerNameGiven = PlayerData.getCombinedString(subCommandArgs, 0);
        PData pData = playerDataMain.getHandler().getPData(playerNameGiven);
        if (pData == null) {
            sender.sendMessage(ColorList.ERR + "Player '" + ColorList.ERR_ARGS + playerNameGiven + ColorList.ERR + "' not found");
            return;
        }
        List<IPLogin> ipLogins = pData.logIns();
        if (ipLogins.isEmpty()) {
            sender.sendMessage(ColorList.ERR + "No know IPs for " + ColorList.ERR_ARGS + pData.userName());
        } else if (ipLogins.size() == 1) {
            sender.sendMessage(ColorList.REG + "Different IPs used by " + pData.userName());
            sender.sendMessage(ColorList.DATA + ipLogins.get(0).ip());
        } else {
            Set<String> ipList = new HashSet<String>();
            for (IPLogin ipl : ipLogins) {
                String ip = ipl.ip();
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
