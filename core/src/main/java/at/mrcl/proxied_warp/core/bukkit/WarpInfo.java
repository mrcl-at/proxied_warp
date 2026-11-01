package at.mrcl.proxied_warp.core.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public record WarpInfo(String warp, String server, String player) {

    public static WarpInfo getInfo(BukkitPlugin plugin, boolean other, String player, String[] args) {
        if (args.length == (other ? 2 : 1)) {
            final var section = plugin.getConfig().getConfigurationSection("aliases");
            if (section != null) {
                for (final var key : section.getKeys(false)) {
                    if (key.equalsIgnoreCase(args[args.length-1])) {
                        return new WarpInfo(key, section.getString(key), player);
                    }
                }
            }

        } else if (args.length == (other ? 3 : 2)) {
            return new WarpInfo(args[args.length-1], args[args.length-2], player);
        }
        return new WarpInfo(null, null, player);
    }

    public boolean isEmptyThenSendMessage(AbstractProxiedWarpCommand command, CommandSender sender) {
        final var is = warp == null || server == null || player == null;
        if (is) command.plugin.sendMessage(sender, command.getUsage().key(), command.getUsage().fallback());
        return is;
    }

    public boolean checkPlayerOrEmptyThenSendMessage(AbstractProxiedWarpCommand command, CommandSender sender) {
        final var is = player == null;
        if (is) command.plugin.sendMessage(sender, "player-not-online", command.plugin.getMessagePlayerNotOnline());
        return is;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(player);
    }
}