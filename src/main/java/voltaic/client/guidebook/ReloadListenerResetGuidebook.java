package voltaic.client.guidebook;

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
    protected Integer prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
	return 0;
    }

    @Override
    protected void apply(Integer number, ResourceManager resourceManager, ProfilerFiller profiler) {
	Voltaic.LOGGER.info("Resetting from client");
	ScreenGuidebook.setInitNotHappened();
    }

    @Override
    public String getName() {
	return "Electrodynamics Guidebook Listener";
    }

}
