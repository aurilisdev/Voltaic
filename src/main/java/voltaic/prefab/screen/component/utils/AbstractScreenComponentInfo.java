package voltaic.prefab.screen.component.utils;

import java.util.Collections;
import java.util.List;

import voltaic.api.screen.ITexture;
import voltaic.api.screen.component.TextPropertySupplier;
import voltaic.prefab.screen.component.ScreenComponentGeneric;
import net.minecraft.util.IReorderingProcessor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractScreenComponentInfo extends ScreenComponentGeneric {
	public static final int SIZE = 26;
	protected TextPropertySupplier infoHandler;

	public static final TextPropertySupplier EMPTY = Collections::emptyList;

	public AbstractScreenComponentInfo(ITexture texture, TextPropertySupplier infoHandler, int x, int y) {
		super(texture, x, y);
		this.infoHandler = infoHandler;
		onTooltip((poseStack, component, xAxis, yAxis) -> {
			
			gui.displayTooltips(poseStack, getInfo(infoHandler.getInfo()), xAxis, yAxis);
		});
	}

	protected abstract List<? extends IReorderingProcessor> getInfo(List<? extends IReorderingProcessor> list);

}