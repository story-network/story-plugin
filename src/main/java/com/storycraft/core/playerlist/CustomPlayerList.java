package com.storycraft.core.playerlist;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.storycraft.StoryPlugin;
import com.storycraft.config.json.JsonConfigFile;
import com.storycraft.StoryMiniPlugin;
import com.storycraft.config.event.ConfigUpdateEvent;
import com.storycraft.server.event.server.ServerUpdateEvent;
import com.storycraft.server.playerlist.ServerPlayerList;
import com.storycraft.util.ConnectionUtil;
import net.minecraft.server.v1_15_R1.ChatComponentText;
import net.minecraft.server.v1_15_R1.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class CustomPlayerList extends StoryMiniPlugin implements Listener {

    private JsonConfigFile configFile;

    @Override
    public void onLoad(StoryPlugin plugin) {
        plugin.getConfigManager().addConfigFile("tab.json", configFile = new JsonConfigFile()).run();
    }

    @Override
    public void onEnable(){
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());

        readFromConfig();
    }

    @EventHandler
    public void onConfigUpdate(ConfigUpdateEvent e) {
        if (configFile.equals(e.getConfig())) {
            readFromConfig();
        }
    }

    protected void readFromConfig() {
        try {
            if (configFile.contains("header")) {
                JsonArray array = configFile.get("header").getAsJsonArray();
                String[] header = new String[array.size()];

                for (int i = 0; i < header.length; i++) {
                    header[i] = array.get(i).getAsString();
                }

                setHeaderText(header);
            }
            else {
                setHeaderText(new String[] { getPlugin().getServerName() });
            }
        } catch (Exception e) {
            e.printStackTrace();
            setHeaderText(new String[] { getPlugin().getServerName() });
        }

        try {
            if (configFile.contains("footer")) {
                JsonArray array = configFile.get("footer").getAsJsonArray();
                String[] footer = new String[array.size()];

                for (int i = 0; i < footer.length; i++) {
                    footer[i] = array.get(i).getAsString();
                }

                setFooterText(footer);
            }
            else {
                setFooterText(new String[] { getPlugin().getServerName() });
            }
        } catch (Exception e) {
            e.printStackTrace();
            setFooterText(new String[] { getPlugin().getServerName() });
        }
    }

    public ServerPlayerList getServerPlayerList() {
        return getPlugin().getServerManager().getPlayerList();
    }

    public void setHeaderText(String[] headerText) {
        getServerPlayerList().setHeaderText(headerText);
        Gson gson = new Gson();

        configFile.set("header", gson.toJsonTree(headerText));
    }

    public void setFooterText(String[] footerText) {
        getServerPlayerList().setFooterText(footerText);
        Gson gson = new Gson();

        configFile.set("footer", gson.toJsonTree(footerText));
    }
}