package com.storycraft.core.entity;

import com.storycraft.StoryPlugin;
import com.storycraft.StoryMiniPlugin;
import com.storycraft.MiniPluginLoader;
import com.storycraft.core.entity.movement.CrawlMovement;

public class EntityManager extends StoryMiniPlugin {

    private CrawlMovement crawlMovement;

    @Override
    public void onLoad(StoryPlugin plugin) {
        MiniPluginLoader<StoryPlugin> loader = plugin.getMiniPluginLoader();

        loader.addMiniPlugin(crawlMovement = new CrawlMovement());
    }

    public CrawlMovement getCrawlMovement() {
        return crawlMovement;
    }
}