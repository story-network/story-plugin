package com.storycraft.core.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;

public class CustomMapTracker {

    private int mapId;

    private List<Player> playerList;

    public CustomMapTracker(int mapId) {
        this.mapId = mapId;

        this.playerList = new ArrayList<>();
    }

    public int getMapId() {
        return mapId;
    }

    public List<Player> getPlayerList() {
        return new ArrayList<>(playerList);
    }

    public boolean contains(Player p) {
        return playerList.contains(p);
    }

    protected void addTracked(Player p) {
        if (!contains(p))
            playerList.add(p);
    }

    protected void removeTracked(Player p) {
        playerList.remove(p);
    }

    public boolean canSeeItemFrame(Player p) {
        for (ItemFrame e : p.getWorld().getEntitiesByClass(ItemFrame.class)) {
            ItemStack item = e.getItem();
            if (e.getLocation().distanceSquared(p.getLocation()) < 16384) {
                if (item != null && item.getType() == Material.MAP) {
                    MapMeta meta = (MapMeta) item.getItemMeta();
    
                    if (meta.hasMapView() && meta.getMapView().getId() == getMapId())
                        return true;
                }
            }
        }

        return false;
    }

    public boolean canSeeItem(Player p) {
        for (ItemStack item : p.getInventory().getContents()) {
            if (item != null && item.getType() == Material.MAP) {
                MapMeta meta = (MapMeta) item.getItemMeta();

                if (meta.hasMapView() && meta.getMapView().getId() == getMapId())
                    return true;
            }
        }

        return false;
    }

    public boolean canSee(Player p) {
        return canSeeItem(p) || canSeeItemFrame(p);
    }

    public void update(Collection<Player> playerList) {
        for (Player p : playerList) {
            boolean flag = contains(p);
            boolean canSee = canSee(p);

            if (flag && !canSee) {
                removeTracked(p);
            }
            else if(!flag && canSee) {
                addTracked(p);
            }
        }
    }

}