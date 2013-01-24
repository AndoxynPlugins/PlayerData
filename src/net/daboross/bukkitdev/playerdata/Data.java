package net.daboross.bukkitdev.playerdata;

/**
 * This is a object that holds custom data given to and from the
 * PlayerDataHandler. It holds a name, which is the data type, an Array of
 * strings, which are the arguments, and it holds it's owner. Its owner is a
 * PData, and the owner is set whenever this data is added to a PData. There is
 * no way to set the owner besides giving this data to a PData, or the
 * PlayerDataHandler.
 *
 * @author daboross
 */
public class Data {

    /**
     * This is the data type.
     */
    private String name;
    /**
     * This is the raw data.
     */
    private String[] data;
    /**
     * This is the owner.
     */
    private PData owner;

    /**
     * Create a Data with the Data Type name, and the Data data.
     *
     * @param name This is the type of this Data.
     * @param data This is the raw data.
     */
    public Data(String name, String[] data) {
        this.name = name;
        this.data = data;
    }

    /**
     * This gets the data type.
     *
     * @return This Type Of Data.
     */
    public String getName() {
        return name;
    }

    /**
     * This gets the raw data.
     *
     * @return The Raw Data stored in this Data.
     */
    public String[] getData() {
        return data;
    }

    /**
     * This gets the PData who is the owner of this Data. This will return null
     * if this Data has never been added to a PData.
     */
    public PData getOwner() {
        return owner;
    }

    /**
     * This should be called from PData when this is added to PData. This should
     * never be called except by the PData class.
     */
    protected void setOwner(PData pData) {
        this.owner = pData;
    }
}
