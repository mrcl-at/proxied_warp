package at.mrcl.proxied_warp.core.bukkit.command;

import at.mrcl.proxied_warp.core.bukkit.BukkitPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class ProxiedWarpCommand extends AbstractProxiedWarpCommand {

    public ProxiedWarpCommand(BukkitPlugin plugin) {
        super(plugin, "proxiedwarp.warp.");
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {
        if (sender instanceof Player player) {
            makeChecksThenSendPluginMessage(sender, WarpInfo.getInfo(plugin, false, player.getName(), args));
        } else {
            sender.sendMessage("§cThis command can only be executed by players.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return filterSuggestions(args[0], getFirstArgSuggestions());
        } else if (args.length == 2) {
            return filterSuggestions(args[1], getStringList("whitelisted-warps"));
        }
        return List.of();
    }

    @Override
    protected Usage getUsage() {
        return new Usage("usage", plugin.getMessageUsage());
    }
}
