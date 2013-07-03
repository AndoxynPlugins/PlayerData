package net.daboross.bukkitdev.playerdata.subcommandhandlers;

import java.util.ArrayList;
import java.util.List;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.SubCommandHandler;
import net.daboross.bukkitdev.playerdata.PData;
import net.daboross.bukkitdev.playerdata.PlayerDataBukkit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author daboross
 */
public class ListPlayersCommandReactor implements SubCommandHandler {

    private final PlayerDataBukkit playerDataMain;

    public ListPlayersCommandReactor(PlayerDataBukkit playerDataMain) {
        this.playerDataMain = playerDataMain;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, SubCommand subCommand, String subCommandLabel, String[] subCommandArgs) {
        if (subCommandArgs.length > 1) {
            sender.sendMessage(ColorList.ERR + "Please use only one number after " + ColorList.CMD + "/" + baseCommandLabel + ColorList.SUBCMD + " " + subCommandLabel);
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
            messagesToSend.add(ColorList.NAME + current.getUsername() + ColorList.REG + " was last seen " + ColorList.DATA + PlayerDataBukkit.getFormattedDate(current.isOnline() ? 0 : System.currentTimeMillis() - current.getLastSeen()) + ColorList.REG + " ago.");
        }

        if (pageNumberReal + 1 < (pDataList.size() / 6.0)) {
            messagesToSend.add(ColorList.REG + "To view the next page type '" + ColorList.CMD + "/" + baseCommandLabel + ColorList.SUBCMD + " " + subCommandLabel + ColorList.ARGS + " " + (pageNumber + 1) + ColorList.REG + "'");
        }
        sender.sendMessage(messagesToSend.toArray(new String[0]));
    }
}
