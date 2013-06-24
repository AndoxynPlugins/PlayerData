package net.daboross.bukkitdev.playerdata.commandreactors;

import java.util.ArrayList;
import java.util.List;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.commandexecutorbase.SubCommandHandler;
import net.daboross.bukkitdev.playerdata.PData;
import net.daboross.bukkitdev.playerdata.PlayerData;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author daboross
 */
public class ListPlayersCommandReactor implements SubCommandHandler {

    private final PlayerData playerDataMain;

    public ListPlayersCommandReactor(PlayerData playerDataMain) {
        this.playerDataMain = playerDataMain;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, SubCommand subCommand, String subCommandLabel, String[] subCommandArgs) {
        if (subCommandArgs.length > 1) {
            sender.sendMessage(ColorList.ERR + "Please use only one number after '" + ColorList.CMD + "/" + baseCommandLabel + ColorList.SUBCMD + " " + subCommandLabel + ColorList.ERR + "'");
        }
        int pageNumber;
        if (subCommandArgs.length == 0) {
            pageNumber = 1;
        } else {
            try {
                pageNumber = Integer.valueOf(subCommandArgs[0]);
            } catch (NumberFormatException nfe) {
                sender.sendMessage(ColorList.ERR_ARGS + subCommandArgs[0] + ColorList.ERR + " is not an integer.");
                sender.sendMessage(subCommand.getHelpMessage(baseCommandLabel, subCommandLabel));
                return;
            }
            if (pageNumber == 0) {
                sender.sendMessage(ColorList.ERR_ARGS + subCommandArgs[0] + ColorList.ERR + " is not a non-0 integer.");
                return;
            } else if (pageNumber < 0) {
                sender.sendMessage(ColorList.ERR_ARGS + subCommandArgs[0] + ColorList.ERR + " is not a positive integer.");
                return;
            }
        }
        int pageNumberReal = pageNumber - 1;
        List<PData> pDataList = playerDataMain.getPDataHandler().getAllPDatas();
        ArrayList<String> messagesToSend = new ArrayList<String>();
        messagesToSend.add(ColorList.TOP_SEPERATOR + " --" + ColorList.TOP + " Player List " + ColorList.TOP_SEPERATOR + "--" + ColorList.TOP + " Page " + ColorList.DATA + pageNumber + ColorList.TOP + "/" + ColorList.DATA + ((pDataList.size() / 6) + (pDataList.size() % 6 == 0 ? 0 : 1)) + ColorList.TOP_SEPERATOR + " --");

        for (int i = pageNumberReal * 6; i < (pageNumberReal + 1) * 6 && i < pDataList.size(); i++) {
            PData current = pDataList.get(i);
            messagesToSend.add(ColorList.NAME + current.userName() + ColorList.REG + " was last seen " + ColorList.DATA + PlayerData.getFormattedDate(current.isOnline() ? 0 : System.currentTimeMillis() - current.lastSeen()) + ColorList.REG + " ago.");
        }

        if (pageNumberReal + 1 < (pDataList.size() / 6.0)) {
            messagesToSend.add(ColorList.REG + "To view the next page type '" + ColorList.CMD + "/" + baseCommandLabel + ColorList.SUBCMD + " " + subCommandLabel + ColorList.ARGS + " " + (pageNumber + 1) + ColorList.REG + "'");
        }
        sender.sendMessage(messagesToSend.toArray(new String[0]));
    }
}
