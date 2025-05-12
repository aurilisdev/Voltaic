package voltaic.compatibility.jei;

import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.util.ResourceLocation;
import voltaic.Voltaic;
import voltaic.client.guidebook.ScreenGuidebook;
import voltaic.client.screen.ScreenDO2OProcessor;
import voltaic.client.screen.ScreenO2OProcessor;
import voltaic.client.screen.ScreenO2OProcessorDouble;
import voltaic.client.screen.ScreenO2OProcessorTriple;
import voltaic.compatibility.jei.screenhandlers.ScreenHandlerGuidebook;
import voltaic.compatibility.jei.screenhandlers.ScreenHandlerMaterialScreen;
import voltaic.compatibility.jei.utils.RecipeType;
import voltaic.prefab.screen.types.GenericMaterialScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;

@JeiPlugin
public class VoltaicJEIPlugin implements IModPlugin {

    public static final ResourceLocation ID = Voltaic.rl( "jei");

    private static final List<ResourceLocation> O2O_CLICK_AREAS = new ArrayList<>();
    private static final List<ResourceLocation> DO2O_CLICK_AREAS = new ArrayList<>();

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registry) {
        registry.addGenericGuiContainerHandler(ScreenGuidebook.class, new ScreenHandlerGuidebook());
        registry.addGenericGuiContainerHandler(GenericMaterialScreen.class, new ScreenHandlerMaterialScreen());
        registry.addRecipeClickArea(ScreenO2OProcessor.class, 48, 35, 22, 15, O2O_CLICK_AREAS.toArray(new ResourceLocation[O2O_CLICK_AREAS.size()]));
        registry.addRecipeClickArea(ScreenO2OProcessorDouble.class, 48, 25, 22, 35, O2O_CLICK_AREAS.toArray(new ResourceLocation[O2O_CLICK_AREAS.size()]));
        registry.addRecipeClickArea(ScreenO2OProcessorTriple.class, 48, 25, 22, 55, O2O_CLICK_AREAS.toArray(new ResourceLocation[O2O_CLICK_AREAS.size()]));
        registry.addRecipeClickArea(ScreenDO2OProcessor.class, 48, 35, 22, 15, DO2O_CLICK_AREAS.toArray(new ResourceLocation[DO2O_CLICK_AREAS.size()]));
    }

    public static void addO2OCategory(RecipeType<?> category) {
        O2O_CLICK_AREAS.add(category.getUid());
    }

    public static void addDO2OCategory(RecipeType<?> category) {
        DO2O_CLICK_AREAS.add(category.getUid());
    }


}
