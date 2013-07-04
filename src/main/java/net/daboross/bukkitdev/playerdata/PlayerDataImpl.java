/*
 * Author: Dabo Ross
 * Website: www.daboross.net
 * Email: daboross@daboross.net
 */
package net.daboross.bukkitdev.playerdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import net.daboross.bukkitdev.playerdata.api.PlayerData;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.ArrayHelpers;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * This is an object that holds all the information PlayerDataBukkit has stored
 * on one player. This holds one player's data. This includes the player's full
 * username, last display name, getDate played on this server, a list of times
 * when they have logged in, and a list of times they have logged out. Other
 * information which is loaded when needed, not from files, includes the user's
 * group, and whether or not they are online. All this is accessible through one
 * player's PlayerDataImpl.
 *
 * @author daboross
 */
public final class PlayerDataImpl implements PlayerData {

    private long MIN_TIME_BETWEEN_DISPLAYNAME_UPDATES = TimeUnit.MINUTES.toMillis(1);
    /**
     * Stores the last getDate that the nickname was updated so that we don't
     * updated it very very often.
     */
    private long minNextDisplaynameUpdate = System.currentTimeMillis();
    private final String username;
    private String displayname;
    private long timePlayed = 0;
    private long currentSession;
    private final List<LoginDataImpl> logins = new ArrayList<LoginDataImpl>();
    private final List<Long> logouts = new ArrayList<Long>();
    private final Map<String, String[]> extraData = new HashMap<String, String[]>();
    private boolean online = false;
    private int nickUpdateExtraThreadUpdateTimes = 0;

    /**
     * Use This to create a NEW Player who has never joined before This should
     * never called be any class besides the PlayerHandlerImpl.
     *
     * @param p The Player to create a PlayerDataImpl from.
     */
    PlayerDataImpl(Player p) {
        if (p == null) {
            throw new IllegalArgumentException("Player Can't Be Null");
        }
        logins.add(new LoginDataImpl(p.getFirstPlayed(), p.getAddress().toString()));
        timePlayed = 0;
        username = p.getName();
        updateDisplayName(p);
        online = p.isOnline();
        currentSession = System.currentTimeMillis();
        sortTimes();
    }

    /**
     * Use This to create a NEW Player who has never joined before This should
     * never called be any class besides the PlayerHandlerImpl. This should only
     * be used when PlayerDataBukkit is creating empty player data files from
     * another data storage, such as Bukkit's store.
     *
     * @param offlinePlayer The Offline Player to create a PlayerDataImpl from.
     */
    PlayerDataImpl(OfflinePlayer offlinePlayer) {
        if (offlinePlayer == null) {
            throw new IllegalArgumentException("Player Can't Be Null");
        }
        if (!offlinePlayer.hasPlayedBefore()) {
            throw new IllegalArgumentException("Player Has Never Been Online!");
        }
        logins.add(new LoginDataImpl(offlinePlayer.getFirstPlayed()));
        timePlayed = 0;
        username = offlinePlayer.getName();
        if (offlinePlayer.isOnline()) {
            Player onlinePlayer = offlinePlayer.getPlayer();
            displayname = onlinePlayer.getDisplayName();
            online = true;
        } else {
            displayname = offlinePlayer.getName();
            online = false;
            logouts.add(offlinePlayer.getLastPlayed());
        }
        currentSession = System.currentTimeMillis();
        sortTimes();
        checkBukkitForTimes();
    }

    /**
     * This creates a PlayerDataImpl from data loaded from a file. This should
     * never be called except from within a FileParser!
     *
     * @param username The Full UserName of this player
     * @param displayname The Last DisplayName this player had that was not the
     * same as this player's username. Or the player's username if the player's
     * display name has never been recorded.
     * @param logins A list of times this player has logged in.
     * @param logouts A list of times this player has logged out.
     * @param timePlayed The getDate this player has played on this server.
     * @param extraData A List of custom data entries.
     */
    public PlayerDataImpl(String username, String displayname, ArrayList<LoginDataImpl> logins, ArrayList<Long> logouts, long timePlayed, Map<String, String[]> extraData) {
        this.username = username;
        this.displayname = displayname;
        if (this.displayname == null) {
            this.displayname = this.username;
        }
        this.logins.addAll(logins);
        this.logouts.addAll(logouts);
        this.timePlayed = timePlayed;
        this.extraData.putAll(extraData);
        currentSession = System.currentTimeMillis();
        sortTimes();
    }

    /**
     * This updates this player's status. This will NOT save this
     * PlayerDataImpl.
     *
     * @return Will return true if the player's username equals the players
     * display name, or if the player is offline. false otherwise.
     */
    protected void updateStatus() {
        Player p = online ? Bukkit.getPlayer(this.username) : null;
        if (p != null) {
            updateDisplayName(p);
            if (online) {
                timePlayed += (System.currentTimeMillis() - currentSession);
                currentSession = System.currentTimeMillis();
            }
        }
    }

    /**
     * Checks if the player is null and then updates the nick with the player's
     * nickname. Don't use this with a player that isn't this PlayerDataImpl's
     * Player.
     */
    private void updateDisplayName(Player p) {
        if (!p.getName().equals(p.getDisplayName())) {
            this.displayname = p.getDisplayName();
        }
    }

    /**
     * Updates this player's DisplayName with a player gotten from Bukkit.
     */
    private void updateDisplayName() {
        if (online) {
            updateDisplayName(Bukkit.getPlayer(this.username));
        }
    }

    /**
     * This saves this PlayerData to file async.
     */
    private void saveStatus() {
        final PlayerDataBukkit plugin = PlayerDataStatic.getPlayerDataBukkit();
        final PlayerHandlerImpl ph = plugin.getInternalHandler();
        final PlayerDataImpl pd = this;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                ph.savePData(pd);
            }
        });
    }

    /**
     * This creates a new Thread that updates next. This is here because when a
     * player logs on, their username is the same as their nick name. This
     * function makes a new thread that runs in 1 second, then checks if this
     * player's username is the same as this player's nickname. If they are,
     * then it will run this function again.
     */
    private void makeExtraThread(final Player p) {
        if (p.isOnline()) {
            updateDisplayName(p);
            if (displayname.equalsIgnoreCase(username)) {
                if (nickUpdateExtraThreadUpdateTimes < 5) {
                    nickUpdateExtraThreadUpdateTimes++;
                    PlayerDataBukkit pdb = PlayerDataStatic.getPlayerDataBukkit();
                    if (pdb != null) {
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pdb, new Runnable() {
                            @Override
                            public void run() {
                                makeExtraThread(p);
                            }
                        }, 40l);
                    }
                }
            }
        }
    }

    /**
     * This function tells this PlayerDataImpl that the player who this
     * PlayerDataImpl is representing just logged out. Do not run this function
     * from outside the PlayerDataEventListener. This will save the current
     * status of this PlayerDataImpl to file.
     */
    void loggedOut(Player p, PlayerHandlerImpl pdh) {
        if (online) {
            timePlayed += (System.currentTimeMillis() - currentSession);
            currentSession = System.currentTimeMillis();
            logouts.add(System.currentTimeMillis());
            online = false;
            updateDisplayName(p);
            saveStatus();
        }
    }

    /**
     * This function tells this PlayerDataImpl that the player who this
     * PlayerDataImpl is representing just logged in. Do not run this function
     * from outside the PlayerDataEventListener. This will save the current
     * status of this PlayerDataImpl to file.
     */
    void loggedIn(Player p, PlayerHandlerImpl pdh) {
        if (!online) {
            logins.add(new LoginDataImpl(System.currentTimeMillis(), p.getAddress().toString()));
            currentSession = System.currentTimeMillis();
            online = true;
            nickUpdateExtraThreadUpdateTimes = 0;
            makeExtraThread(p);
        }
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getDisplayname() {
        if (System.currentTimeMillis() > minNextDisplaynameUpdate) {
            updateDisplayName();
            minNextDisplaynameUpdate = System.currentTimeMillis() + MIN_TIME_BETWEEN_DISPLAYNAME_UPDATES;
        }
        return displayname;
    }

    @Override
    public boolean isOnline() {
        return online;
    }

    @Override
    public long getTimePlayed() {
        return timePlayed;
    }

    @Override
    public List<LoginDataImpl> getAllLogins() {
        return Collections.unmodifiableList(logins);
    }

    @Override
    public List<Long> getAllLogouts() {
        return Collections.unmodifiableList(logouts);
    }

    @Override
    public boolean hasExtraData(String dataName) {
        return extraData.containsKey(dataName.toLowerCase());
    }

    @Override
    public String[] addExtraData(String dataName, String[] data) {
        return extraData.put(dataName.toLowerCase(), ArrayHelpers.copyArray(data));
    }

    @Override
    public String[] removeExtraData(String dataName) {
        return extraData.remove(dataName.toLowerCase());
    }

    @Override
    public String[] getExtraData(String dataName) {
        return ArrayHelpers.copyArray(extraData.get(dataName.toLowerCase()));
    }

    @Override
    public String[] getExtraDataNames() {
        return extraData.keySet().toArray(new String[extraData.keySet().size()]);
    }

    private void sortTimes() {
        Collections.sort(logins);
        Collections.sort(logouts);
    }

    /**
     * This function will check the first getDate this player has played and the
     * last getDate this player has played with Bukkit's records.
     */
    public void checkBukkitForTimes() {
        OfflinePlayer p = Bukkit.getOfflinePlayer(username);
        long bukkitFirstPlayed = p.getFirstPlayed();
        long bukkitLastPlayed = p.getLastPlayed();
        if (logins.isEmpty()) {
            logins.add(new LoginDataImpl(bukkitFirstPlayed));
        } else if (bukkitFirstPlayed < logins.get(0).getDate()) {
            logins.add(0, new LoginDataImpl(bukkitFirstPlayed));
        }
        if (!online) {
            if (logouts.isEmpty()) {
                logouts.add(bukkitLastPlayed);
            } else if (bukkitLastPlayed > logouts.get(logouts.size() - 1)) {
                logouts.add(bukkitLastPlayed);
            }
        }
        Set<Long> logoutsNew = new LinkedHashSet<Long>(logouts);
        logouts.clear();
        logouts.addAll(logoutsNew);
    }

    /**
     * This function checks when the player was last on the server.
     */
    @Override
    public long getLastSeen() {
        if (online) {
            return System.currentTimeMillis();
        }
        if (logouts.size() > 0) {
            return logouts.get(logouts.size() - 1);
        } else {
            return 0;
        }
    }

    public int compareTo(PlayerDataImpl other) {
        if (other == null) {
            throw new NullPointerException();
        }
        Long l1;
        Long l2;
        if (online) {
            l1 = System.currentTimeMillis();
        } else {
            l1 = getLastSeen();
        }
        if (other.isOnline()) {
            l2 = System.currentTimeMillis();
        } else {
            l2 = other.getLastSeen();
        }
        return l2.compareTo(l1);
    }
}
