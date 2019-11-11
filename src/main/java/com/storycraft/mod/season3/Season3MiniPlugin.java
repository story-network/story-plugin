package com.storycraft.mod.season3;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

import com.storycraft.StoryMiniPlugin;
import com.storycraft.StoryPlugin;
import com.storycraft.config.json.JsonConfigEntry;
import com.storycraft.config.json.JsonConfigFile;
import com.storycraft.config.json.JsonConfigPrettyFile;
import com.storycraft.server.advancement.AdvancementType;
import com.storycraft.server.hologram.Hologram;
import com.storycraft.server.hologram.ShortHologram;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class Season3MiniPlugin extends StoryMiniPlugin implements Listener {

    private JsonConfigFile configFile;

    private static UUID combatModifierUUID = UUID.fromString("7e761c07-f525-45f8-9577-86daaec40b10");
    private static UUID entityModifierUUID = UUID.fromString("538f4c4b-1562-474b-bbc8-6e429ddd817b");
    private static UUID axeNurfModifierUUID = UUID.fromString("538f4c4b-1562-474b-bbc8-6e429ddd817b");

    private static AttributeModifier combatModifier = new AttributeModifier(combatModifierUUID, "StoryNetwork S3 combat advantage", 3, Operation.MULTIPLY_SCALAR_1);
    private static AttributeModifier axeModifier = new AttributeModifier(axeNurfModifierUUID, "StoryNetwork S3 Axe nurf", -0.5, Operation.MULTIPLY_SCALAR_1);

    private Hologram spawnHologram;

    private Location spawnLocation;

    @Override
    public void onLoad(StoryPlugin plugin) {
        plugin.getConfigManager().addConfigFile("session3.json", configFile = new JsonConfigPrettyFile()).run();
    }

    @Override
    public void onEnable() {
        this.spawnLocation = getPlugin().getCoreManager().getServerSpawnManager().getSpawnLocation();

        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());

        getPlugin().getServer().getScheduler().runTask(getPlugin(), () -> {
            this.spawnLocation.setWorld(getPlugin().getDefaultWorld());
            this.spawnLocation.setY(getPlugin().getDefaultWorld().getHighestBlockAt(176, 248).getY());
        
            this.spawnHologram = new ShortHologram(this.spawnLocation.clone().add(0, 1.75, 0), ChatColor.AQUA + "스폰지점", ChatColor.WHITE + "스폰 주변 " + getPlugin().getCoreManager().getServerSpawnManager().getSpawnRadius() + "블록은 보호되어있습니다", ChatColor.LIGHT_PURPLE + "Story Network S3");
    
            getPlugin().getDecorator().getHologramManager().addHologram(getSpawnHologram());
        });
    }

    @Override
    public void onDisable(boolean reload) {
        getPlugin().getDecorator().getHologramManager().removeHologram(getSpawnHologram());
    }

    public Hologram getSpawnHologram() {
        return spawnHologram;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
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

        p.getAttribute(Attribute.GENERIC_ATTACK_SPEED).addModifier(combatModifier);
        
        p.teleportAsync(getSpawnLocation()).thenApply((Boolean b) -> {
            getPlugin().getDecorator().getAdvancementManager().sendToastToPlayer(p, "StoryServer 플레이어 프로필 생성 완료", AdvancementType.CHALLENGE, new ItemStack(Material.ENCHANTED_GOLDEN_APPLE));
            
            return null;
        });
    }

    @EventHandler
    public void onItemChanged(PlayerItemHeldEvent e) {
        ItemStack item = e.getPlayer().getInventory().getItem(e.getNewSlot());

        if (item == null) {
            return;
        }

        AttributeInstance attackSpeed = e.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_SPEED);

        attackSpeed.removeModifier(axeModifier);

        if (item.getType() == Material.WOODEN_AXE || item.getType() == Material.STONE_AXE || item.getType() == Material.IRON_AXE || item.getType() == Material.DIAMOND_AXE) {
            attackSpeed.addModifier(axeModifier);
        }
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent e) {
        if (e.getEntity() instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) e.getEntity();

            AttributeModifier entityModifier = new AttributeModifier(entityModifierUUID, "StoryNetwork S3 entity advantage", 0.3 + Math.random() * 1.2, Operation.MULTIPLY_SCALAR_1);
            AttributeInstance hp = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            Iterator<AttributeModifier> modifierIter = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getModifiers().iterator();
            while (modifierIter.hasNext()) {
                AttributeModifier modifier = modifierIter.next();

                if (modifier.getUniqueId().equals(entityModifierUUID)) {
                    modifierIter.remove();
                }
            }
            
            hp.addModifier(entityModifier);
            entity.setHealth(entity.getMaxHealth());
        }
    }

    @EventHandler
    public void onEntityDie(EntityDeathEvent e) {
        if (e.getEntity() != null) {
            LivingEntity entity = e.getEntity();

            Iterator<AttributeModifier> modifierIter = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getModifiers().iterator();

            while (modifierIter.hasNext()) {
                AttributeModifier modifier = modifierIter.next();

                if (modifier.getUniqueId().equals(entityModifierUUID)) {
                    e.setDroppedExp((int) Math.round(e.getDroppedExp() * (0.7 + modifier.getAmount())));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!hasJoined(e.getPlayer().getUniqueId())) {
            firstJoinHandler(e.getPlayer());
        }
    }

    @EventHandler
    public void onSpawnerBroken(BlockBreakEvent e) {
        if (e.getBlock() == null || e.isCancelled()|| !(e.getBlock().getState() instanceof CreatureSpawner)) {
            return;
        }

        Player p = e.getPlayer();
        ItemStack mineItem = p.getInventory().getItemInMainHand();

        if (mineItem == null || !mineItem.containsEnchantment(Enchantment.SILK_TOUCH)) {
            return;
        }

        Block block = e.getBlock();

        Collection<ItemStack> drops = block.getDrops();
        drops.clear();
        drops.add(new ItemStack(Material.SPAWNER));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        
    }
}
