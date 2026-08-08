package at.mrcl.proxied_warp.core.bukkit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

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
        return List.of();
    }

    @Override
    protected Usage getUsage() {
        return new Usage("usage-other", plugin.getMessageUsageOther());
    }
}
