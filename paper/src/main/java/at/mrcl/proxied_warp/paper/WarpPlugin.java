package at.mrcl.proxied_warp.paper;

import at.mrcl.proxied_warp.core.ProjectVariable;
import at.mrcl.proxied_warp.core.bukkit.BukkitPlugin;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
        return "<red>Usage:</red> /proxiedwarp <server> <warp>";
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
    public void sendMessage(CommandSender sender, String key, String fallback) {
        var message = getConfig().getString("message." + key);
        if (message == null) message = fallback;
        sender.sendMessage(MiniMessage.miniMessage().deserialize(message));
    }
}
