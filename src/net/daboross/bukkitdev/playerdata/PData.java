package net.daboross.bukkitdev.playerdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 * This is an object that holds all the information PlayerData has stored on one
 * player. This holds one player's data. This includes the player's full
 * username, last display name, time played on this server, a list of times when
 * they have logged in, and a list of times they have logged out. Other
 * information which is loaded when needed, not from files, includes the user's
 * group, and whether or not they are online. All this is accessible through one
 * player's PData.
 *
 * @author daboross
 */
public final class PData implements Comparable<PData> {

    private String userName;
    private String nickName;
    private long timePlayed;
    private long currentSession;
    private final ArrayList<Long> logIns = new ArrayList<Long>();
    private final ArrayList<Long> logOuts = new ArrayList<Long>();
    private final ArrayList<Data> data = new ArrayList<Data>();
    private boolean online;
    private boolean alive = false;
    private String[] groups;
    private PermissionUser permUser;

    /**
     * Use This to create a NEW Player who has never joined before This should
     * never called be any class besides the PDataHandler.
     *
     * @param p The Player to create a PData from.
     */
    protected PData(Player p) {
        if (p == null) {
            throw new IllegalArgumentException("Player Can't Be Null");
        }
        logIns.add(p.getFirstPlayed());
        timePlayed = 0;
        userName = p.getName();
        nickName = p.getDisplayName();
        timePlayed = 0;
        online = p.isOnline();
        initialChecks();
    }

    /**
     * Use This to create a NEW Player who has never joined before This should
     * never called be any class besides the PDataHandler. This should only be
     * used when PlayerData is creating empty player data files from another
     * data storage, such as Bukkit's store.
     *
     * @param p The Offline Player to create a PData from.
     */
    protected PData(OfflinePlayer p) {
        if (p == null) {
            throw new IllegalArgumentException("Player Can't Be Null");
        }
        if (!p.hasPlayedBefore()) {
            throw new IllegalArgumentException("Player Has Never Been Online!");
        }
        logIns.add(p.getFirstPlayed());
        timePlayed = 0;
        userName = p.getName();
        if (p.isOnline()) {
            Player pl = p.getPlayer();
            nickName = pl.getDisplayName();
            online = true;
        } else {
            nickName = p.getName();
            online = false;
            logOuts.add(p.getLastPlayed());
        }
        initialChecks();
    }

    /**
     * This creates a PData from data loaded from a file. This should never be
     * called except from within FileParser!
     *
     * @param userName The Full UserName of this player
     * @param nickName The Last DisplayName this player had that was not the
     * same as this player's username. Or the player's username if the player's
     * display name has never been recorded.
     * @param logIns A list of times this player has logged in.
     * @param logOuts A list of times this player has logged out.
     * @param timePlayed The time this player has played on this server.
     * @param data A List of custom data entries.
     */
    protected PData(String userName, String nickName, ArrayList<Long> logIns, ArrayList<Long> logOuts, long timePlayed, Data[] data) {
        this.userName = userName;
        this.nickName = nickName;
        if (this.nickName == null) {
            this.nickName = this.userName;
        } else if (this.nickName.length() == 0) {
            this.nickName = this.userName;
        }
        this.logIns.addAll(logIns);
        this.logOuts.addAll(logOuts);
        this.timePlayed = timePlayed;
        this.data.addAll(Arrays.asList(data));
        for (Data d : data) {
            d.setOwner(this);
        }
        initialChecks();
    }

    /**
     * This should ONLY be called from a constructor. And ALWAYS be called from
     * EVERY constructor.
     */
    private void initialChecks() {
        currentSession = System.currentTimeMillis();
        updateStatus(true, false);
        checkBukkitForTimes();
    }

    /**
     * This loads data from the player given. Check if that player's username is
     * the same as this PData's username before running this function.
     *
     * @param p The Player.
     */
    protected void setPlayer(Player p) {
        userName = p.getName();
        nickName = p.getDisplayName();
    }

    /**
     * This updates this player's status.
     *
     * @param saveIfOnline Whether to save the info when the player is online.
     * @param saveIfOffline Whether to save the info when the player is offline.
     * @return Will return true if the player's username equals the players
     * display name, or if the player is offline. false otherwise.
     */
    protected boolean updateStatus(boolean saveIfOnline, boolean saveIfOffline) {
        Player[] pList = Bukkit.getServer().getOnlinePlayers();
        online = false;
        boolean returnV = true;
        for (Player p : pList) {
            String name = p.getName();
            String nName = p.getDisplayName();
            if (name.equals(this.userName)) {
                if (!nName.equals(name)) {
                    this.nickName = nName;
                } else {
                    returnV = false;
                }
                online = true;
                timePlayed += (System.currentTimeMillis() - currentSession);
                currentSession = System.currentTimeMillis();
                if (saveIfOnline) {
                    saveStatus();
                }
                updateGroup();
                return returnV;
            }
        }
        if (saveIfOffline) {
            saveStatus();
        }
        return returnV;
    }

    /**
     * This saves this PData's Status to files. Does this by calling the
     * PDataHandler's function to do this.
     */
    private void saveStatus() {
        PlayerData pd = PlayerData.getCurrentInstance();
        if (pd != null) {
            PDataHandler pDH = pd.getPDataHandler();
            if (pDH != null) {
                pDH.savePData(this);
            } else {
                pd.getLogger().info("PDataHandler Not Found!");
            }
        }
    }

    /**
     * This creates a new Thread that updates next. This is here because when a
     * player logs on, their username is the same as their nick name. This
     * function makes a new thread that runs in 1 second, then checks if this
     * player's username is the same as this player's nickname. If they are,
     * then it will run this function again.
     */
    protected void makeExtraThread() {
        if (PlayerData.getCurrentInstance() != null) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(PlayerData.getCurrentInstance(), new Runnable() {
                @Override
                public void run() {
                    if (!updateStatus(true, false)) {
                        makeExtraThread();
                    }
                }
            }, 20L);
        }
    }

    /**
     * This function tells this PData that the player who this PData is
     * representing just logged out. Do not run this function from outside the
     * PlayerDataEventListener. This will save the current status of this PData
     * to file.
     */
    protected void loggedOut() {
        timePlayed += (System.currentTimeMillis() - currentSession);
        currentSession = System.currentTimeMillis();
        logOuts.add(System.currentTimeMillis());
        updateStatus(true, true);
        PlayerData.getCurrentInstance().getLogger().log(Level.INFO, "PData Logged Out: {0}", userName);
    }

    /**
     * This function tells this PData that the player who this PData is
     * representing just logged in. Do not run this function from outside the
     * PlayerDataEventListener. This will save the current status of this PData
     * to file.
     */
    protected void loggedIn() {
        logIns.add(System.currentTimeMillis());
        currentSession = System.currentTimeMillis();
        makeExtraThread();
        PlayerData.getCurrentInstance().getPDataHandler().loggedIn(this);
        PlayerData.getCurrentInstance().getLogger().log(Level.INFO, "PData Logged In: {0}", userName);
    }

    /**
     * This function gets the username of the player represented by this PData.
     *
     * @return The username of the player represented by this PData.
     */
    public String userName() {
        return userName;
    }

    /**
     *
     * This gets the last display name that this player had the last time they
     * were online.
     *
     * @param updateStatus If this is true, then this PData will update status
     * before returning. If false, then this will return the last nickname
     * recorded. If the player is online, and had changed their display name
     * since last update, and this is false, then their old nickname is
     * returned.
     * @return
     */
    public String nickName(boolean updateStatus) {
        if (updateStatus) {
            updateStatus(false, false);
        }
        return nickName;
    }

    /**
     * This function gets whether or not this player is online.
     *
     * @return Whether or not this player is online
     */
    public boolean isOnline() {
        updateStatus(false, false);
        return online;
    }

    /**
     * This function gets the first time this player logged into this server. If
     * Bukkit's the recorded first log in is earlier then this PData's recorded
     * first log in, then this PData's information is updated with Bukkit's
     *
     * @return
     */
    public long getFirstLogIn() {
        return logIns.get(0);
    }

    /**
     * This function gets how long this player has played on this server.
     *
     * @return The Time Played on this server in milliseconds.
     */
    public long timePlayed() {
        return timePlayed;
    }

    /**
     * This function gets a list of times this player has logged in.
     *
     * @return A list of timestamps when this player has logged in. Each In
     * milliseconds.
     */
    public Long[] logIns() {
        return logIns.toArray(new Long[0]);
    }

    /**
     * This function gets a list of times this player has logged out.
     *
     * @return A list of timestamps when this player has logged out. Each In
     * milliseconds.
     */
    public Long[] logOuts() {
        return logOuts.toArray(new Long[0]);
    }

    /**
     * This returns whether or not this player is 'alive'. Alive in this case
     * means if the player has logged in within the last 2 months. This does run
     * updateStatus(). WILL NOT CHECK IF THE PLAYER IS ONLINE!
     *
     * @return Whether or not this player has logged in in the last 2 months or
     * not.
     */
    private boolean checkIsAlive() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -2);
        if (isOnline()) {
            return true;
        }
        if (lastSeen() > cal.getTimeInMillis()) {
            return true;
        }
        return false;
    }

    /**
     * This returns whether or not the player has joined within the last 2
     * months
     *
     * @return true if the played has played within the last 2 months, false
     * otherwise.
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * This function checks whether the last time the player was seen is within
     * the specified amount in days.
     */
    public boolean joinedLastWithinDays(int days) {
        if (isOnline()) {
            return true;
        }
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -days);
        if (lastSeen() > cal.getTimeInMillis()) {
            return true;
        }
        return false;
    }

    /**
     * Adds Data To This Player. If Data Is Already On With the given data's
     * Name, It will be Replaced! This PData will keep track of this data, and
     * will save it on server shutdown, and will load it when PlayerData is
     * loaded.
     *
     * @param d The Data To Add.
     */
    public void addData(Data d) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getName().equalsIgnoreCase(d.getName())) {
                data.remove(i);
            }
        }
        data.add(d);
        d.setOwner(this);
        updateStatus(true, true);
    }

    /**
     * This gets ALL Custom Data that has ever been given to a PData for this
     * player.
     *
     * @return An array of custom datas that have been added to this Player's
     * PData.
     */
    public Data[] getData() {
        return data.toArray(new Data[0]);
    }

    /**
     * This gets a Data from this player with of a given type.
     *
     * @param name The data type, EG "bandata".
     * @return The Data that has been given to this player with addData(), or
     * null if data of this type has never been added to this player.
     */
    public Data getData(String name) {
        for (Data d : data) {
            if (d.getName().equalsIgnoreCase(name)) {
                return d;
            }
        }
        return null;
    }

    /**
     * This function gets whether or not this PData is storing a custom data of
     * a given type.
     *
     * @param name The type of PData to check.
     * @return true if that type of data is being stored, false otherwise.
     */
    public boolean hasData(String name) {
        for (Data d : data) {
            if (d.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the OfflinePlayer stored by Bukkit that represents the same player
     * that this PData represents.
     *
     * @return The OfflinePlayer stored by Bukkit that represents the same
     * player that this PData represents.
     */
    public OfflinePlayer getOfflinePlayer() {
        OfflinePlayer ofp = Bukkit.getOfflinePlayer(userName);
        return ofp;
    }

    /**
     * This function gets the permissions group that this player is in. This
     * will get the main group, EG the first group which has the "basic"
     * permission. This will return "basic" if there are no groups which have
     * the "basic" permission.
     *
     * @return
     */
    public String[] getGroups() {
        updateGroup();
        return groups;
    }

    public boolean isGroup(String group) {
        updateGroup();
        for (String gr : groups) {
            if (group.equals(gr)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This function gets the Permissions User which represents the same player
     * that this PData represents.
     *
     * @return The Permissions User which represents the same player that this
     * PData represents. null if PermissionEx is not loaded
     */
    public PermissionUser getPermUser() {
        updateGroup();
        return permUser;
    }

    public boolean hasPermission(String perm) {
        if (PlayerData.isPEX()) {
            getPermUser();
            return permUser.has(perm);
        }
        return false;
    }

    /**
     * This function updates this PData's Permission Group. This function gets
     * data from PermissionsEx on this user, and sets variables in this PData
     * according to that.
     */
    private void updateGroup() {
        if (PlayerData.isPEX()) {
            findPermUser();
            groups = permUser.getGroupsNames();
        } else {
            permUser = null;
            groups = new String[]{"Unknown"};
        }
    }

    private void findPermUser() {
        if (PlayerData.isPEX()) {
            permUser = PermissionsEx.getUser(userName);
        }
    }

    /**
     * This function will check the first time this player has played and the
     * last time this player has played with Bukkit's records.
     */
    protected void checkBukkitForTimes() {
        Comparator<Long> comp = new Comparator<Long>() {
            public int compare(Long l1, Long l2) {
                return (int) (l1 - l2);
            }
        };
        Collections.sort(logIns, comp);
        Collections.sort(logOuts, comp);
        OfflinePlayer offP = Bukkit.getOfflinePlayer(userName);
        long bukkitFirstPlayed = offP.getFirstPlayed();
        long bukkitLastPlayed = offP.getLastPlayed();
        if (offP.hasPlayedBefore()) {
            if (logIns.isEmpty()) {
                logIns.add(bukkitFirstPlayed);
            } else if (bukkitFirstPlayed < logIns.get(0)) {
                logIns.add(0, bukkitFirstPlayed);
            }
            if (!online) {
                if (logOuts.isEmpty()) {
                    logOuts.add(bukkitLastPlayed);
                } else if (bukkitLastPlayed > logOuts.get(0)) {
                    logOuts.add(bukkitLastPlayed);
                }
            }
        }
    }

    /**
     * This function checks when the player was last on the server. This
     * function will NOT return correctly if the player is currently online, so
     * CHECK IF THEY ARE ONLINE first.
     */
    public long lastSeen() {
        return logIns.get(logIns.size() - 1);
    }

    public int compareTo(PData pd) {
        if (pd == null) {
            throw new NullPointerException();
        }
        Long l1 = lastSeen();
        Long l2 = pd.lastSeen();
        if (l1 == l2 && pd != this) {
            PlayerData.getCurrentInstance().getLogger().log(Level.INFO, "Players Are Equal? {0} {1}", new Object[]{userName, pd.userName});
        }
        return l2.compareTo(l1);
    }

    /**
     * This method should ONLY be called from PDataHandler.
     */
    protected void changeTime(boolean bool) {
        logOuts.set(logOuts.size() - 1, logOuts.get(logOuts.size() - 1) + (bool ? 1 : -1));
    }
}
