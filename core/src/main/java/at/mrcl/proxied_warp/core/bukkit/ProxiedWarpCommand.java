package at.mrcl.proxied_warp.core.bukkit;

import at.mrcl.proxied_warp.core.ProjectVariable;
import at.mrcl.proxied_warp.core.codec.Codec;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.Map;

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

        String server = null, warp = null;

        if (args.length == 1) {
            final var section = plugin.getConfig().getConfigurationSection("aliases");
            if (section != null) {
                for (final var key : section.getKeys(false)) {
                    if (key.equalsIgnoreCase(args[0])) {
                        warp = key;
                        server = section.getString(key);
                        break;
                    }
                }
            }

        } else if (args.length == 2) {
            server = args[0];
            warp = args[1];
        } else {
            plugin.sendMessage(player, "usage", plugin.getMessageUsage());
            return true;
        }

        if (server == null || warp == null) {
            plugin.sendMessage(player, "usage", plugin.getMessageUsage());
            return true;
        }

        if (!hasPermission(sender, warp)) {
            plugin.sendMessage(sender, "permission", plugin.getMessagePermission());
            return true;
        }

        if (!isServerWhitelisted(server)) {
            plugin.sendMessage(sender, "server-not-whitelisted", plugin.getMessageServerNotWhitelisted());
            return true;
        }

        if (!isWarpWhitelisted(warp)) {
            plugin.sendMessage(sender, "warp-not-whitelisted", plugin.getMessageWarpNotWhitelisted());
            return true;
        }

        player.sendPluginMessage(plugin.getPlugin(), ProjectVariable.CHANNEL, new Codec(sender.getName(), server, warp).write());
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
