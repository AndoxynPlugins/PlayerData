package net.daboross.bukkitdev.playerdata;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import net.daboross.bukkitdev.playerdata.api.LoginData;
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

    private long MIN_TIME_BETWEEN_DISPLAYNAME_UPDATES = TimeUnit.SECONDS.toMillis(10);
    /**
     * Stores the last getDate that the nickname was updated so that we don't
     * updated it very very often.
     */
    private long minNextNicknameUpdate = System.currentTimeMillis();
    private final String username;
    private String displayname;
    private long timePlayed = 0;
    private long currentSession;
    private final List<LoginDataImpl> logins = new ArrayList<LoginDataImpl>();
    private final List<LoginDataImpl> loginsUnmodifiable = Collections.unmodifiableList(logins);
    private final List<Long> logouts = new ArrayList<Long>();
    private final List<Long> logoutsUnmodifiable = Collections.unmodifiableList(logouts);
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
     * @param getUsername The Full UserName of this player
     * @param getDisplayname The Last DisplayName this player had that was not
     * the same as this player's username. Or the player's username if the
     * player's display name has never been recorded.
     * @param getAllLogins A list of times this player has logged in.
     * @param getAllLogouts A list of times this player has logged out.
     * @param getTimePlayed The getDate this player has played on this server.
     * @param data A List of custom data entries.
     */
    public PlayerDataImpl(String userName, String nickName, ArrayList<LoginDataImpl> logIns, ArrayList<Long> logOuts, long timePlayed, Map<String, String[]> extraData) {
        this.username = userName;
        this.displayname = nickName;
        if (this.displayname == null || this.displayname.length() == 0) {
            this.displayname = this.username;
        }
        this.logins.addAll(logIns);
        this.logouts.addAll(logOuts);
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

    private boolean updateDisplayNameWithResult(Player p) {
        if (!p.getName().equals(p.getDisplayName())) {
            this.displayname = p.getDisplayName();
            return true;
        } else {
            return false;
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
     * This saves this PlayerDataImpl's Status to file. Does this by calling the
     * PlayerHandlerImpl's function to do this.
     */
    private void saveStatus() {
        PlayerHandlerImpl pdh = PlayerDataStatic.getInternalHandler();
        if (pdh != null) {
            pdh.savePData(this);
        } else {
            PlayerDataStatic.getPlayerDataLogger().warning("PlayerData internal handler not found!");
        }
    }

    /**
     * This creates a new Thread that updates next. This is here because when a
     * player logs on, their username is the same as their nick name. This
     * function makes a new thread that runs in 1 second, then checks if this
     * player's username is the same as this player's nickname. If they are,
     * then it will run this function again.
     */
    private void makeExtraThread(final Player p) {
        if (p.isOnline() && !updateDisplayNameWithResult(p)) {
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
            pdh.loggedIn(this);
            PlayerDataStatic.getPlayerDataLogger().log(Level.INFO, "{0} Logged Out", username);
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
            pdh.loggedIn(this);
            PlayerDataStatic.getPlayerDataLogger().log(Level.INFO, "{0} Logged In", username);
        }
    }

    /**
     * This function gets the username of the player represented by this
     * PlayerDataImpl.
     *
     * @return The username of the player represented by this PlayerDataImpl.
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * This gets the last display name that this player had the last getDate
     * they were online.
     */
    @Override
    public String getDisplayname() {
        if (System.currentTimeMillis() > minNextNicknameUpdate) {
            updateDisplayName();
            minNextNicknameUpdate = System.currentTimeMillis() + MIN_TIME_BETWEEN_DISPLAYNAME_UPDATES;
        }
        return displayname;
    }

    /**
     * This function gets whether or not this player is online.
     *
     * @return Whether or not this player is online
     */
    @Override
    public boolean isOnline() {
        return online;
    }

    /**
     * This function gets the first getDate this player logged into this server.
     * If Bukkit's the recorded first log in is earlier then this
     * PlayerDataImpl's recorded first log in, then this PlayerDataImpl's
     * information is updated with Bukkit's
     *
     * @return
     */
    public LoginDataImpl getFirstLogIn() {
        return logins.get(0);
    }

    /**
     * This function gets how long this player has played on this server.
     *
     * @return The Time Played on this server in milliseconds.
     */
    @Override
    public long getTimePlayed() {
        return timePlayed;
    }

    /**
     * This function gets a list of times this player has logged in.
     *
     * @return An unmodifiable list of timestamps when this player has logged
     * in. Each In milliseconds.
     */
    @Override
    public List<? extends LoginData> getAllLogins() {
        return loginsUnmodifiable;
    }

    public List<LoginDataImpl> getAllLoginsInternal() {
        return loginsUnmodifiable;
    }

    /**
     * This function gets a list of times this player has logged out.
     *
     * @return An unmodifiable list of timestamps when this player has logged
     * out. Each In milliseconds.
     */
    @Override
    public List<Long> getAllLogouts() {
        return logoutsUnmodifiable;
    }

    /**
     * This function checks whether the last getDate the player was seen is
     * within the specified amount in days.
     */
    public boolean joinedLastWithinDays(int days) {
        if (isOnline()) {
            return true;
        }
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -days);
        return getLastSeen() > cal.getTimeInMillis();
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
        OfflinePlayer offP = Bukkit.getOfflinePlayer(username);
        long bukkitFirstPlayed = offP.getFirstPlayed();
        long bukkitLastPlayed = offP.getLastPlayed();
        if (offP.hasPlayedBefore()) {
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
        }
        ArrayList<Long> logOutsNewList = new ArrayList<Long>();
        for (Long l : logouts) {
            if (!logOutsNewList.contains(l)) {
                logOutsNewList.add(l);
            }
        }
        logouts.clear();
        logouts.addAll(logOutsNewList);
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
