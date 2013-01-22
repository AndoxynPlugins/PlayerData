package net.daboross.bukkitdev.playerdata;

/**
 *
 * @author daboross
 */
public class PlayerDataHandler {

    private PDataHandler pDataHandler;

    public PlayerDataHandler(PlayerData pd) {
        this.pDataHandler = pd.getPDataHandler();
    }

    public boolean doesPlayerExists(String str) {
        return (pDataHandler.getPDataFromUsername(str) != null);
    }

    /**
     * @returns the full username of a partial username
     * @returns null if not found
     */
    public String getFullUsername(String str) {
        return pDataHandler.getFullUsername(str);
    }

    /**
     * Adds the data given to the Player given. If the player already has a data
     * with data's name, then the previous data is replaced with the new data.
     */
    public boolean addCustomData(String playerName, Data data) {
        PData pData = pDataHandler.getPDataFromUsername(playerName);
        if (pData != null) {
            pData.addData(data);
            return true;
        } else {
            return false;
        }
    }

    public void addCustomDataParsing(String dataName, DataDisplayParser ddp) {
        if (dataName == null || ddp == null) {
            throw new IllegalArgumentException("Null Argument!");
        }
        pDataHandler.addDataParser(dataName, ddp);
    }

    /**
     * Get Custom Data For Given Player Name and Given data Name. Will return
     * null if player is not found or if data with given name is not found on
     * player.
     */
    public Data getCustomData(String playerName, String dataName) {
        PData pData = pDataHandler.getPDataFromUsername(playerName);
        if (pData != null) {
            return pData.getData(dataName);
        } else {
            return null;
        }
    }

    /**Returns a PData corresponding to the username given. Null if not found.*/
    public PData getPData(String playerToBanUserName) {
        return pDataHandler.getPDataFromUsername(pDataHandler.getFullUsername(playerToBanUserName));
    }
}
