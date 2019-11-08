package com.storycraft.core.combat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.storycraft.StoryMiniPlugin;
import com.storycraft.server.event.server.ServerUpdateEvent;
import com.storycraft.server.event.server.ServerUpdateEvent.UpdateType;
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

    private List<HologramTimeData> hologramList;

    private int hologramLastTime;

    public DamageHologram() {
        this.hologramList = new ArrayList<>();
        this.hologramLastTime = 1250;
    }

    public int getHologramLastTime() {
        return hologramLastTime;
    }

    public void setHologramLastTime(int hologramLastTime) {
        this.hologramLastTime = hologramLastTime;
    }
    
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
        this.hologramList.add(new HologramTimeData(System.currentTimeMillis(), hologram));
    }

    @EventHandler
    public void onUpdate(ServerUpdateEvent e) {
        if (this.hologramList.isEmpty()) {
            return;
        }

        long now = System.currentTimeMillis();
        Iterator<HologramTimeData> iter = this.hologramList.iterator();
        while (iter.hasNext()) {
            HologramTimeData data = iter.next();
            if (data.getCreatedAt() + this.hologramLastTime < now) {
                getPlugin().getDecorator().getHologramManager().removeHologram(data.getHologram());
                iter.remove();
            }
        }
    }

    public class HologramTimeData {

        private long createdAt;
        private Hologram hologram;

        public HologramTimeData(long createdAt, Hologram hologram) {
            this.createdAt = createdAt;
            this.hologram = hologram;
        }
        
        public long getCreatedAt() {
            return createdAt;
        }

        public Hologram getHologram() {
            return hologram;
        }

    }
}
