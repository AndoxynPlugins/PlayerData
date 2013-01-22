package net.daboross.bukkitdev.playerdata;

/**
 *
 * @author daboross
 */
public class Data {

    private String name;
    private String[] data;
    private PData owner;

    public Data(String name, String[] data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public String[] getData() {
        return data;
    }

    public PData getOwner() {
        return owner;
    }
    //This should be called from PData when this is added to PData

    protected void setOwner(PData pData) {
        this.owner = pData;
    }
}
