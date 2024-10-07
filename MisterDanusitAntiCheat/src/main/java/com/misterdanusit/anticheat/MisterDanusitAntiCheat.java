package com.misterdanusit.anticheat;

import com.misterdanusit.anticheat.listeners.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class MisterDanusitAntiCheat extends JavaPlugin {

    private File cheatLogFile;
    private FileConfiguration cheatLogConfig;

    @Override
    public void onEnable() {
        // สร้างไฟล์บันทึกการโกง (cheat_logs.yml)
        createCheatLogFile();

        // ลงทะเบียน Listener
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getLogger().info("MisterDanusit AntiCheat Enabled!");
    }

    @Override
    public void onDisable() {
        saveCheatLogFile();
        getLogger().info("MisterDanusit AntiCheat Disabled!");
    }

    // สร้างไฟล์สำหรับบันทึกข้อมูลการโกง
    private void createCheatLogFile() {
        cheatLogFile = new File(getDataFolder(), "cheat_logs.yml");
        if (!cheatLogFile.exists()) {
            cheatLogFile.getParentFile().mkdirs();
            saveResource("cheat_logs.yml", false);
        }
        cheatLogConfig = YamlConfiguration.loadConfiguration(cheatLogFile);
    }

    // บันทึกข้อมูลการโกงลงไฟล์
    public void logCheating(String playerName, String hackType, String details) {
        String path = "players." + playerName + "." + hackType;
        cheatLogConfig.set(path, details);
        saveCheatLogFile();
    }

    // เซฟไฟล์บันทึก
    private void saveCheatLogFile() {
        try {
            cheatLogConfig.save(cheatLogFile);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not save cheat_logs.yml!", e);
        }
    }
}
