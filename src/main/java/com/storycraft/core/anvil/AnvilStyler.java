package com.storycraft.core.anvil;

import com.storycraft.StoryMiniPlugin;
import com.storycraft.server.event.client.AsyncAnvilNameEvent;
import com.storycraft.util.NMSUtil;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.minecraft.server.v1_15_R1.ContainerAnvil;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.SharedConstants;

public class AnvilStyler extends StoryMiniPlugin implements Listener {

    @Override
    public void onEnable() {
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    @EventHandler
    public void onNameItem(AsyncAnvilNameEvent e) {
        e.setCancelled(true);

        final EntityPlayer player = NMSUtil.getNMSPlayer(e.getPlayer());

        if (player.activeContainer instanceof ContainerAnvil) {
            if (e.getName().length() > 35)
                return;

            runSync(() -> {
                ContainerAnvil container = (ContainerAnvil) player.activeContainer;

                String styledName = ChatColor.translateAlternateColorCodes('&', SharedConstants.a(e.getName()));

                container.a(styledName);
                return null;
            });
        }
    }

}