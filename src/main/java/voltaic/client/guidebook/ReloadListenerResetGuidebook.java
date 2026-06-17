package voltaic.client.guidebook;

import org.jetbrains.annotations.NotNull;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import voltaic.Voltaic;

/**
 * Avert your eyes kids
 * 
 * @author skip999
 *
 */
public class ReloadListenerResetGuidebook extends SimplePreparableReloadListener<Integer> {

    @Override
    protected @NotNull Integer prepare(@NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
	return 0;
    }

    @Override
    protected void apply(@NotNull Integer number, @NotNull ResourceManager resourceManager,
	    @NotNull ProfilerFiller profiler) {
	Voltaic.LOGGER.info("Resetting from client");
	ScreenGuidebook.setInitNotHappened();
    }

    @Override
    public @NotNull String getName() {
	return "Electrodynamics Guidebook Listener";
    }

}
