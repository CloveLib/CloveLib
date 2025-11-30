/*
 * Copyright (c) 2025 Clove Twilight
 * Licensed under the MIT License
 */

package uk.co.clovetwilight3.clovelib.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

/**
 * Event fired when a player is banned via CloveLib.
 * Other plugins can listen to this event to perform actions when a player is banned.
 */
public class PlayerBannedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final UUID playerUUID;
    private final String playerName;
    private final String reason;
    private final String bannedBy;
    private final UUID bannedByUUID;

    public PlayerBannedEvent(UUID playerUUID, String playerName, String reason, String bannedBy, UUID bannedByUUID) {
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.reason = reason;
        this.bannedBy = bannedBy;
        this.bannedByUUID = bannedByUUID;
    }

    /**
     * Get the UUID of the banned player
     */
    public UUID getPlayerUUID() {
        return playerUUID;
    }

    /**
     * Get the username of the banned player
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Get the ban reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * Get the name of who issued the ban
     */
    public String getBannedBy() {
        return bannedBy;
    }

    /**
     * Get the UUID of who issued the ban (null if console)
     */
    public UUID getBannedByUUID() {
        return bannedByUUID;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}