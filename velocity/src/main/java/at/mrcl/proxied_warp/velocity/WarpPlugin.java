package at.mrcl.proxied_warp.velocity;

import at.mrcl.proxied_warp.core.ProjectVariable;
import at.mrcl.proxied_warp.core.codec.Codec;
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

@Plugin( id = "proxied_warp", name = "proxied_warp", version = "1.2.0-SNAPSHOT", authors = { "mrcl" })
public class WarpPlugin {

    private static final MinecraftChannelIdentifier CHANNEL_IDENTIFIER = MinecraftChannelIdentifier.from(ProjectVariable.CHANNEL);

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
        server.getChannelRegistrar().register(CHANNEL_IDENTIFIER);
        metricsFactory.make(this, ProjectVariable.BSTATS_ID_VELOCITY);
    }

    @Subscribe
    public void onPluginMessageEvent(PluginMessageEvent event) {
        if (!event.getIdentifier().equals(CHANNEL_IDENTIFIER)) return;
        event.setResult(PluginMessageEvent.ForwardResult.handled());

        try {
            final var codec = Codec.decode(event.getData());

            server.getPlayer(codec.player()).ifPresentOrElse(player -> {
                server.getServer(codec.server()).ifPresentOrElse(registeredServer -> {
                    if (player.getCurrentServer().isPresent() && player.getCurrentServer().get().getServerInfo().equals(registeredServer)) {
                        sendPluginMessage(registeredServer, codec.encode());
                    } else {
                        player.createConnectionRequest(registeredServer)
                                .connect()
                                .whenComplete((result, throwable) -> {
                                    if (throwable != null) {
                                        logger.error("Error while connecting to server: {}", throwable.getMessage());
                                        return;
                                    }
                                    sendPluginMessage(registeredServer, codec.encode());
                                });
                    }
                }, () -> logger.error("Server not found: {}", codec.server()));
            }, () -> logger.error("Player not found: {}", codec.player()));
        } catch (Exception exception) {
            logger.error("Error while processing message: {}", exception.getMessage());
        }
    }

    private void sendPluginMessage(RegisteredServer server, String message) {
        final var output = ByteStreams.newDataOutput();
        output.writeUTF(message);
        server.sendPluginMessage(CHANNEL_IDENTIFIER, output.toByteArray());
    }
}
