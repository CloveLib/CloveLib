package uk.co.clovetwilight3.clovelib;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main extends JavaPlugin {
    private static Main instance;

    // Shared data maps
    private final Map<UUID, JailData> jailDataMap = new HashMap<>();

    // Jail-related data structure
    public static class JailData {
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

        // Getters and setters
        public boolean isJailed() { return isJailed; }
        public void setJailed(boolean jailed) { isJailed = jailed; }
        public long getJailEndTime() { return jailEndTime; }
        public void setJailEndTime(long jailEndTime) { this.jailEndTime = jailEndTime; }
        public Location getJailLocation() { return jailLocation; }
        public Location getUnjailLocation() { return unjailLocation; }
    }

    @Override
    public void onEnable() {
        // Initialize the singleton instance
        if (instance == null) {
            instance = this;
            getLogger().info("CloveLib instance has been set successfully.");
        } else {
            getLogger().warning("CloveLib instance was already set! Possible duplicate initialization.");
        }

        // Plugin-specific initialization
        getLogger().info("CloveLib has been initialized!");
    }


    @Override
    public void onDisable() {
        getLogger().info("CloveLib is shutting down...");
    }

    // Singleton instance getter
    public static CloveLib getInstance(){
        return instance;
    }

    // Jail-related methods
    public void setPlayerJailData(UUID playerUUID, JailData jailData) {
        jailDataMap.put(playerUUID, jailData);
    }

    public JailData getPlayerJailData(UUID playerUUID) {
        return jailDataMap.get(playerUUID);
    }

    public boolean isPlayerJailed(UUID playerUUID) {
        JailData jailData = jailDataMap.get(playerUUID);
        return jailData != null && jailData.isJailed();
    }

    public void clearPlayerJailData(UUID playerUUID) {
        jailDataMap.remove(playerUUID);
    }

    // Command utility to check if a player can use a command
    public boolean canUseCommand(Player player, String permission) {
        UUID playerUUID = player.getUniqueId();
        if (isPlayerJailed(playerUUID)) {
            player.sendMessage("You are jailed and cannot use this command!");
            return false;
        }
        return true;
    }
}