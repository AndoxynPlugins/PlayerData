package net.daboross.bukkitdev.playerdata;

/**
 * This is an interface that you should implement if you want your data to show
 * up in /playerdata viewinfo
 *
 * @author daboross
 */
public interface DataDisplayParser {

    /**
     * This should return a short info summery, preferably one line, that
     * represents the data given.
     */
    public String[] shortInfo(Data d);

    /**
     * This should return a full length summery of the data given, preferably
     * all data that can be taken out of the data given
     */
    public String[] longInfo(Data d);
}
