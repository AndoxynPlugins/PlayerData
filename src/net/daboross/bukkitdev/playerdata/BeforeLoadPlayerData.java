package net.daboross.bukkitdev.playerdata;

import java.util.ArrayList;

/**
 *
 * @author daboross
 */
public class BeforeLoadPlayerData {

    private String userName;
    private String nickName;
    private ArrayList<IPLogin> logIns;
    private ArrayList<Long> logOuts;
    private long timePlayed;
    private Data[] data;

    protected BeforeLoadPlayerData(String userName, String nickName, ArrayList<IPLogin> logIns, ArrayList<Long> logOuts, long timePlayed, Data[] data) {
        this.userName = userName;
        this.nickName = nickName;
        this.logIns = logIns;
        this.logOuts = logOuts;
        this.timePlayed = timePlayed;
        this.data = data;
    }

    protected PData getPData() {
        return new PData(userName, nickName, logIns, logOuts, timePlayed, data);
    }
}
