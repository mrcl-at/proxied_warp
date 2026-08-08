package at.mrcl.proxied_warp.core.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ProxiedWarpOtherCommand extends AbstractProxiedWarpCommand {

    public ProxiedWarpOtherCommand(BukkitPlugin plugin) {
        super(plugin, "proxiedwarp.warp.other.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String playerName = null;
        if (args.length > 0) {
            playerName = args[0];
        }

        makeChecksThenSendPluginMessage(sender, getWarpInfo(true, playerName, args));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return filterSuggestions(args[0], Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
        } else if (args.length == 2) {
            return filterSuggestions(args[1], getFirstArgSuggestions());
        } else if (args.length == 3) {
            return filterSuggestions(args[2], getStringList("whitelisted-warps"));
        }
        return List.of();
    }

    @Override
    protected Usage getUsage() {
        return new Usage("usage-other", plugin.getMessageUsageOther());
    }
}
