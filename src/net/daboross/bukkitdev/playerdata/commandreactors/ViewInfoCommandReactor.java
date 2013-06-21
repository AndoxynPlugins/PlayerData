package net.daboross.bukkitdev.playerdata.commandreactors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.commandexecutorbase.SubCommandHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.daboross.bukkitdev.playerdata.Data;
import net.daboross.bukkitdev.playerdata.IPLogin;
import net.daboross.bukkitdev.playerdata.PData;
import net.daboross.bukkitdev.playerdata.PDataHandler;
import net.daboross.bukkitdev.playerdata.PlayerData;
import org.bukkit.Bukkit;

/**
 *
 * @author daboross
 */
public class ViewInfoCommandReactor implements SubCommandHandler {

    private final PlayerData playerDataMain;

    public ViewInfoCommandReactor(PlayerData playerDataMain) {
        this.playerDataMain = playerDataMain;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, SubCommand subCommand, String subCommandLabel, String[] subCommandArgs) {
        if (subCommandArgs.length < 1) {
            sender.sendMessage(ColorList.ILLEGALARGUMENT + "Must Provide A Player!");
            sender.sendMessage(subCommand.getHelpMessage(baseCommandLabel, subCommandLabel));
            return;
        }
        String givenPlayerName = PlayerData.getCombinedString(subCommandArgs, 0);
        PData pData = playerDataMain.getHandler().getPData(givenPlayerName);
        if (pData == null) {
            sender.sendMessage(ColorList.ERROR + "Player: " + ColorList.ERROR_ARGS + givenPlayerName + ColorList.ERROR + " not found!");
            return;
        }
        sender.sendMessage(ColorList.MAIN + "Info Avalible For " + ColorList.NAME + pData.userName() + ColorList.MAIN + ":");
        ArrayList<String> linesToSend = new ArrayList<String>();
        linesToSend.add(ColorList.MAIN + "Display Name: " + ColorList.NAME + pData.nickName());
        if (pData.isOnline()) {
            linesToSend.add(ColorList.NAME + pData.userName() + ColorList.MAIN + " is online");
            IPLogin[] logIns = pData.logIns();
            linesToSend.add(ColorList.NAME + pData.userName() + ColorList.MAIN + " has been online " + ColorList.NUMBER + PlayerData.getFormattedDate(System.currentTimeMillis() - logIns[logIns.length - 1].time()));
        } else {
            linesToSend.add(ColorList.NAME + pData.userName() + ColorList.MAIN + " is not online");
            linesToSend.add(ColorList.NAME + pData.userName() + ColorList.MAIN + " was last seen " + ColorList.NUMBER + PlayerData.getFormattedDate(System.currentTimeMillis() - pData.lastSeen()) + ColorList.MAIN + " ago");
        }
        linesToSend.add(ColorList.MAIN + "Times logged into " + ColorList.SERVERNAME + Bukkit.getServerName() + ColorList.MAIN + ": " + ColorList.NUMBER + pData.logIns().length);
        linesToSend.add(ColorList.MAIN + "Times logged out of " + ColorList.SERVERNAME + Bukkit.getServerName() + ColorList.MAIN + ": " + ColorList.NUMBER + pData.logOuts().length);
        linesToSend.add(ColorList.MAIN + "Time Played On " + ColorList.SERVERNAME + Bukkit.getServerName() + ColorList.MAIN + ": " + ColorList.NUMBER + PlayerData.getFormattedDate(pData.timePlayed()));
        linesToSend.add(ColorList.MAIN + "First Time On " + ColorList.SERVERNAME + Bukkit.getServerName() + ColorList.MAIN + " was  " + ColorList.NUMBER + PlayerData.getFormattedDate(System.currentTimeMillis() - pData.getFirstLogIn().time()) + ColorList.MAIN + " ago");
        linesToSend.add(ColorList.MAIN + "First Time On " + ColorList.SERVERNAME + Bukkit.getServerName() + ColorList.MAIN + " was  " + ColorList.NUMBER + new Date(pData.getFirstLogIn().time()));
        if (PlayerData.isVaultLoaded()) {
            linesToSend.add(ColorList.NAME + pData.userName() + ColorList.MAIN + " is currently " + ColorList.NUMBER + PlayerData.formatList(pData.getGroups()));
        }
        PDataHandler pdh = playerDataMain.getPDataHandler();
        for (Data d : pData.getData()) {
            linesToSend.addAll(Arrays.asList(pdh.getDisplayData(d, false)));
        }
        sender.sendMessage(linesToSend.toArray(new String[0]));
    }
}
