package at.mrcl.proxied_warp.core.bukkit;

import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class BukkitMessagePluginMessageListener implements PluginMessageListener {

    private final BukkitPlugin plugin;

    public BukkitMessagePluginMessageListener(BukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player p, byte[] data) {
        final var message = ByteStreams.newDataInput(data)
                .readUTF()
                .split(" ");

        CommandSender commandSender = null;

        if (message[0].equalsIgnoreCase("#")) {
            commandSender = Bukkit.getConsoleSender();
        } else {
            commandSender = Bukkit.getPlayer(message[0]);
        }

        if (commandSender == null) {
            this.plugin.getPlugin().getLogger().warning("Player not found: " + message[0]);
            return;
        }

        this.plugin.sendMessage(commandSender, message[1]);
    }
}
