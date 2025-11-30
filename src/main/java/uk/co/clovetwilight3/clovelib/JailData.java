/*
 * Copyright (c) 2025 Clove Twilight
 * Licensed under the MIT License
 */

package uk.co.clovetwilight3.clovelib;

import org.bukkit.Location;

public class JailData {
    private boolean isJailed;
    private long jailEndTime;
    private Location jailLocation;
    private Location unjailLocation;

    public JailData(boolean isJailed, long jailEndTime, Location jailLocation, Location unjailLocation) {
        this.isJailed = isJailed;
        this.jailEndTime = jailEndTime;
        this.jailLocation = jailLocation;
        this.unjailLocation = unjailLocation;
    }

    public boolean isJailed() {
        return isJailed;
    }

    public void setJailed(boolean jailed) {
        isJailed = jailed;
    }

    public long getJailEndTime() {
        return jailEndTime;
    }

    public void setJailEndTime(long jailEndTime) {
        this.jailEndTime = jailEndTime;
    }

    public Location getJailLocation() {
        return jailLocation;
    }

    public void setJailLocation(Location jailLocation) {
        this.jailLocation = jailLocation;
    }

    public Location getUnjailLocation() {
        return unjailLocation;
    }

    public void setUnjailLocation(Location unjailLocation) {
        this.unjailLocation = unjailLocation;
    }
}