package com.storycraft.core.combat;

import java.util.ArrayList;
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

    private int hologramLastTick;

    public DamageHologram() {
        this.hologramList = new ArrayList<>();
        this.hologramLastTick = 20;
    }

    public int getHologramLastTime() {
        return hologramLastTick;
    }

    public void setHologramLastTime(int hologramLastTime) {
        this.hologramLastTick = hologramLastTime;
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

    public void onUpdate(ServerUpdateEvent e) {
        if (!e.isUpdateType(UpdateType.SECOND) || this.hologramList.isEmpty()) {
            return;
        }

        for (HologramTimeData data : new ArrayList<>(this.hologramList)) {
            if (data.getCreatedAt() + this.hologramLastTick < System.currentTimeMillis()) {
                getPlugin().getDecorator().getHologramManager().removeHologram(data.getHologram());
                this.hologramList.remove(data);
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
