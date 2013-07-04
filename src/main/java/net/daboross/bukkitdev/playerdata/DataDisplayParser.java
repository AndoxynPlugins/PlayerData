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
    public String commandInfo(String dataName, String[] data);
}
