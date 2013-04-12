package net.daboross.bukkitdev.playerdata.commandreactors;

import java.util.ArrayList;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.CommandExecutorBase;
import static net.daboross.bukkitdev.commandexecutorbase.CommandExecutorBase.CommandReactor;
import net.daboross.bukkitdev.playerdata.PData;
import net.daboross.bukkitdev.playerdata.PlayerData;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author daboross
 */
public class ListPlayersCommandReactor implements CommandReactor {

    private final PlayerData playerDataMain;

    public ListPlayersCommandReactor(PlayerData playerDataMain) {
        this.playerDataMain = playerDataMain;
    }

    public void runCommand(CommandSender sender, Command mainCommand, String mainCommandLabel, String subCommand, String subCommandLabel, String[] subCommandArgs, CommandExecutorBase.CommandExecutorBridge executorBridge) {
        if (subCommandArgs.length > 1) {
            sender.sendMessage(ColorList.MAIN + "Please Use Only 1 Number After " + ColorList.CMD + "/" + mainCommandLabel + ColorList.SUBCMD + " " + subCommandLabel);
        }
        int pageNumber;
        if (subCommandArgs.length == 0) {
            pageNumber = 1;
        } else {
            try {
                pageNumber = Integer.valueOf(subCommandArgs[0]);
            } catch (Exception e) {
                sender.sendMessage(ColorList.ERROR_ARGS + subCommandArgs[0] + ColorList.ERROR + " is not a number.");
                sender.sendMessage(executorBridge.getHelpMessage(subCommandLabel, mainCommandLabel));
                return;
            }
            if (pageNumber < 1) {
                sender.sendMessage(ColorList.ERROR_ARGS + subCommandArgs[0] + ColorList.ERROR + " is not a non-0 positive number.");
                return;
            }
        }
        PData[] pDataList = playerDataMain.getPDataHandler().getAllPDatas();
        ArrayList<String> messagesToSend = new ArrayList<String>();
        messagesToSend.add("");
        messagesToSend.add(ColorList.MAIN_DARK + "Player List, Page " + ColorList.NUMBER + pageNumber + ColorList.MAIN_DARK + ":");
        for (int i = ((pageNumber - 1) * 6); i < ((pageNumber - 1) * 6) + 6 & i < pDataList.length; i++) {
            PData current = pDataList[i];
            messagesToSend.add(ColorList.NAME + current.userName() + ColorList.MAIN + " was last seen " + ColorList.NUMBER + PlayerData.getFormattedDDate(current.isOnline() ? 0 : System.currentTimeMillis() - current.lastSeen()) + ColorList.MAIN + " ago.");
        }
        if (pageNumber < (pDataList.length / 6.0)) {
            messagesToSend.add(ColorList.MAIN_DARK + "To View The Next Page, Type: " + ColorList.CMD + "/" + mainCommandLabel + ColorList.SUBCMD + " " + subCommandLabel + ColorList.ARGS + " " + (pageNumber + 1));
        }
        sender.sendMessage(messagesToSend.toArray(new String[0]));
    }
}
