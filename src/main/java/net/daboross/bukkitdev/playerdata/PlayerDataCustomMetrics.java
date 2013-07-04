/*
 * Author: Dabo Ross
 * Website: www.daboross.net
 * Email: daboross@daboross.net
 */
package net.daboross.bukkitdev.playerdata;

import net.daboross.bukkitdev.playerdata.metrics.Metrics;

/**
 *
 * @author daboross
 */
public class PlayerDataCustomMetrics {

    private final PlayerDataBukkit playerdata;
    private final Metrics metrics;

    public PlayerDataCustomMetrics(PlayerDataBukkit playerdata, Metrics metrics) {
        this.playerdata = playerdata;
        this.metrics = metrics;
    }

    /**
     * Adds the custom Plotters/Graphics to Metrics
     */
    public void addCustom() {
        Metrics.Plotter plotter = new Metrics.Plotter("#PlayerDatas Loaded") {
            @Override
            public int getValue() {
                return playerdata.getPDataHandler().numPlayersLoaded();
            }
        };
        Metrics.Graph graph = metrics.createGraph("#PlayerDatas Loaded");
        graph.addPlotter(plotter);
        metrics.addGraph(graph);
    }
}
