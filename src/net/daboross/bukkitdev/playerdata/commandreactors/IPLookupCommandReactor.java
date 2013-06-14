package net.daboross.bukkitdev.playerdata.commandreactors;

import java.util.HashSet;
import java.util.Set;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.CommandExecutorBase;
import net.daboross.bukkitdev.playerdata.IPLogin;
import net.daboross.bukkitdev.playerdata.PData;
import net.daboross.bukkitdev.playerdata.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author daboross
 */
public class IPLookupCommandReactor implements CommandExecutorBase.CommandReactor {

    private final PlayerData playerDataMain;

    public IPLookupCommandReactor(PlayerData playerDataMain) {
        this.playerDataMain = playerDataMain;
    }

    public void runCommand(CommandSender sender, Command mainCommand, String mainCommandLabel, String subCommand, String subCommandLabel,
            String[] subCommandArgs, CommandExecutorBase.CommandExecutorBridge executorBridge) {
        if (subCommandArgs.length < 1) {
            sender.sendMessage(ColorList.ILLEGALARGUMENT + "Must Provide A Player!");
            sender.sendMessage(executorBridge.getHelpMessage(subCommandLabel, mainCommandLabel));
            return;
        }
        String playerNameGiven = PlayerData.getCombinedString(subCommandArgs, 0);
        PData pData = playerDataMain.getHandler().getPData(playerNameGiven);
        if (pData == null) {
            sender.sendMessage(ColorList.ERROR + "Player '" + ColorList.ERROR_ARGS + playerNameGiven + ColorList.ERROR + "' not found!");
            return;
        }
        sender.sendMessage(ColorList.MAIN + "Different IP's used by " + pData.userName());
        Set<String> ipList = new HashSet<String>();
        for (IPLogin ipl : pData.logIns()) {
            String ip = ipl.ip();
            String[] ipSplit = ip.split(":")[0].split("/");
            ip = ipSplit[ipSplit.length - 1];
            if (!ip.equals("Unknown")) {
                if (!ipList.contains(ip)) {
                    ipList.add(ip);
                    sender.sendMessage(ColorList.NUMBER + ip);
                }
            }
        }
        if (ipList.isEmpty()) {
            sender.sendMessage(ColorList.ERROR + "No Known IPs");
        }
    }
}
