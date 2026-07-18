package at.mrcl.proxied_warp.paper;

import com.google.common.io.ByteStreams;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.checkerframework.checker.nullness.qual.NonNull;

public class WarpPlugin extends JavaPlugin implements PluginMessageListener, CommandExecutor {

    private static final String CHANNEL = "proxied_warp:proxied_warp";
    private static final String MESSAGE_PERMISSION = "<red>You do not have permission to do that!";
    private static final String MESSAGE_USAGE = "<red>Usage:</red> /proxiedwarp <server> <warp>";

    @Override
    public void onEnable() {
        getServer().getMessenger().registerOutgoingPluginChannel(this, CHANNEL);
        getServer().getMessenger().registerIncomingPluginChannel(this, CHANNEL, this);

        getCommand("proxiedwarp").setExecutor(this);

        loadConfig();
    }

    private void loadConfig() {
        getConfig().addDefault("command-prefix", "warp");
        getConfig().addDefault("need-permission", false);
        getConfig().addDefault("message.usage", MESSAGE_USAGE);
        getConfig().addDefault("message.permission", MESSAGE_PERMISSION);
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    @Override
    public void onPluginMessageReceived(@NonNull String channel, @NonNull Player player, byte @NonNull [] data) {
        final var message = ByteStreams.newDataInput(data).readUTF();
        final var args = message.split(" ");
        // Message: <player> <server> <warp>
        if (args.length != 3) {
            getLogger().severe("Invalid message format: " + message);
            return;
        }

        final var target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            getLogger().severe("Player not found: " + args[0] + " (Message: " + message + ")");
            return;
        }

        Bukkit.dispatchCommand(player, getConfig().getString("command-prefix") + " " + args[2]);
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cThis command can only be executed by players.");
            return true;
        }

        if (args.length != 2) {
            player.sendMessage(getMessageComponent("usage", MESSAGE_USAGE));
            return true;
        }

        if (getConfig().getBoolean("need-permission") && !(player.hasPermission("proxiedwarp.warp.*") || player.hasPermission("proxiedwarp.warp." + args[1]))) {
            player.sendMessage(getMessageComponent("permission", MESSAGE_PERMISSION));
            return true;
        }

        final var stream = ByteStreams.newDataOutput();
        stream.writeUTF(player.getName() + " " + String.join(" ", args));

        player.sendPluginMessage(this, CHANNEL, stream.toByteArray());
        return true;
    }

    private Component getMessageComponent(String key, String fallback) {
        var message = getConfig().getString("message." + key);
        if (message == null) message = fallback;
        return MiniMessage.miniMessage().deserialize(message);
    }
}
