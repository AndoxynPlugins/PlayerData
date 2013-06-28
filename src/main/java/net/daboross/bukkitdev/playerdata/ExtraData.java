package net.daboross.bukkitdev.playerdata;

/**
 *
 * @author daboross
 */
public class ExtraData {

    private String[] data;

    public ExtraData(final String[] data) {
        this.data = data;
    }

    public void setDataList(final String[] data) {
        this.data = data;
    }

    public String[] getData() {
        return data;
    }
}
