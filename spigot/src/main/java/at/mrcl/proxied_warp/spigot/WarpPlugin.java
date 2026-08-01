package at.mrcl.proxied_warp.spigot;

import at.mrcl.proxied_warp.core.ProjectVariable;
import at.mrcl.proxied_warp.core.bukkit.BukkitPlugin;
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
    public String getMessagePermission() {
        return "&cYou do not have permission to do that!";
    }

    @Override
    public String getMessageUsage() {
        return "&cUsage:&r /proxiedwarp <server> <warp>";
    }

    @Override
    public String getMessageServerNotWhitelisted() {
        return "&cThis server is not whitelisted!";
    }

    @Override
    public void sendMessage(CommandSender sender, String key, String fallback) {
        var message = getConfig().getString("message." + key);
        if (message == null) message = fallback;
        sender.sendMessage(message.replace("&", "§"));
    }
}
