package com.storycraft.server.world.addon;

import com.storycraft.StoryPlugin;
import com.storycraft.server.world.IWorldAddon;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPhysicsEvent;

public class NoFluidPhysicsAddon implements IWorldAddon {

    @Override
    public AddonHandler createHandler(StoryPlugin plugin, World world) {
		return new NoFluidPhysicsAddonHandler(plugin, this, world);
    }
    
    public class NoFluidPhysicsAddonHandler extends AddonHandler {

        protected NoFluidPhysicsAddonHandler(StoryPlugin plugin,IWorldAddon addon, World world) {
            super(plugin, addon, world);
        }
        
        @EventHandler
        public void onPhysics(BlockPhysicsEvent e) {
            if (isTargetWorld(e.getSourceBlock().getWorld()) && e.getSourceBlock().isLiquid())
                e.setCancelled(true);
        }

    }

}