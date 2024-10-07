package com.misterdanusit.anticheat.listeners;

import com.misterdanusit.anticheat.MisterDanusitAntiCheat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.UUID;

public class PlayerListener implements Listener {

    private final MisterDanusitAntiCheat plugin;
    private final HashMap<UUID, Integer> playerWarnings = new HashMap<>();

    private static final double MAX_ALLOWED_SPEED = 0.6;
    private static final int MAX_WARNINGS = 3;

    public PlayerListener(MisterDanusitAntiCheat plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        // คำนวณความเร็วแนวนอน
        double deltaX = event.getTo().getX() - event.getFrom().getX();
        double deltaZ = event.getTo().getZ() - event.getFrom().getZ();
        double horizontalSpeed = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        // ตรวจจับ Speedhack
        if (horizontalSpeed > MAX_ALLOWED_SPEED) {
            issueWarning(player, "Speedhack");
        }

        // ตรวจจับ Flyhack
        if (!player.isOnGround() && horizontalSpeed > 0.0) {
            issueWarning(player, "Flyhack");
        }
    }

    // ตรวจจับ KillAura
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            issueWarning(player, "KillAura");
        }
    }

    // ฟังก์ชันออกคำเตือนและบันทึกข้อมูล
    private void issueWarning(Player player, String hackType) {
        UUID playerId = player.getUniqueId();
        int warnings = playerWarnings.getOrDefault(playerId, 0) + 1;

        playerWarnings.put(playerId, warnings);
        player.sendMessage("§c[AntiCheat] " + hackType + " Detected! Warning: " + warnings + "/" + MAX_WARNINGS);
        plugin.getLogger().warning("Player " + player.getName() + " detected using " + hackType + " (Warning: " + warnings + ")");

        // บันทึกข้อมูลการโกงลงไฟล์
        plugin.logCheating(player.getName(), hackType, "Detected " + warnings + " time(s)");

        // ลงโทษผู้เล่น
        if (warnings >= MAX_WARNINGS) {
            punishPlayer(player);
        }
    }

    private void punishPlayer(Player player) {
        player.sendMessage("§c[AntiCheat] You have been kicked for using hacks!");
        player.kickPlayer("You have been kicked for repeated use of hacks!");
        playerWarnings.remove(player.getUniqueId());
        plugin.getLogger().info("Player " + player.getName() + " has been kicked for hacking.");
    }
}
