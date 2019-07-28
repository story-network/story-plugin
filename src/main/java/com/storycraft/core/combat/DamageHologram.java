package com.storycraft.core.combat;

import com.storycraft.StoryMiniPlugin;
import com.storycraft.server.hologram.Hologram;
import com.storycraft.server.hologram.HologramManager;
import com.storycraft.server.hologram.ShortHologram;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageHologram extends StoryMiniPlugin implements Listener {
    
    @Override
    public void onEnable(){
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent e){
        if (!(e.getEntity() instanceof LivingEntity) || e.isCancelled())
            return;

        HologramManager hologramManager = getPlugin().getDecorator().getHologramManager();
        Hologram hologram = new ShortHologram(e.getEntity().getLocation().add(Math.random() - 0.5d, Math.random() - 0.25d, Math.random() - 0.5d), ChatColor.RED + "" + Math.floor(e.getFinalDamage() * 100) / 100);

        hologramManager.addHologram(hologram);
        getPlugin().getServer().getScheduler().runTaskLater(getPlugin(), new Runnable() {
            @Override
            public void run() {
                hologramManager.removeHologram(hologram);
            }
        }, 25);
    }
}
