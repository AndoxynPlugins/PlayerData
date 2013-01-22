package net.daboross.bukkitdev.playerdata;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author daboross
 */
public class PossibleUserNames implements CommandExecutor {

    private PlayerData instance;

    protected PossibleUserNames(PlayerData pd) {
        instance = pd;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("gu")) {
            if (args.length > 0) {
                String[] users = instance.getPDataHandler().getPossibleUsernames(args[0]);
                if (users.length > 10) {
                    sender.sendMessage(ColorL.MAIN + "First Ten Possible Auto Completes for " + ColorL.NAME + args[0] + ColorL.MAIN + ":");
                } else {
                    sender.sendMessage(ColorL.MAIN + "Possible Auto Completes for " + ColorL.NAME + args[0] + ColorL.MAIN + ":");
                }
                for (int i = 0; i < users.length && i < 10; i++) {
                    sender.sendMessage(users[i]);
                }
                sender.sendMessage(ColorL.MAIN + "Total Auto Completes Found: " + ColorL.NUMBER + users.length);
            } else {
                sender.sendMessage(ColorL.MAIN + "Please Specify A Player!");
                sender.sendMessage(ColorL.CMD + "/" + label + ColorL.ARGS + " <PartialUsername>");
            }
            return true;
        }
        return false;
    }
}
