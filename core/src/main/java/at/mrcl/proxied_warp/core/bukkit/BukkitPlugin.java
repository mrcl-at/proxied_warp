package at.mrcl.proxied_warp.core.bukkit;

import at.mrcl.proxied_warp.core.ProjectVariable;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public interface BukkitPlugin {

    default void enable() {
        final var messenger = getPlugin().getServer().getMessenger();
        messenger.registerOutgoingPluginChannel(getPlugin(), ProjectVariable.CHANNEL);
        messenger.registerIncomingPluginChannel(getPlugin(), ProjectVariable.CHANNEL, new BukkitWarpPluginMessageListener(this));
        messenger.registerIncomingPluginChannel(getPlugin(), ProjectVariable.CHANNEL_MESSAGE, new BukkitMessagePluginMessageListener(this));

        getCommand("proxiedwarp").setExecutor(new ProxiedWarpCommand(this));
        getCommand("proxiedwarpother").setExecutor(new ProxiedWarpOtherCommand(this));

        getConfig().options().header("ProxiedWarp by mrcl - https://github.com/mrcl-at/proxied_warp");
        getConfig().addDefault("command-prefix", "warp");
        getConfig().addDefault("need-permission", false);
        getConfig().addDefault("whitelisted-servers", new String[]{});
        getConfig().addDefault("whitelisted-warps", new String[]{});
        getConfig().addDefault("aliases", null);
        getConfig().addDefault("message.usage", getMessageUsage());
        getConfig().addDefault("message.usage-other", getMessageUsageOther());
        getConfig().addDefault("message.permission", getMessagePermission());
        getConfig().addDefault("message.server-not-whitelisted", getMessageServerNotWhitelisted());
        getConfig().addDefault("message.warp-not-whitelisted", getMessageWarpNotWhitelisted());
        getConfig().addDefault("message.player-not-online", getMessagePlayerNotOnline());
        getConfig().addDefault("message.key-not-found", getMessageKeyNotFound());
        getConfig().addDefault("message.server-not-found", getMessageServerNotFound());
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    JavaPlugin getPlugin();

    PluginCommand getCommand(String command);

    FileConfiguration getConfig();
    void saveConfig();

    String getMessageUsage();
    String getMessageUsageOther();
    String getMessagePermission();
    String getMessageServerNotWhitelisted();
    String getMessageWarpNotWhitelisted();
    String getMessagePlayerNotOnline();
    String getMessageKeyNotFound();
    String getMessageServerNotFound();

    default String getMessage(String key, String fallback) {
        var message = getConfig().getString("message." + key);
        return message == null ? fallback : message;
    }
    void sendMessage(CommandSender sender, String key, String fallback);
    void sendMessage(CommandSender sender, String key);
}
