package net.daboross.bukkitdev.playerdata.subcommandhandlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import net.daboross.bukkitdev.commandexecutorbase.ArrayHelpers;
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
public class ViewInfoCommandHandler implements SubCommandHandler {

    private final PlayerData playerDataMain;

    public ViewInfoCommandHandler(PlayerData playerDataMain) {
        this.playerDataMain = playerDataMain;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, SubCommand subCommand, String subCommandLabel, String[] subCommandArgs) {
        if (subCommandArgs.length < 1) {
            sender.sendMessage(ColorList.ERR + "Please specify a player");
            sender.sendMessage(subCommand.getHelpMessage(baseCommandLabel, subCommandLabel));
            return;
        }
        String givenPlayerName = PlayerData.getCombinedString(subCommandArgs, 0);
        PData pData = playerDataMain.getHandler().getPData(givenPlayerName);
        if (pData == null) {
            sender.sendMessage(ColorList.ERR + "Player " + ColorList.ERR_ARGS + givenPlayerName + ColorList.ERR + " not found");
            return;
        }
        sender.sendMessage(ColorList.TOP_SEPERATOR + " -- " + ColorList.TOP + "Info for " + ColorList.NAME + pData.userName() + ColorList.TOP_SEPERATOR + " --");
        ArrayList<String> linesToSend = new ArrayList<String>();
        linesToSend.add(ColorList.REG + "DisplayName: " + ColorList.NAME + pData.nickName());
        if (pData.isOnline()) {
            linesToSend.add(ColorList.NAME + pData.userName() + ColorList.REG + " is online");
            List<IPLogin> logIns = pData.logIns();
            linesToSend.add(ColorList.NAME + pData.userName() + ColorList.REG + " has been online " + ColorList.DATA + PlayerData.getFormattedDate(System.currentTimeMillis() - logIns.get(logIns.size() - 1).time()));
        } else {
            linesToSend.add(ColorList.NAME + pData.userName() + ColorList.REG + " is not online");
            linesToSend.add(ColorList.NAME + pData.userName() + ColorList.REG + " was last seen " + ColorList.DATA + PlayerData.getFormattedDate(System.currentTimeMillis() - pData.lastSeen()) + ColorList.REG + " ago");
        }
        linesToSend.add(ColorList.REG + "Times logged into " + ColorList.SERVER + Bukkit.getServerName() + ColorList.REG + ": " + ColorList.DATA + pData.logIns().size());
        linesToSend.add(ColorList.REG + "Times logged out of " + ColorList.SERVER + Bukkit.getServerName() + ColorList.REG + ": " + ColorList.DATA + pData.logOuts().size());
        linesToSend.add(ColorList.REG + "Time played on " + ColorList.SERVER + Bukkit.getServerName() + ColorList.REG + ": " + ColorList.DATA + PlayerData.getFormattedDate(pData.timePlayed()));
        linesToSend.add(ColorList.REG + "First time on " + ColorList.SERVER + Bukkit.getServerName() + ColorList.REG + " was  " + ColorList.DATA + PlayerData.getFormattedDate(System.currentTimeMillis() - pData.getFirstLogIn().time()) + ColorList.REG + " ago");
        linesToSend.add(ColorList.REG + "First time on " + ColorList.SERVER + Bukkit.getServerName() + ColorList.REG + " was  " + ColorList.DATA + new Date(pData.getFirstLogIn().time()));
        if (PlayerData.isVaultLoaded()) {
            linesToSend.add(ColorList.NAME + pData.userName() + ColorList.REG + " is currently " + ColorList.DATA + ArrayHelpers.combinedWithSeperator(pData.getGroups(), ", "));
        }
        PDataHandler pdh = playerDataMain.getPDataHandler();
        for (Data d : pData.getData()) {
            linesToSend.addAll(Arrays.asList(pdh.getDisplayData(d, false)));
        }
        sender.sendMessage(linesToSend.toArray(new String[0]));
    }
}
