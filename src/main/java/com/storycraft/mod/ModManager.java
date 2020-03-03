package com.storycraft.mod;

import com.storycraft.StoryPlugin;
import com.storycraft.mod.season3.Season3MiniPlugin;
import com.storycraft.StoryMiniPlugin;
import com.storycraft.MiniPluginLoader;

public class ModManager extends StoryMiniPlugin {

    private StoryPlugin plugin;

    public ModManager(StoryPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onLoad(StoryPlugin plugin) {
        MiniPluginLoader<StoryPlugin> loader = plugin.getMiniPluginLoader();

    
    }

    @Override
    public void onEnable() {
        
    }

    @Override
    public void onDisable(boolean reload) {

    }

    public StoryPlugin getPlugin() {
        return plugin;
    }

    public MiniPluginLoader<StoryPlugin> getMiniPluginLoader() {
        return getPlugin().getMiniPluginLoader();
    }
}
