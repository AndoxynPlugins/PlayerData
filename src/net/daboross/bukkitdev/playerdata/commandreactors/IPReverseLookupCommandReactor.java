package net.daboross.bukkitdev.playerdata.commandreactors;

import java.util.ArrayList;
import java.util.List;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.CommandExecutorBase;
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
public class IPReverseLookupCommandReactor implements SubCommandHandler {

    private final PlayerData playerDataMain;

    public IPReverseLookupCommandReactor(PlayerData playerDataMain) {
        this.playerDataMain = playerDataMain;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel,SubCommand subCommand, String subCommandLabel, String[] subCommandArgs) {
        if (subCommandArgs.length < 1) {
            sender.sendMessage(ColorList.ILLEGALARGUMENT + "Must Provide an IP!");
            sender.sendMessage(subCommand.getHelpMessage(baseCommandLabel, subCommandLabel));
            return;
        }
        if (subCommandArgs.length > 1) {
            sender.sendMessage(ColorList.ILLEGALARGUMENT + "To Many Arguments!");
            sender.sendMessage(subCommand.getHelpMessage(baseCommandLabel, subCommandLabel));
        }
        sender.sendMessage(ColorList.MAIN + "Looking for users who have used the IP: " + subCommandArgs[0]);
        List<String> usersList = new ArrayList<String>();
        for (PData pData : playerDataMain.getHandler().getAllPDatas()) {
            for (IPLogin login : pData.logIns()) {
                String ip = login.ip();
                String[] ipSplit = ip.split(":")[0].split("/");
                ip = ipSplit[ipSplit.length - 1];
                if (ip.equalsIgnoreCase(subCommandArgs[0])) {
                    usersList.add(pData.userName());
                    break;
                }
            }
        }
        if (usersList.isEmpty()) {
            sender.sendMessage(ColorList.ERROR + "No Players found who have used the IP: " + subCommandArgs[0]);
        } else {
            StringBuilder builder = new StringBuilder(usersList.get(0));
            for (int i = 1; i < usersList.size(); i++) {
                builder.append(", ").append(usersList.get(i));
            }
            sender.sendMessage(ColorList.MAIN + "Different Player's who have used the IP: " + subCommandArgs[0]);
            sender.sendMessage(builder.toString());

        }
    }
}
