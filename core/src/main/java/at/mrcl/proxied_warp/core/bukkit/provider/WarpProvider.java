package at.mrcl.proxied_warp.core.bukkit.provider;

public interface WarpProvider {
    boolean warpExists(String warpName);
    default boolean hasPermission(String warpName, String playerName) {
        return true;
    }
}
