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
                    sender.sendMessage(ColorList.MAIN + "First Ten Possible Auto Completes for " + ColorList.NAME + args[0] + ColorList.MAIN + ":");
                } else {
                    sender.sendMessage(ColorList.MAIN + "Possible Auto Completes for " + ColorList.NAME + args[0] + ColorList.MAIN + ":");
                }
                for (int i = 0; i < users.length && i < 10; i++) {
                    sender.sendMessage(users[i]);
                }
                sender.sendMessage(ColorList.MAIN + "Total Auto Completes Found: " + ColorList.NUMBER + users.length);
            } else {
                sender.sendMessage(ColorList.MAIN + "Please Specify A Player!");
                sender.sendMessage(ColorList.CMD + "/" + label + ColorList.ARGS + " <PartialUsername>");
            }
            return true;
        }
        return false;
    }
}
