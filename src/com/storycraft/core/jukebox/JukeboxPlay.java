package com.storycraft.core.jukebox;

import com.storycraft.core.MiniPlugin;
import com.storycraft.core.hologram.Hologram;
import com.storycraft.core.hologram.ShortHologram;
import com.storycraft.server.update.ServerUpdateEvent;
import net.minecraft.server.v1_12_R1.Entity;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.Jukebox;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JukeboxPlay extends MiniPlugin implements Listener {

    private Map<Jukebox, Hologram> jukeBoxHologramMap;

    public JukeboxPlay(){
        this.jukeBoxHologramMap = new HashMap<>();
    }

    @Override
    public void onEnable(){
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        Block b = e.getClickedBlock();

        if (b != null && e.getAction() == Action.RIGHT_CLICK_BLOCK && b.getState() instanceof Jukebox){
            Jukebox jukebox = (Jukebox) b.getState();

            jukeBoxHologramMap.put(jukebox, new ShortHologram(jukebox.getLocation().add(0.5d, 1.25d, 0.5d), ChatColor.YELLOW + "Playing music ♪"));
        }
    }

    @EventHandler
    public void onUpdate(ServerUpdateEvent e){
        for (Jukebox jukebox : new ArrayList<>(jukeBoxHologramMap.keySet())){
            Hologram hologram = jukeBoxHologramMap.get(jukebox);
            if (jukebox.isPlaying()){
                Location location = jukebox.getLocation();

                location.getWorld().spawnParticle(Particle.NOTE, location.add(0.5d, 0.5d, 0.5d), 4, 0.75d, 0.75d ,0.75d, 0.25d);
            }
            else{
                getPlugin().getDecorator().getHologramManager().removeHologram(hologram);
                jukeBoxHologramMap.remove(jukebox);
            }
        }
    }
}
