package at.mrcl.proxied_warp.core.bukkit;

import at.mrcl.proxied_warp.core.ProjectVariable;
import at.mrcl.proxied_warp.core.codec.Codec;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractProxiedWarpCommand implements CommandExecutor, TabCompleter {

    protected final BukkitPlugin plugin;
    private final String permissionPrefix;

    protected AbstractProxiedWarpCommand(BukkitPlugin plugin, String permissionPrefix) {
        this.plugin = plugin;
        this.permissionPrefix = permissionPrefix;
    }

    protected record Usage(String key, String fallback) {}
    protected abstract Usage getUsage();

    protected boolean isServerWhitelistedOrSendMessage(String server, CommandSender sender) {
        final var list = plugin.getConfig().getStringList("whitelisted-servers");
        final var is = list.isEmpty() || list.stream().anyMatch(server::equalsIgnoreCase);
        if (!is) this.plugin.sendMessage(sender, "server-not-whitelisted", plugin.getMessageServerNotWhitelisted());
        return is;
    }

    protected boolean isWarpWhitelistedOrSendMessage(String warp, CommandSender sender) {
        final var list = plugin.getConfig().getStringList("whitelisted-warps");
        final var is = list.isEmpty() || list.stream().anyMatch(warp::equalsIgnoreCase);
        if (!is) this.plugin.sendMessage(sender, "warp-not-whitelisted", plugin.getMessageWarpNotWhitelisted());
        return is;
    }

    protected boolean hasPermissionOrSendMessage(String warp, CommandSender sender) {
        if (!plugin.getConfig().getBoolean("need-permission")) return true;
        if (sender.hasPermission(this.permissionPrefix + "*")) return true;
        if (sender.hasPermission(this.permissionPrefix + warp)) return true;
        this.plugin.sendMessage(sender, "permission", plugin.getMessagePermission());
        return false;
    }

    protected void makeChecksThenSendPluginMessage(CommandSender sender, WarpInfo warpInfo) {
        if (warpInfo.isEmptyThenSendMessage(this, sender)) return;
        if (!hasPermissionOrSendMessage(warpInfo.warp(), sender)) return;
        if (warpInfo.checkPlayerOrEmptyThenSendMessage(this, sender)) return;
        if (!isServerWhitelistedOrSendMessage(warpInfo.server(), sender)) return;
        if (!isWarpWhitelistedOrSendMessage(warpInfo.warp(), sender)) return;

        warpInfo.getPlayer().sendPluginMessage(
                plugin.getPlugin(),
                ProjectVariable.CHANNEL,
                new Codec(warpInfo.player(), warpInfo.server(), warpInfo.warp()).write()
        );
    }

    protected List<String> getFirstArgSuggestions() {
        List<String> list = new ArrayList<>();
        list.addAll(getStringList("whitelisted-warps"));
        list.addAll(getStringList("whitelisted-servers"));

        var aliasesSection = this.plugin.getConfig().getConfigurationSection("aliases");
        if (aliasesSection != null) {
            list.addAll(aliasesSection.getKeys(false));
        }
        return list;
    }

    protected List<String> getStringList(String path) {
        return this.plugin.getConfig().getStringList(path);
    }

    protected List<String> filterSuggestions(String input, List<String> options) {
        String lower = input.toLowerCase();
        return options.stream()
                .filter(s -> s.toLowerCase().startsWith(lower))
                .toList();
    }
}
