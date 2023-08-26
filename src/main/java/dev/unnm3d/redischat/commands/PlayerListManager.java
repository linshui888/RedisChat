package dev.unnm3d.redischat.commands;

import dev.unnm3d.redischat.RedisChat;
import dev.unnm3d.redischat.api.VanishIntegration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerListManager {
    private final RedisChat plugin;
    private final BukkitTask task;
    private final ConcurrentHashMap<String, Long> playerList;
    private final List<VanishIntegration> vanishIntegrations;


    public PlayerListManager(RedisChat plugin) {
        this.plugin = plugin;
        this.playerList = new ConcurrentHashMap<>();
        this.vanishIntegrations = new ArrayList<>();
        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                playerList.entrySet().removeIf(stringLongEntry -> System.currentTimeMillis() - stringLongEntry.getValue() > 1000 * 6);
                sendPlayerListUpdate();
            }
        }.runTaskTimerAsynchronously(plugin, 0, 80);//4 seconds
    }

    public void sendPlayerListUpdate() {
        List<String> tempList = plugin.getServer().getOnlinePlayers().stream()
                .filter(player -> vanishIntegrations.stream().noneMatch(integration -> integration.isVanished(player)))
                .map(HumanEntity::getName)
                .filter(s -> !s.isEmpty())
                .toList();
        plugin.getDataManager().publishPlayerList(tempList);

        tempList.forEach(s -> playerList.put(s, System.currentTimeMillis()));
    }

    public void updatePlayerList(List<String> inPlayerList) {
        long currentTimeMillis = System.currentTimeMillis();
        inPlayerList.forEach(s -> {
            if (s != null && !s.isEmpty())
                playerList.put(s, currentTimeMillis);
        });
    }

    public void addVanishIntegration(VanishIntegration vanishIntegration) {
        vanishIntegrations.add(vanishIntegration);
    }

    public Set<String> getPlayerList() {
        return playerList.keySet();
    }

    public void stop() {
        task.cancel();
    }

}
