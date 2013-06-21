package net.daboross.bukkitdev.playerdata.commandreactors;

import java.util.ArrayList;
import java.util.List;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.CommandExecutorBase;
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
public class ListPlayersByFirstJoinCommandReactor implements SubCommandHandler {

    private final PlayerData playerDataMain;

    public ListPlayersByFirstJoinCommandReactor(PlayerData playerDataMain) {
        this.playerDataMain = playerDataMain;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, SubCommand subCommand, String subCommandLabel, String[] subCommandArgs) {
        if (subCommandArgs.length > 1) {
            sender.sendMessage(ColorList.MAIN + "Please Use Only 1 Number After " + ColorList.CMD + "/" + baseCommandLabel + ColorList.SUBCMD + " " + subCommandLabel);
        }
        int pageNumber;
        if (subCommandArgs.length == 0) {
            pageNumber = 1;
        } else {
            try {
                pageNumber = Integer.valueOf(subCommandArgs[0]);
            } catch (Exception e) {
                sender.sendMessage(ColorList.ERROR_ARGS + subCommandArgs[0] + ColorList.ERROR + " is not an integer.");
                sender.sendMessage(subCommand.getHelpMessage(baseCommandLabel, subCommandLabel));
                return;
            }
            if (pageNumber == 0) {
                sender.sendMessage(ColorList.ERROR_ARGS + subCommandArgs[0] + ColorList.ERROR + " is not a non-0 integer.");
                return;
            } else if (pageNumber < 0) {
                sender.sendMessage(ColorList.ERROR_ARGS + subCommandArgs[0] + ColorList.ERROR + " is not a positive integer.");
                return;
            }
        }
        int pageNumberReal = pageNumber - 1;
        List<PData> pDataList = playerDataMain.getPDataHandler().getAllPDatasFirstJoin();
        ArrayList<String> messagesToSend = new ArrayList<String>();
        messagesToSend.add(ColorList.TOP_OF_LIST_SEPERATOR + " --" + ColorList.TOP_OF_LIST + " Player List " + ColorList.TOP_OF_LIST_SEPERATOR + "--" + ColorList.TOP_OF_LIST + " Page " + ColorList.NUMBER + pageNumber + ColorList.TOP_OF_LIST + "/" + ColorList.NUMBER + ((pDataList.size() / 6) + (pDataList.size() % 6 == 0 ? 0 : 1)) + ColorList.TOP_OF_LIST_SEPERATOR + " --");
        for (int i = (pageNumberReal * 6); i < ((pageNumberReal + 1) * 6) && i < pDataList.size(); i++) {
            PData current = pDataList.get(i);
            messagesToSend.add(ColorList.NAME + current.userName() + ColorList.MAIN + " was first seen " + ColorList.NUMBER + PlayerData.getFormattedDate(System.currentTimeMillis() - current.getFirstLogIn().time()) + ColorList.MAIN + " ago.");
        }
        if (pageNumberReal+1 < (pDataList.size() / 6.0)) {
            messagesToSend.add(ColorList.MAIN + "To View The Next Page, Type: " + ColorList.CMD + "/" + baseCommandLabel + ColorList.SUBCMD + " " + subCommandLabel + ColorList.ARGS + " " + (pageNumber + 1));
        }
        sender.sendMessage(messagesToSend.toArray(new String[0]));
    }
}
