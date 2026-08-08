package at.mrcl.proxied_warp.paper;

import at.mrcl.proxied_warp.core.ProjectVariable;
import at.mrcl.proxied_warp.core.bukkit.BukkitPlugin;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class WarpPlugin extends JavaPlugin implements BukkitPlugin {

    @Override
    public void onEnable() {
        enable();
        new Metrics(this, ProjectVariable.BSTATS_ID_BUKKIT);
    }

    @Override
    public JavaPlugin getPlugin() {
        return this;
    }

    @Override
    public String getMessageUsage() {
        return "<red>Usage:</red> /proxiedwarp <server/alias> [<warp>]";
    }

    @Override
    public String getMessageUsageOther() {
        return "<red>Usage:</red> /proxiedwarpother <player> <server/alias> [<warp>]";
    }

    @Override
    public String getMessagePermission() {
        return "<red>You do not have permission to do that!";
    }

    @Override
    public String getMessageServerNotWhitelisted() {
        return "<red>This server is not whitelisted!";
    }

    @Override
    public String getMessageWarpNotWhitelisted() {
        return "<red>This warp is not whitelisted!";
    }

    @Override
    public String getMessagePlayerNotOnline() {
        return "<red>Player is not online!";
    }

    @Override
    public String getMessageKeyNotFound() {
        return "<red>Key <gray>'<key>'</gray> not found!";
    }

    @Override
    public String getMessageServerNotFound() {
        return "<red>Server not found!";
    }

    @Override
    public void sendMessage(CommandSender sender, String key, String fallback) {
        sender.sendMessage(MiniMessage.miniMessage().deserialize(getMessage(key, fallback)));
    }

    @Override
    public void sendMessage(CommandSender sender, String key) {
        final var component = MiniMessage.miniMessage().deserialize(
                getMessage(key, getMessageKeyNotFound()),
                Placeholder.parsed("key", key)
        );
        sender.sendMessage(component);
    }
}
