/*
 * Copyright (C) 2013 Dabo Ross <http://www.daboross.net/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.daboross.bukkitdev.playerdata.api.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.daboross.bukkitdev.playerdata.api.PlayerData;
import net.daboross.bukkitdev.playerdata.api.PlayerHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author daboross
 */
public class PlayerDataInfoEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final CommandSender infoRequester;
    private final PlayerData playerDataRequested;
    private final PlayerHandler handler;
    private final List<String> extraInfo;

    public PlayerDataInfoEvent(CommandSender infoRequester, PlayerData playerDataRequested, PlayerHandler handler) {
        super(false);
        this.infoRequester = infoRequester;
        this.playerDataRequested = playerDataRequested;
        this.handler = handler;
        this.extraInfo = new ArrayList<String>();
    }

    public CommandSender getInfoRequester() {
        return infoRequester;
    }

    public PlayerData getPlayerDataRequested() {
        return playerDataRequested;
    }

    public PlayerHandler getHandler() {
        return handler;
    }

    public void addExtraInfo(String info) {
        extraInfo.add(info);
    }

    public List<String> getExtraInfo() {
        return Collections.unmodifiableList(extraInfo);
    }

    public String[] getExtraInfoArray() {
        return extraInfo.toArray(new String[extraInfo.size()]);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
