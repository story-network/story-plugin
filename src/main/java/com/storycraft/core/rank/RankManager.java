package com.storycraft.core.rank;

import com.google.gson.JsonArray;
import com.storycraft.StoryPlugin;
import com.storycraft.command.ICommand;
import com.storycraft.config.json.JsonConfigFile;
import com.storycraft.config.json.JsonConfigPrettyFile;
import com.storycraft.StoryMiniPlugin;
import com.storycraft.util.ConnectionUtil;
import com.storycraft.util.MessageUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.PermissionAttachmentInfo;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityStatus;

import java.util.StringJoiner;

public class RankManager extends StoryMiniPlugin implements ICommand, Listener {

    public static final ServerRank DEFAULT_RANK = ServerRank.USER;

    private JsonConfigFile configFile;

    @Override
    public void onLoad(StoryPlugin plugin) {
        try {
            plugin.getConfigManager().addConfigFile("rank.json", configFile = new JsonConfigPrettyFile()).getSync();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        plugin.getCommandManager().addCommand(this);
    }

    @Override
    public void onEnable() {
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());

        for (Player p : getPlugin().getServer().getOnlinePlayers()) {
            updatePlayerName(p);
        }
    }

    @Override
    public void onDisable(boolean reload) {
        for (Player p : getPlugin().getServer().getOnlinePlayers()) {
            p.setPlayerListName(p.getName());
        }
    }

    public ServerRank getRank(Player p) {
        if (!configFile.contains(p.getUniqueId().toString())) {
            setRank(p, DEFAULT_RANK);
            return DEFAULT_RANK;
        }

        try {
            return ServerRank.valueOf(configFile.get(p.getUniqueId().toString()).getAsString());
        } catch (Exception e) {
            setRank(p, DEFAULT_RANK);
            return DEFAULT_RANK;
        }
    }

    public void setRank(Player p, ServerRank rank) {
        configFile.set(p.getUniqueId().toString(), rank.name());
    }

    @Override
    public String[] getAliases() {
        return new String[] { "rank" };
    }

    @Override
    public boolean isPermissionRequired() {
	    return true;
    }

    @Override
    public String getPermissionRequired() {
        return "server.command.rank";
    }

    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent e) {
        updatePlayerName(e.getPlayer());
        updatePlayerLevel(e.getPlayer());
    }

    protected void updatePlayerName(Player p) {
        p.setPlayerListName(getRank(p).getNameColor() + p.getName());
    }

    protected void updatePlayerLevel(Player p) {
        ServerRank rank = getRank(p);

        ConnectionUtil.sendPacket(p, new PacketPlayOutEntityStatus(((CraftPlayer)p).getHandle(), (byte) (0x24 + Math.min(Math.max(rank.getRankLevel(), 0), 4))));
    }

    public boolean hasPermission(Player p, ServerRank minRank) {
        return getRank(p).getRankLevel() >= minRank.getRankLevel();
    }

    @EventHandler
    public void onRankUpdate(RankUpdateEvent e) {
        updatePlayerName(e.getPlayer());
        updatePlayerLevel(e.getPlayer());
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "RankManager", "사용법 /rank <set/get/list>"));
            return;
        }

        if ("set".equals(args[0])) {
            if (args.length < 3) {
                sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "RankManager", "사용법 /rank set <플레이어 이름> <랭크>"));
                return;
            }

            String name = args[1];
            String rankName = args[2];
    
            Player p = getPlugin().getServer().getPlayer(name);
    
            if (p == null) {
                sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "RankManager", "플레이어 " + name + " 을(를) 찾을 수 없습니다"));
                return;
            }
    
            boolean found = false;
            for (ServerRank r : ServerRank.values()) {
                if (r.name().equals(rankName)) {
                    found = true;
                    break;
                }
            }
    
            if (!found) {
                sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "RankManager", "알 수 없는 랭크 입니다"));
                return;
            }
    
            ServerRank from = getPlugin().getCoreManager().getRankManager().getRank(p);
            ServerRank rank = ServerRank.valueOf(rankName);
    
            getPlugin().getCoreManager().getRankManager().setRank(p, rank);
            sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.SUCCESS, "RankManager", p.getName() + " 의 랭크를 " + rank.name() + " 로 설정했습니다"));
            getPlugin().getServer().getPluginManager().callEvent(new RankUpdateEvent(p, from, rank));
        }
        else if ("get".equals(args[0])) {
            if (args.length < 2) {
                sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "RankManager", "사용법 /rank get <플레이어 이름>"));
                return;
            }

            String name = args[1];

            Player p = getPlugin().getServer().getPlayer(name);
    
            if (p == null) {
                sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "RankManager", "플레이어 " + name + " 을(를) 찾을 수 없습니다"));
                return;
            }

            sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.ALERT, "RankManager", p.getName() + " 의 랭크는 " + ChatColor.WHITE + getPlugin().getCoreManager().getRankManager().getRank(p).toString() + ChatColor.GRAY + " 입니다"));
        }
        else if ("list".equals(args[0])) {
            StringJoiner sj = new StringJoiner(", ");
                for (ServerRank r : ServerRank.values())
                    sj.add(r.name());
    
            sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "RankManager", "사용가능한 랭크 목록: " + sj.toString()));
        }
        else {
            sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "RankManager", "사용법 /rank <set/get/list>"));
        }
    }

    @Override
    public boolean availableOnConsole() {
        return true;
    }

    @Override
    public boolean availableOnCommandBlock() {
        return false;
    }
}
