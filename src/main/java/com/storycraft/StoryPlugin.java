package com.storycraft;

import com.storycraft.command.CommandManager;
import com.storycraft.config.json.JsonConfigFile;
import com.storycraft.config.json.JsonConfigPrettyFile;
import com.storycraft.core.CoreManager;
import com.storycraft.core.command.SayCommand;

import org.bukkit.ChatColor;

import java.lang.instrument.Instrumentation;

public class StoryPlugin extends MainPlugin {

    private JsonConfigFile serverConfig;

    private MiniPluginLoader<StoryPlugin> miniPluginLoader;

    private CoreManager coreManager;

    public StoryPlugin() {
        this.coreManager = new CoreManager(this);
    }

    public CoreManager getCoreManager() {
        return coreManager;
    }

    @Override
    protected void createMiniPluginLoader() {
        this.miniPluginLoader = new MiniPluginLoader<StoryPlugin>(this);
    }

    @Override
    public MiniPluginLoader<StoryPlugin> getMiniPluginLoader() {
        return miniPluginLoader;
    }

    @Override
    protected void onPostLoad() {
        try {
            getConfigManager().addConfigFile("server.json", serverConfig = new JsonConfigPrettyFile()).getSync();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostEnable() {

    }

    @Override
    protected void registerCommand() {
        CommandManager manager = getCommandManager();

        manager.addCommand(new SayCommand());
    }

    @Override
    protected void registerMiniPlugin() {
        MiniPluginLoader<StoryPlugin> loader = getMiniPluginLoader();
        
        loader.addMiniPlugin(getCoreManager());
    }

    public JsonConfigFile getServerConfig() {
        return serverConfig;
    }

    public String getServerName(){
        try {
            return serverConfig.get("server-name").getAsString();
        } catch (Exception e) {
            String defaultName = ChatColor.GREEN + "@";

            serverConfig.set("server-name", defaultName);

            return defaultName;
        }
    }

    public String getServerHomepage(){
        try {
            return serverConfig.get("server-web").getAsString();
        } catch (Exception e) {
            String defaultURL = "https://";

            serverConfig.set("server-web", defaultURL);

            return defaultURL;
        }
    }

    public static void main(String[] args){
        System.out.println("이 프로그램은 단독 실행 될수 없습니다");
    }

    public static void premain(String args, Instrumentation inst) throws Exception {
        System.out.println("Story Server Preloaded");
    }
}
