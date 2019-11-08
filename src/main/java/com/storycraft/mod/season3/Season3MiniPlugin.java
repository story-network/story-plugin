package com.storycraft.mod.season3;

import java.util.UUID;

import com.storycraft.StoryMiniPlugin;
import com.storycraft.StoryPlugin;
import com.storycraft.config.json.JsonConfigEntry;
import com.storycraft.config.json.JsonConfigFile;
import com.storycraft.config.json.JsonConfigPrettyFile;
import com.storycraft.server.advancement.AdvancementType;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class Season3MiniPlugin extends StoryMiniPlugin implements Listener {

    private JsonConfigFile configFile;

    @Override
    public void onLoad(StoryPlugin plugin) {
        plugin.getConfigManager().addConfigFile("session3.json", configFile = new JsonConfigPrettyFile()).run();
    }

    @Override
    public void onEnable() {
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    @Override
    public void onDisable(boolean reload) {
    }

    public JsonConfigEntry getPlayerProfile(UUID uuid) {
        JsonConfigEntry entry = configFile.getObject(uuid.toString());

        if (entry == null) {
            configFile.set(uuid.toString(), entry = configFile.createEntry());
        }

        return entry;
    }

    public long getFirstJoin(UUID uuid) {
        try {
            return getPlayerProfile(uuid).get("firstJoin").getAsLong();
        } catch (Exception e) {
            getPlayerProfile(uuid).set("firstJoin", -1);

            return -1;
        }
    }

    public boolean hasJoined(UUID uuid) {
        return getFirstJoin(uuid) != -1;
    }

    protected void firstJoinHandler(Player p) {
        getPlayerProfile(p.getUniqueId()).set("firstJoin", System.currentTimeMillis());
        getPlugin().getDecorator().getAdvancementManager().sendToastToPlayer(p, "StoryServer 플레이어 프로필 생성 완료", AdvancementType.TASK, new ItemStack(Material.ENCHANTED_GOLDEN_APPLE));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!hasJoined(e.getPlayer().getUniqueId())) {
            firstJoinHandler(e.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        
    }
}
