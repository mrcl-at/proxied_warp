package at.mrcl.proxied_warp.core.bukkit;

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
            makeChecksThenSendPluginMessage(sender, getWarpInfo(false, player.getName(), args));
        } else {
            sender.sendMessage("§cThis command can only be executed by players.");
        }
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return List.of();
    }

    @Override
    protected Usage getUsage() {
        return new Usage("usage", plugin.getMessageUsage());
    }
}
