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
        messenger.registerIncomingPluginChannel(getPlugin(), ProjectVariable.CHANNEL, new BukkitPluginMessageListener(this));

        getCommand("proxiedwarp").setExecutor(new ProxiedWarpCommand(this));

        getConfig().addDefault("command-prefix", "warp");
        getConfig().addDefault("need-permission", false);
        getConfig().addDefault("whitelisted-servers", new String[]{});
        getConfig().addDefault("whitelisted-warps", new String[]{});
        getConfig().addDefault("message.usage", getMessageUsage());
        getConfig().addDefault("message.permission", getMessagePermission());
        getConfig().addDefault("message.server-not-whitelisted", getMessageServerNotWhitelisted());
        getConfig().addDefault("message.warp-not-whitelisted", getMessageWarpNotWhitelisted());
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    JavaPlugin getPlugin();

    PluginCommand getCommand(String command);

    FileConfiguration getConfig();
    void saveConfig();

    String getMessageUsage();
    String getMessagePermission();
    String getMessageServerNotWhitelisted();
    String getMessageWarpNotWhitelisted();

    void sendMessage(CommandSender sender, String key, String fallback);
}
