package at.mrcl.proxied_warp.core.bukkit;

import at.mrcl.proxied_warp.core.codec.Codec;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jspecify.annotations.NonNull;

public class BukkitWarpPluginMessageListener implements PluginMessageListener {

    private final BukkitPlugin plugin;

    public BukkitWarpPluginMessageListener(BukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPluginMessageReceived(@NonNull String channel, @NonNull Player player, byte @NonNull [] data) {
        // Message: <player> <server> <warp>
        final var codec = Codec.decode(data);

        final var target = Bukkit.getPlayer(codec.player());
        if (target == null) {
            plugin.getPlugin().getLogger().severe("Player not found: " + codec.player() + " (Message: " + codec.encode() + ")");
            return;
        }

        Bukkit.dispatchCommand(player, plugin.getConfig().getString("command-prefix") + " " + codec.warp());
    }
}
