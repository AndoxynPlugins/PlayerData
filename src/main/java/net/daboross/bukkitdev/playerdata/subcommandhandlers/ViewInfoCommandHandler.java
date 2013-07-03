package net.daboross.bukkitdev.playerdata.subcommandhandlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.ArrayHelpers;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.SubCommandHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.daboross.bukkitdev.playerdata.Data;
import net.daboross.bukkitdev.playerdata.IPLogin;
import net.daboross.bukkitdev.playerdata.PData;
import net.daboross.bukkitdev.playerdata.PDataHandler;
import net.daboross.bukkitdev.playerdata.PlayerDataBukkit;
import net.daboross.bukkitdev.playerdata.api.LoginData;
import org.bukkit.Bukkit;

/**
 *
 * @author daboross
 */
public class ViewInfoCommandHandler implements SubCommandHandler {

    private final PlayerDataBukkit playerDataMain;

    public ViewInfoCommandHandler(PlayerDataBukkit playerDataMain) {
        this.playerDataMain = playerDataMain;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, SubCommand subCommand, String subCommandLabel, String[] subCommandArgs) {
        if (subCommandArgs.length < 1) {
            sender.sendMessage(ColorList.ERR + "Please specify a player");
            sender.sendMessage(subCommand.getHelpMessage(baseCommandLabel, subCommandLabel));
            return;
        }
        String givenPlayerName = PlayerDataBukkit.getCombinedString(subCommandArgs, 0);
        PData pData = playerDataMain.getHandler().getPData(givenPlayerName);
        if (pData == null) {
            sender.sendMessage(ColorList.ERR + "Player " + ColorList.ERR_ARGS + givenPlayerName + ColorList.ERR + " not found");
            return;
        }
        sender.sendMessage(ColorList.TOP_SEPERATOR + " -- " + ColorList.TOP + "Info for " + ColorList.NAME + pData.getUsername() + ColorList.TOP_SEPERATOR + " --");
        ArrayList<String> linesToSend = new ArrayList<String>();
        linesToSend.add(ColorList.REG + "DisplayName: " + ColorList.NAME + pData.getDisplayname());
        if (pData.isOnline()) {
            linesToSend.add(ColorList.NAME + pData.getUsername() + ColorList.REG + " is online");
            List<? extends LoginData> logIns = pData.getAllLogins();
            linesToSend.add(ColorList.NAME + pData.getUsername() + ColorList.REG + " has been online " + ColorList.DATA + PlayerDataBukkit.getFormattedDate(System.currentTimeMillis() - logIns.get(logIns.size() - 1).getDate()));
        } else {
            linesToSend.add(ColorList.NAME + pData.getUsername() + ColorList.REG + " is not online");
            linesToSend.add(ColorList.NAME + pData.getUsername() + ColorList.REG + " was last seen " + ColorList.DATA + PlayerDataBukkit.getFormattedDate(System.currentTimeMillis() - pData.getLastSeen()) + ColorList.REG + " ago");
        }
        linesToSend.add(ColorList.REG + "Times logged into " + ColorList.SERVER + Bukkit.getServerName() + ColorList.REG + ": " + ColorList.DATA + pData.getAllLogins().size());
        linesToSend.add(ColorList.REG + "Times logged out of " + ColorList.SERVER + Bukkit.getServerName() + ColorList.REG + ": " + ColorList.DATA + pData.getAllLogouts().size());
        linesToSend.add(ColorList.REG + "Time played on " + ColorList.SERVER + Bukkit.getServerName() + ColorList.REG + ": " + ColorList.DATA + PlayerDataBukkit.getFormattedDate(pData.getTimePlayed()));
        linesToSend.add(ColorList.REG + "First time on " + ColorList.SERVER + Bukkit.getServerName() + ColorList.REG + " was  " + ColorList.DATA + PlayerDataBukkit.getFormattedDate(System.currentTimeMillis() - pData.getFirstLogIn().getDate()) + ColorList.REG + " ago");
        linesToSend.add(ColorList.REG + "First time on " + ColorList.SERVER + Bukkit.getServerName() + ColorList.REG + " was  " + ColorList.DATA + new Date(pData.getFirstLogIn().getDate()));
        if (PlayerDataBukkit.isVaultLoaded()) {
            linesToSend.add(ColorList.NAME + pData.getUsername() + ColorList.REG + " is currently " + ColorList.DATA + ArrayHelpers.combinedWithSeperator(pData.getGroups(), ", "));
        }
        PDataHandler pdh = playerDataMain.getPDataHandler();
        for (Data d : pData.getData()) {
            linesToSend.addAll(Arrays.asList(pdh.getDisplayData(d, false)));
        }
        sender.sendMessage(linesToSend.toArray(new String[0]));
    }
}
