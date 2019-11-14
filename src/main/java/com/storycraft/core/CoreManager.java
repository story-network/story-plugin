package com.storycraft.core;

import com.storycraft.MiniPluginLoader;
import com.storycraft.StoryMiniPlugin;
import com.storycraft.StoryPlugin;
import com.storycraft.core.anvil.AnvilStyler;
import com.storycraft.core.broadcast.BroadcastManager;
import com.storycraft.core.broadcast.ToastCommand;
import com.storycraft.core.chat.ChatManager;
import com.storycraft.core.chat.ColoredChat;
import com.storycraft.core.combat.DamageHologram;
import com.storycraft.core.discord.DiscordChatHook;
import com.storycraft.core.disguise.HeadDisguise;
import com.storycraft.core.dropping.DropCounter;
import com.storycraft.core.dropping.HologramXPDrop;
import com.storycraft.core.entity.EntityBlood;
import com.storycraft.core.entity.EntityManager;
import com.storycraft.core.explosion.Explosion;
import com.storycraft.core.faq.FAQCommand;
import com.storycraft.core.fly.FlyCommand;
import com.storycraft.core.jukebox.JukeboxPlay;
import com.storycraft.core.map.ImageMap;
import com.storycraft.core.payload.PayloadBrandEditor;
import com.storycraft.core.permission.PermissionManager;
import com.storycraft.core.player.PlayerManager;
import com.storycraft.core.playerlist.CustomPlayerList;
import com.storycraft.core.punish.PunishManager;
import com.storycraft.core.randomtp.RandomTP;
import com.storycraft.core.rank.RankManager;
import com.storycraft.core.saving.AutoSaveManager;
import com.storycraft.core.skin.PlayerCustomSkin;
import com.storycraft.core.spawn.ServerSpawnManager;
import com.storycraft.core.teleport.TeleportAskCommand;
import com.storycraft.core.uuid.UUIDRevealCommand;
import com.storycraft.core.world.WorldTeleporter;
import com.storycraft.mod.ModManager;

public class CoreManager extends StoryMiniPlugin {

    private StoryPlugin plugin;

    private PlayerManager playerManager;
    private EntityManager entityManager;

    private ServerSpawnManager serverSpawnManager;

    private PunishManager punishManager;

    private RankManager rankManager;

    private ModManager modManager;

    private DiscordChatHook discordChat;

    public CoreManager(StoryPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public StoryPlugin getPlugin() {
        return plugin;
    }

    public ServerSpawnManager getServerSpawnManager() {
        return serverSpawnManager;
    }

    public MiniPluginLoader<StoryPlugin> getMiniPluginLoader() {
        return getPlugin().getMiniPluginLoader();
    }

    @Override
    public void onLoad(StoryPlugin plugin) {
        if (plugin != getPlugin()) {
            throw new RuntimeException("Illegal load");
        }

        preInitStoryMiniPlugin();

        initStoryMiniPlugin();
    }

    protected void preInitStoryMiniPlugin() {
        MiniPluginLoader<StoryPlugin> loader = getMiniPluginLoader();
        loader.addMiniPlugin(rankManager = new RankManager());
        loader.addMiniPlugin(playerManager = new PlayerManager());
        loader.addMiniPlugin(entityManager = new EntityManager());
        loader.addMiniPlugin(punishManager = new PunishManager());
    }

    protected void initStoryMiniPlugin() {
        MiniPluginLoader<StoryPlugin> loader = getMiniPluginLoader();
        loader.addMiniPlugin(new PermissionManager());
        loader.addMiniPlugin(new Explosion());
        loader.addMiniPlugin(serverSpawnManager = new ServerSpawnManager());
        loader.addMiniPlugin(new ChatManager());
        loader.addMiniPlugin(new EntityBlood());
        loader.addMiniPlugin(new DropCounter());
        loader.addMiniPlugin(new RandomTP());
        loader.addMiniPlugin(discordChat = new DiscordChatHook());
        loader.addMiniPlugin(new DamageHologram());
        loader.addMiniPlugin(new JukeboxPlay());
        loader.addMiniPlugin(new BroadcastManager());
        loader.addMiniPlugin(new ColoredChat());
        loader.addMiniPlugin(new FlyCommand());
        loader.addMiniPlugin(new FAQCommand());
        loader.addMiniPlugin(new AnvilStyler());
        loader.addMiniPlugin(new ToastCommand());
        loader.addMiniPlugin(new HeadDisguise());
        loader.addMiniPlugin(new HologramXPDrop());
        loader.addMiniPlugin(new UUIDRevealCommand());
        loader.addMiniPlugin(new AutoSaveManager());
        loader.addMiniPlugin(new WorldTeleporter());
        loader.addMiniPlugin(new TeleportAskCommand());
        loader.addMiniPlugin(new PlayerCustomSkin());
        loader.addMiniPlugin(new CustomPlayerList());
        loader.addMiniPlugin(new ImageMap());
        loader.addMiniPlugin(new PayloadBrandEditor());

        postInitStoryMiniPlugin();
    }

    protected void postInitStoryMiniPlugin() {
        MiniPluginLoader<StoryPlugin> loader = getMiniPluginLoader();

        loader.addMiniPlugin(modManager = new ModManager(getPlugin()));
    }

    public ModManager getModManager() {
        return modManager;
    }

    public DiscordChatHook getDiscordChat() {
        return discordChat;
    }

    public RankManager getRankManager() {
        return rankManager;
    }

    public PunishManager getPunishManager() {
        return punishManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }
}