package at.mrcl.proxied_warp.bungeecord;

import at.mrcl.proxied_warp.core.ProjectVariable;
import net.md_5.bungee.api.plugin.Plugin;
import org.bstats.bungeecord.Metrics;

public class WarpPlugin extends Plugin {

    @Override
    public void onEnable() {
        new Metrics(this, ProjectVariable.BSTATS_ID_BUNGEECORD);
    }
}
