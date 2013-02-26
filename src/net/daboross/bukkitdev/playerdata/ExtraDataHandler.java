package net.daboross.bukkitdev.playerdata;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;

/**
 *
 * @author daboross
 */
public class ExtraDataHandler {

    private Map<String, ExtraData> extraDatas = new HashMap<String, ExtraData>();
    private ExtraDataSaveThread extraThread;

    protected ExtraDataHandler() {
        extraThread = new ExtraDataSaveThread(this);
        Bukkit.getScheduler().runTaskAsynchronously(PlayerData.getCurrentInstance(), extraThread);
    }

    /**
     * Puts a data into the extraDataHandler with the given dataName. It
     */
    public ExtraData setExtraData(final String dataName, final ExtraData extraData) {
        return extraDatas.put(dataName, extraData);
    }

    /**
     * Schedules a data save with the given dataName.
     */
    public void saveData(final String dataName) {
        extraThread.saveData(dataName);
    }

    /**
     * This should ONLY be called from ExtraDataSaveThread.
     */
    protected void saveDataAsyncFromExtraDataSaveThread(final String extraDatas) {
    }
}
