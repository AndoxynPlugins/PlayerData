package net.daboross.bukkitdev.playerdata;

import java.util.logging.Level;

/**
 *
 * @author daboross
 */
class PDataExtraThread implements Runnable {

    PData pData;

    public PDataExtraThread(PData pData) {
        this.pData = pData;
        PlayerData.getCurrentInstance().getLogger().log(Level.INFO, "PDataExtraThread started for: {0}", pData.userName());
    }

    @Override
    public void run() {
        PlayerData.getCurrentInstance().getLogger().log(Level.INFO, "PDataExtraThread running for: {0}", pData.nickName(false));
        pData.nextAction();
    }
}
