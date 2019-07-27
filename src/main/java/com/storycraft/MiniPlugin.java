package com.storycraft;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class MiniPlugin {

    private StoryPlugin plugin;
    private boolean enabled = false;

    public void onLoad(StoryPlugin plugin) {

    }

    public void onEnable() {

    }

    public void onDisable(boolean reload) {

    }

    public StoryPlugin getPlugin() {
        return plugin;
    }

    protected void setPlugin(StoryPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isEnabled() {
        return enabled;
    }

    protected void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isMainThread(){
        return getPlugin().getServer().isPrimaryThread();
    }

    public <T>Future<T> runSync(Callable<T> callable){
        return getPlugin().getServer().getScheduler().callSyncMethod(getPlugin(), callable);
    }
}