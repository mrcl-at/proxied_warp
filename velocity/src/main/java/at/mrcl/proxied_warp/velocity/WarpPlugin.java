package at.mrcl.proxied_warp.velocity;

import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.bstats.velocity.Metrics;
import org.slf4j.Logger;

@Plugin( id = "proxied_warp", name = "proxied_warp", version = "1.0", authors = { "mrcl" })
public class WarpPlugin {

    private static final MinecraftChannelIdentifier CHANNEL = MinecraftChannelIdentifier.from("proxied_warp:proxied_warp");
    private static final int BSTATS_ID = 32758;

    private final ProxyServer server;
    private final Logger logger;
    private final Metrics.Factory metricsFactory;

    @Inject
    public WarpPlugin(ProxyServer server, Logger logger, Metrics.Factory metricsFactory) {
        this.server = server;
        this.logger = logger;
        this.metricsFactory = metricsFactory;
    }

    @Subscribe
    public void onProxyInitializeEvent(ProxyInitializeEvent event) {
        server.getChannelRegistrar().register(CHANNEL);
        metricsFactory.make(this, BSTATS_ID);
    }

    @Subscribe
    public void onPluginMessageEvent(PluginMessageEvent event) {
        if (!event.getIdentifier().equals(CHANNEL)) return;
        event.setResult(PluginMessageEvent.ForwardResult.handled());

        try {
            final var message = ByteStreams.newDataInput(event.getData()).readUTF();
            final var args = message.split(" ");
            // Message: <player> <server> <warp>
            if (args.length != 3) {
                logger.error("Invalid message format: {}", message);
                return;
            }

            server.getPlayer(args[0]).ifPresentOrElse(player -> {
                server.getServer(args[1]).ifPresentOrElse(registeredServer -> {
                    if (player.getCurrentServer().isPresent() && player.getCurrentServer().get().getServerInfo().equals(registeredServer)) {
                        sendPluginMessage(registeredServer, message);
                    } else {
                        player.createConnectionRequest(registeredServer)
                                .connect()
                                .whenComplete((result, throwable) -> {
                                    if (throwable != null) {
                                        logger.error("Error while connecting to server: {}", throwable.getMessage());
                                        return;
                                    }
                                    sendPluginMessage(registeredServer, message);
                                });
                    }
                }, () -> logger.error("Server not found: {}", args[1]));
            }, () -> logger.error("Player not found: {}", args[0]));
        } catch (Exception exception) {
            logger.error("Error while processing message: {}", exception.getMessage());
        }
    }

    private void sendPluginMessage(RegisteredServer server, String message) {
        final var output = ByteStreams.newDataOutput();
        output.writeUTF(message);
        server.sendPluginMessage(CHANNEL, output.toByteArray());
    }
}
