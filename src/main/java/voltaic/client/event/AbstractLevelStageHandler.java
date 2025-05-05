package voltaic.client.event;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.math.vector.Matrix4f;

/**
 * A note about using this event: It is IMPERATIVE that the event be as efficient as possible. The more work the event must do in a single tick, the more tiny inefficient solutions start to add up. This really doens't play out much in the marker lines renderer for example, but it definitely has an impact in the quarry renderer
 * 
 * @author skip999
 *
 */
public abstract class AbstractLevelStageHandler {

	public abstract void render(WorldRenderer context, MatrixStack mat, float partialTicks, Matrix4f projectionMatrix, long finishTimeNano);

	public void clear() {

	}

}
