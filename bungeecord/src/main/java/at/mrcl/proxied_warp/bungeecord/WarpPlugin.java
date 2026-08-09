package at.mrcl.proxied_warp.bungeecord;

import at.mrcl.proxied_warp.core.ProjectVariable;
import at.mrcl.proxied_warp.core.codec.Codec;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import org.bstats.bungeecord.Metrics;

public class WarpPlugin extends Plugin implements Listener {

    @Override
    public void onEnable() {
        new Metrics(this, ProjectVariable.BSTATS_ID_BUNGEECORD);
        ProxyServer.getInstance().registerChannel(ProjectVariable.CHANNEL);
        ProxyServer.getInstance().registerChannel(ProjectVariable.CHANNEL_MESSAGE);
        ProxyServer.getInstance().getPluginManager().registerListener(this, this);
    }

    @EventHandler
    public void onPluginMessageEvent(PluginMessageEvent event) {
        if (!event.getTag().equals(ProjectVariable.CHANNEL)) return;
        final var codec = Codec.decode(event.getData());

        final var player = ProxyServer.getInstance().getPlayer(codec.player());
        if (player == null) {
            getLogger().warning("Player " + codec.player() + " not found!");
            return;
        }

        final var server = ProxyServer.getInstance().getServerInfo(codec.server());
        if (server == null) {
            getLogger().warning("Server " + codec.server() + " not found!");
            sendPluginMessage(player, ProjectVariable.CHANNEL_MESSAGE, player.getName() + " server-not-found");
            return;
        }

        if (!player.getServer().getInfo().getName().equals(server.getName())) {
            player.connect(server, (result, throwable) -> {
                if (result) {
                    sendPluginMessage(player, ProjectVariable.CHANNEL, codec.encode());
                } else {
                    getLogger().warning("Player " + player.getName() + " failed to connect to server " + server.getName());
                }
            });
        } else {
            sendPluginMessage(player, ProjectVariable.CHANNEL, codec.encode());
        }
    }

    private void sendPluginMessage(ProxiedPlayer player, String channel, String message) {
        final var output = ByteStreams.newDataOutput();
        output.writeUTF(message);
        if (player.getServer() != null) {
            player.getServer().sendData(channel, output.toByteArray());
        }
    }
}
