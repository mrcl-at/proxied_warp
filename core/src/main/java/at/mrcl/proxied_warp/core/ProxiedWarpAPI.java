package at.mrcl.proxied_warp.core;

import at.mrcl.proxied_warp.core.bukkit.provider.WarpProvider;

import java.util.Optional;

public class ProxiedWarpAPI {

    private static WarpProvider provider;

    public static void setProvider(WarpProvider provider) {
        if (provider == null) throw new NullPointerException("Provider cannot be null");
        if (ProxiedWarpAPI.provider != null) throw new IllegalStateException("Provider already set");
        ProxiedWarpAPI.provider = provider;
    }

    public static Optional<WarpProvider> getProvider() {
        return Optional.ofNullable(provider);
    }
}
