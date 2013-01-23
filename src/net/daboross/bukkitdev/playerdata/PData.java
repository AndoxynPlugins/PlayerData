package net.daboross.bukkitdev.playerdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 *
 * @author daboross
 */
public final class PData {

    private String userName;
    private String nickName;
    private long timePlayed;
    private long currentSession;
    private final ArrayList<Long> logIns = new ArrayList<>();
    private final ArrayList<Long> logOuts = new ArrayList<>();
    private final ArrayList<Data> data = new ArrayList<>();
    private boolean online;
    private boolean alive = false;
    private String group;
    private PermissionUser permUser;

    /**
     * Use This to create a NEW Player who has never joined before
     *
     * @param p The Player.
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
        currentSession = System.currentTimeMillis();
        online = p.isOnline();
        if (p.isBanned()) {
            p.setBanned(false);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ("/pex user " + p.getName() + " group set banned"));
        }
        updateStatus(true, false);
        setAlive();
    }

    /**
     *
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
            currentSession = System.currentTimeMillis();
        } else {
            nickName = p.getName();
            online = false;
            logOuts.add(p.getLastPlayed());
        }
        updateStatus(true, false);
        setAlive();
    }

    /**
     *
     * @param userName
     * @param nickName
     * @param logIns
     * @param logOuts
     */
    protected PData(String userName, String nickName, ArrayList<Long> logIns, ArrayList<Long> logOuts, long timePlayed, Data[] data) {
        this.userName = userName;
        this.nickName = nickName;
        this.logIns.addAll(logIns);
        this.logOuts.addAll(logOuts);
        this.timePlayed = timePlayed;
        currentSession = System.currentTimeMillis();
        this.data.addAll(Arrays.asList(data));
        for (Data d : data) {
            d.setOwner(this);
        }
        updateStatus(true, false);
        setAlive();
    }

    /**
     *
     * @param p The Player.
     */
    protected void setPlayer(Player p) {
        userName = p.getName();
        nickName = p.getDisplayName();
    }

    /**
     *
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

    private void setAlive() {
        PlayerData pd = PlayerData.getCurrentInstance();
        alive = (isAlive() || online);
        if (pd != null) {
            PDataHandler pDH = pd.getPDataHandler();
            if (pDH != null) {
                pDH.setAlive(this, alive);
            } else {
                pd.getLogger().info("PDataHandler Not Found!");
            }
        }
    }

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

    protected void nextAction() {
        if (!updateStatus(true, false)) {
            makeExtraThread();
        }
    }

    protected void makeExtraThread() {
        if (PlayerData.getCurrentInstance() != null) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(PlayerData.getCurrentInstance(), new PDataExtraThread(this), 60L);
        }
    }

    /**
     * Run This Function to tell the PData that this player just logged out
     */
    protected void loggedOut() {
        timePlayed += (System.currentTimeMillis() - currentSession);
        currentSession = System.currentTimeMillis();
        logOuts.add(System.currentTimeMillis());
        updateStatus(true, true);
        PlayerData.getCurrentInstance().getLogger().log(Level.INFO, "PData Logged Out: {0}", userName);
    }

    /**
     * Run This Function to tell the PData that this player just logged in
     */
    protected void loggedIn() {
        logIns.add(System.currentTimeMillis());
        currentSession = System.currentTimeMillis();
        if (!updateStatus(true, true)) {
            makeExtraThread();
        }
        if (!alive) {
            setAlive();
        }
        PlayerData.getCurrentInstance().getLogger().log(Level.INFO, "PData Logged In: {0}", userName);
    }

    /**
     *
     * @return The username of this player
     */
    public String userName() {
        return userName;
    }

    /**
     *
     * @return
     */
    public String nickName(boolean updateStatus) {
        if (updateStatus) {
            updateStatus(false, false);
        }
        return nickName;
    }

    /**
     *
     * @return
     */
    public long lastLogIn() {
        if (logIns.isEmpty()) {
            return 0;
        } else {
            return logIns.get(logIns.size() - 1);
        }
    }

    /**
     *
     * @return
     */
    public long lastLogOut() {
        if (logOuts.isEmpty()) {
            return 0;
        } else {
            return logOuts.get(logOuts.size() - 1);
        }
    }

    /**
     * @return Whether or not this player is online
     */
    public boolean isOnline() {
        updateStatus(false, false);
        return online;
    }

    public long getFirstLogIn() {
        OfflinePlayer p = Bukkit.getServer().getOfflinePlayer(userName);
        long f = p.getFirstPlayed();
        if (!p.hasPlayedBefore() && !logIns.isEmpty()) {
            return logIns.get(0);
        } else if (logIns.isEmpty()) {
            return 0;
        } else if (f < logIns.get(0) && f > 0) {
            logIns.add(0, f);
            return f;
        } else {
            return logIns.get(0);
        }
    }

    /**
     *
     * @return The Time Played
     */
    public long timePlayed() {
        return timePlayed;
    }

    public Long[] logIns() {
        return logIns.toArray(new Long[0]);
    }

    public Long[] logOuts() {
        return logOuts.toArray(new Long[0]);
    }

    public boolean isAlive() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -2);
        if (lastLogIn() > cal.getTimeInMillis()) {
            return true;
        }
        return false;
    }

    /**
     * Adds Data To This Player. If Data Is Already On With the given data's
     * Name, It will be Replaced!
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

    public Data[] getData() {
        return data.toArray(new Data[0]);
    }

    public Data getData(String name) {
        for (Data d : data) {
            if (d.getName().equalsIgnoreCase(name)) {
                return d;
            }
        }
        return null;
    }

    public boolean hasData(String name) {
        for (Data d : data) {
            if (d.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public OfflinePlayer getOfflinePlayer() {
        OfflinePlayer ofp = Bukkit.getOfflinePlayer(userName);
        return ofp;
    }

    public String getGroup() {
        updateGroup();
        return group;
    }
    public PermissionUser getPermUser(){
        updateGroup();
        return permUser;
    }

    private void updateGroup() {
        permUser = PermissionsEx.getUser(userName);
        for (PermissionGroup permG : permUser.getGroups()) {
            if (permG.isChildOf(PermissionsEx.getPermissionManager().getGroup("Basic")) || permG.getName().equalsIgnoreCase("basic")) {
                group = permG.getName();
                break;
            }
        }
    }
}
