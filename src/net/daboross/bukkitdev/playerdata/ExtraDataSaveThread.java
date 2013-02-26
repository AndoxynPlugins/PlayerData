package net.daboross.bukkitdev.playerdata;

import java.util.ArrayList;
import java.util.logging.Level;

/**
 *
 * @author daboross
 */
public class ExtraDataSaveThread implements Runnable {

    private final ArrayList<String> dataToSave = new ArrayList<String>();
    private ExtraDataHandler edh;

    protected ExtraDataSaveThread(ExtraDataHandler edh) {
        this.edh = edh;
    }

    protected void saveData(String dataName) {
        if (!dataToSave.contains(dataName)) {
            dataToSave.add(dataName);
        }
        synchronized (dataToSave) {
            dataToSave.notify();
        }
    }

    public void run() {
        while (true) {
            for (int i = 0; i < dataToSave.size(); i++) {
                edh.saveDataAsyncFromExtraDataSaveThread(dataToSave.get(i));
            }
            synchronized (dataToSave) {
                try {
                    dataToSave.wait();
                } catch (InterruptedException ex) {
                    PlayerData.getCurrentInstance().getLogger().log(Level.SEVERE, "ExtraDataSaveThread had an InterruptedExeption! {0} : {1}", new Object[]{ex, ex.getMessage()});
                }
            }
        }
    }
}