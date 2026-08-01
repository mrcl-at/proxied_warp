package at.mrcl.proxied_warp.core.bukkit;

import at.mrcl.proxied_warp.core.ProjectVariable;
import at.mrcl.proxied_warp.core.codec.Codec;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public class ProxiedWarpCommand implements CommandExecutor {

    private final BukkitPlugin plugin;

    public ProxiedWarpCommand(BukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cThis command can only be executed by players.");
            return true;
        }

        if (args.length != 2) {
            plugin.sendMessage(player, "usage", plugin.getMessageUsage());
            return true;
        }

        final var warp = args[1];
        if (!hasPermission(sender, warp)) {
            plugin.sendMessage(player, "permission", plugin.getMessagePermission());
            return true;
        }

        final var server = args[0];
        if (!isServerWhitelisted(server)) {
            plugin.sendMessage(player, "server-not-whitelisted", plugin.getMessageServerNotWhitelisted());
            return true;
        }

        if (!isWarpWhitelisted(warp)) {
            plugin.sendMessage(player, "warp-not-whitelisted", plugin.getMessageWarpNotWhitelisted());
            return true;
        }

        player.sendPluginMessage(plugin.getPlugin(), ProjectVariable.CHANNEL, new Codec(player.getName(), args[0], warp).write());
        return true;
    }

    private boolean isServerWhitelisted(String server) {
        final var list = plugin.getConfig().getStringList("whitelisted-servers");
        return list.isEmpty() || list.stream().anyMatch(server::equalsIgnoreCase);
    }

    private boolean isWarpWhitelisted(String warp) {
        final var list = plugin.getConfig().getStringList("whitelisted-warps");
        return list.isEmpty() || list.stream().anyMatch(warp::equalsIgnoreCase);
    }

    private boolean hasPermission(CommandSender sender, String warp) {
        if (!plugin.getConfig().getBoolean("need-permission")) return true;
        if (sender.hasPermission("proxiedwarp.warp.*")) return true;
        return sender.hasPermission("proxiedwarp.warp." + warp);
    }
}
