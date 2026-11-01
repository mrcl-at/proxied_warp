package at.mrcl.proxied_warp.spigot;

import at.mrcl.proxied_warp.core.ProjectVariable;
import at.mrcl.proxied_warp.core.bukkit.BukkitPlugin;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotWarpPlugin extends JavaPlugin implements BukkitPlugin {

    @Override
    public void onEnable() {
        enable();
        new Metrics(this, ProjectVariable.B_STATS_ID_BUKKIT);
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
        return "&cUsage:&r /proxiedwarp <server/alias> [<warp>]";
    }

    @Override
    public String getMessageUsageOther() {
        return "&cUsage:&r /proxiedwarpother <player> <server/alias> [<warp>]";
    }

    @Override
    public String getMessageServerNotWhitelisted() {
        return "&cThis server is not whitelisted!";
    }

    @Override
    public String getMessageWarpNotWhitelisted() {
        return "&cThis warp is not whitelisted!";
    }

    @Override
    public String getMessagePlayerNotOnline() {
        return "&cPlayer is not online!";
    }

    @Override
    public String getMessageKeyNotFound() {
        return "&cKey &f'%s' &cnot found!";
    }

    @Override
    public String getMessageServerNotFound() {
        return "&cServer not found!";
    }

    @Override
    public String getMessageConnectionFailed() {
        return "&cConnection failed!";
    }

    @Override
    public void sendMessage(CommandSender sender, String key, String fallback) {
        sender.sendMessage(getMessage(key, fallback).replace("&", "§"));
    }

    @Override
    public void sendMessage(CommandSender sender, String key) {
        sender.sendMessage(getMessage(key, getMessageKeyNotFound().formatted(key)).replace("&", "§"));
    }
}
