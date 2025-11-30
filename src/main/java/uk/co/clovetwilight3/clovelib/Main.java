/*
 * Copyright (c) 2025 Clove Twilight
 * Licensed under the MIT License
 */

package uk.co.clovetwilight3;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import uk.co.clovetwilight3.clovelib.BanData;
import uk.co.clovetwilight3.clovelib.JailData;
import uk.co.clovetwilight3.clovelib.events.PlayerBannedEvent;
import uk.co.clovetwilight3.clovelib.events.PlayerUnbannedEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main extends JavaPlugin {
    private static Main instance;

    // Shared data maps
    private final Map<UUID, JailData> jailDataMap = new HashMap<>();
    private final Map<UUID, BanData> banDataMap = new HashMap<>();

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private File banDataFile;

    @Override
    public void onEnable() {
        instance = this;

        // Initialize data storage
        saveDefaultConfig();
        setupDataFiles();
        loadBanData();

        getLogger().info("CloveLib has been initialized!");
    }

    @Override
    public void onDisable() {
        saveBanData();
        getLogger().info("CloveLib is shutting down...");
    }

    // Singleton instance getter
    public static Main getInstance() {
        return instance;
    }

    // ==================== File Storage Setup ====================

    private void setupDataFiles() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        banDataFile = new File(getDataFolder(), "bans.json");
    }

    private void loadBanData() {
        if (!banDataFile.exists()) {
            return;
        }

        try (FileReader reader = new FileReader(banDataFile)) {
            Type type = new TypeToken<Map<String, BanData>>(){}.getType();
            Map<String, BanData> loadedData = gson.fromJson(reader, type);
            if (loadedData != null) {
                for (Map.Entry<String, BanData> entry : loadedData.entrySet()) {
                    banDataMap.put(UUID.fromString(entry.getKey()), entry.getValue());
                }
            }
            getLogger().info("Loaded " + banDataMap.size() + " ban records.");
        } catch (IOException e) {
            getLogger().warning("Failed to load ban data: " + e.getMessage());
        }
    }

    private void saveBanData() {
        try (FileWriter writer = new FileWriter(banDataFile)) {
            Map<String, BanData> saveData = new HashMap<>();
            for (Map.Entry<UUID, BanData> entry : banDataMap.entrySet()) {
                saveData.put(entry.getKey().toString(), entry.getValue());
            }
            gson.toJson(saveData, writer);
            getLogger().info("Saved " + banDataMap.size() + " ban records.");
        } catch (IOException e) {
            getLogger().warning("Failed to save ban data: " + e.getMessage());
        }
    }

    // ==================== Ban System ====================

    /**
     * Ban a player
     * @param playerUUID The UUID of the player to ban
     * @param playerName The username of the player
     * @param reason The ban reason
     * @param bannedBy The name of who issued the ban
     * @param bannedByUUID The UUID of who issued the ban (null for console)
     */
    public void banPlayer(UUID playerUUID, String playerName, String reason, String bannedBy, UUID bannedByUUID) {
        BanData banData = new BanData(playerUUID, playerName, reason, bannedBy, bannedByUUID, System.currentTimeMillis());
        banDataMap.put(playerUUID, banData);
        saveBanData();

        // Fire the ban event for other plugins to listen to
        PlayerBannedEvent event = new PlayerBannedEvent(playerUUID, playerName, reason, bannedBy, bannedByUUID);
        Bukkit.getPluginManager().callEvent(event);

        // Kick the player if they're online
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null && player.isOnline()) {
            player.kickPlayer("§cYou have been banned!\n§7Reason: " + reason);
        }

        getLogger().info(playerName + " has been banned by " + bannedBy + ". Reason: " + reason);
    }

    /**
     * Unban a player
     * @param playerUUID The UUID of the player to unban
     * @param unbannedBy The name of who removed the ban
     */
    public void unbanPlayer(UUID playerUUID, String unbannedBy) {
        BanData banData = banDataMap.remove(playerUUID);
        if (banData != null) {
            saveBanData();

            // Fire the unban event for other plugins to listen to
            PlayerUnbannedEvent event = new PlayerUnbannedEvent(playerUUID, banData.getPlayerName(), unbannedBy);
            Bukkit.getPluginManager().callEvent(event);

            getLogger().info(banData.getPlayerName() + " has been unbanned by " + unbannedBy);
        }
    }

    /**
     * Check if a player is banned
     * @param playerUUID The UUID to check
     * @return true if banned, false otherwise
     */
    public boolean isPlayerBanned(UUID playerUUID) {
        return banDataMap.containsKey(playerUUID);
    }

    /**
     * Get ban data for a player
     * @param playerUUID The UUID to look up
     * @return BanData or null if not banned
     */
    public BanData getBanData(UUID playerUUID) {
        return banDataMap.get(playerUUID);
    }

    /**
     * Get all current bans
     * @return Map of UUID to BanData
     */
    public Map<UUID, BanData> getAllBans() {
        return new HashMap<>(banDataMap);
    }

    // ==================== Jail System ====================

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

    // ==================== Command Utilities ====================

    /**
     * Check if a player can use a command (not jailed)
     * @param player The player to check
     * @param commandName The command name (for future permission integration)
     * @return true if the player can use the command
     */
    public boolean canUseCommand(Player player, String commandName) {
        UUID playerUUID = player.getUniqueId();
        if (isPlayerJailed(playerUUID)) {
            return false;
        }
        return true;
    }
}