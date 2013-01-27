package net.daboross.bukkitdev.playerdata;

import java.util.concurrent.TimeUnit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * PlayerData Plugin Made By DaboRoss
 *
 * @author daboross
 */
public final class PlayerData extends JavaPlugin {

    private static PlayerData currentInstance;
    private PlayerDataCommandExecutor pDCE;
    private PlayerDataEventListener pDEL;
    private PDataHandler playerDataHandler;
    private PlayerDataHandler handler;

    /**
     *
     */
    @Override
    public void onEnable() {
        currentInstance = this;
        playerDataHandler = new PDataHandler(this);
        playerDataHandler.init();
        pDCE = new PlayerDataCommandExecutor(this);
        pDEL = new PlayerDataEventListener(this);

        PluginCommand pd = getCommand("pd");
        PluginCommand gu = getCommand("gu");
        if (pd != null) {
            pd.setExecutor(pDCE);
        } else {
            getLogger().severe("Command PD is null");
        }
        if (gu != null) {
            gu.setExecutor(new PossibleUserNames(this));
        } else {
            getLogger().severe("Command GU is null");
        }
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(pDEL, this);
        playerDataHandler.startServer();
        handler = new PlayerDataHandler(this);
        getLogger().info("PlayerData Enabled");
    }

    /**
     *
     */
    @Override
    public void onDisable() {
        playerDataHandler.endServer();
        playerDataHandler.saveAllData();
        currentInstance = null;
        getLogger().info("PlayerData Disabled");
    }

    /**
     *
     * @return
     */
    protected PDataHandler getPDataHandler() {
        return playerDataHandler;
    }

    /**
     *
     * @return
     */
    protected static PlayerData getCurrentInstance() {
        return currentInstance;
    }

    public PlayerDataHandler getHandler() {
        return handler;
    }

    /**
     * Get a visually nice date from a timestamp. Acts like: 4 years, 2 months,
     * 1 day, 10 hours, 30 minutes, and 9 seconds (That is just a random string
     * of numbers I came up with, but that is what the formating is like) Will
     * emit any terms that are 0, eg, if 0 days, then it would be 4 years, 2
     * months, 10 hours, 30 minutes, and 9 seconds Will put a , between all
     * terms and also a , and between the last term and the second to last term.
     * would do 4 years, 2 months and 10 hours returns now if
     *
     * @param millis the millisecond value to turn into a date string
     * @return A visually nice date. now if millis == 0;
     */
    public static String getFormattedDDate(long millis) {
        long years;
        long days;
        long hours;
        long minutes;
        long seconds;
        years = 0;
        days = TimeUnit.MILLISECONDS.toDays(millis);
        hours = TimeUnit.MILLISECONDS.toHours(millis) - TimeUnit.DAYS.toHours(days);
        minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(hours) - TimeUnit.DAYS.toMinutes(days);
        seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(minutes) - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.DAYS.toSeconds(days);
        while (days > 365) {
            years += 1;
            days -= 365;
        }
        String returnValue = "";
        if (years > 0) {
            if (years == 1) {
                returnValue += "1 year";
            } else {
                returnValue += years + " years";
            }
            if (days > 0 || hours > 0 || minutes > 0 || seconds > 0) {
                if ((days == 0 && ((hours == 0 && minutes == 0) || (minutes == 0 && seconds == 0) || (hours == 0 && seconds == 0))) || (hours == 0 && minutes == 0 && seconds == 0)) {
                    returnValue += ", and ";
                } else {
                    returnValue += ", ";
                }
            }
        }
        if (days > 0) {
            if (days == 1) {
                returnValue += "1 day";
            } else {
                returnValue += days + " days";
            }
            if (hours > 0 || minutes > 0 || seconds > 0) {
                if ((hours == 0 && minutes == 0) || (minutes == 0 && seconds == 0) || (hours == 0 && seconds == 0)) {
                    returnValue += ", and ";
                } else {
                    returnValue += ", ";
                }
            }
        }
        if (hours > 0) {
            if (hours == 1) {
                returnValue += "1 hour";
            } else {
                returnValue += hours + " hours";
            }
            if (minutes > 0 || seconds > 0) {
                if (minutes == 0 || seconds == 0) {
                    returnValue += ", and ";
                } else {
                    returnValue += ", ";
                }
            }
        }
        if (minutes > 0) {
            if (minutes == 1) {
                returnValue += "1 minute";
            } else {
                returnValue += minutes + " minutes";
            }
            if (seconds > 0) {
                returnValue += ", and ";
            }
        }
        if (seconds > 0) {
            if (seconds == 1) {
                returnValue += "1 second";
            } else {
                returnValue += seconds + " seconds";
            }
        }
        if (years == 0 && days == 0 && hours == 0 && minutes == 0 && seconds == 0) {
            returnValue += "now";
        }
        return returnValue;
    }
}
