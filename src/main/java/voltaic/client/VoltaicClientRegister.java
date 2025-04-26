package voltaic.client;

import java.util.HashMap;
import java.util.List;

import voltaic.Voltaic;
import voltaic.client.guidebook.ScreenGuidebook;
import voltaic.client.model.block.bakerytypes.CableModelLoader;
import voltaic.client.particle.fluiddrop.ParticleFluidDrop;
import voltaic.client.particle.lavawithphysics.ParticleLavaWithPhysics;
import voltaic.client.particle.plasmaball.ParticlePlasmaBall;
import voltaic.client.guidebook.ReloadListenerResetGuidebook;

import voltaic.client.screen.ScreenDO2OProcessor;
import voltaic.client.screen.ScreenO2OProcessor;
import voltaic.client.screen.ScreenO2OProcessorDouble;
import voltaic.client.screen.ScreenO2OProcessorTriple;
import voltaic.client.texture.atlas.AtlasHolderVoltaicCustom;
import voltaic.registers.VoltaicMenuTypes;
import voltaic.registers.VoltaicParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.ModelEvent.RegisterAdditional;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = Voltaic.ID, bus = EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class VoltaicClientRegister {


    public static final ResourceLocation ON = Voltaic.vanillarl("on");

    // Custom Textures
    public static final ResourceLocation TEXTURE_WHITE = Voltaic.forgerl("white");
    public static final ResourceLocation TEXTURE_MERCURY = Voltaic.rl("block/custom/mercury");
    public static final ResourceLocation TEXTURE_GAS = Voltaic.rl("block/custom/gastexture");
    public static final ResourceLocation TEXTURE_MULTISUBNODE = Voltaic.rl("block/multisubnode");

    private static final String MULTIBLOCK_API_MODEL_FOLDER = "multiblockmodels";

    private static final HashMap<ResourceLocation, TextureAtlasSprite> CACHED_TEXTUREATLASSPRITES = new HashMap<>();
    // for registration purposes only!
    private static final List<ResourceLocation> CUSTOM_TEXTURES = List.of(VoltaicClientRegister.TEXTURE_WHITE, VoltaicClientRegister.TEXTURE_MERCURY, VoltaicClientRegister.TEXTURE_GAS, VoltaicClientRegister.TEXTURE_MULTISUBNODE);

    public static void setup() {
    	MenuScreens.register(VoltaicMenuTypes.CONTAINER_GUIDEBOOK.get(), ScreenGuidebook::new);
        MenuScreens.register(VoltaicMenuTypes.CONTAINER_O2OPROCESSOR.get(), ScreenO2OProcessor::new);
        MenuScreens.register(VoltaicMenuTypes.CONTAINER_O2OPROCESSORDOUBLE.get(), ScreenO2OProcessorDouble::new);
        MenuScreens.register(VoltaicMenuTypes.CONTAINER_O2OPROCESSORTRIPLE.get(), ScreenO2OProcessorTriple::new);
        MenuScreens.register(VoltaicMenuTypes.CONTAINER_DO2OPROCESSOR.get(), ScreenDO2OProcessor::new);
    }

    @SubscribeEvent
    public static void onModelEvent(RegisterAdditional event) {

        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        FileToIdConverter converter = FileToIdConverter.json("models/" + MULTIBLOCK_API_MODEL_FOLDER);
        converter.listMatchingResources(manager).forEach((location, resource) -> event.register(converter.fileToId(location).withPrefix(MULTIBLOCK_API_MODEL_FOLDER + "/")));
    }

    @SubscribeEvent
    public static void cacheCustomTextureAtlases(TextureStitchEvent.Post event) {
        if (event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS)) {
            CACHED_TEXTUREATLASSPRITES.clear();
            for (ResourceLocation loc : CUSTOM_TEXTURES) {
                VoltaicClientRegister.CACHED_TEXTUREATLASSPRITES.put(loc, event.getAtlas().getSprite(loc));
            }
        }
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(VoltaicParticles.PARTICLE_PLASMA_BALL.get(), ParticlePlasmaBall.Factory::new);
        event.registerSpriteSet(VoltaicParticles.PARTICLE_LAVAWITHPHYSICS.get(), ParticleLavaWithPhysics.Factory::new);
        event.registerSpriteSet(VoltaicParticles.PARTICLE_FLUIDDROP.get(), ParticleFluidDrop.Factory::new);
    }

    @SubscribeEvent
    public static void registerClientReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(AtlasHolderVoltaicCustom.INSTANCE = new AtlasHolderVoltaicCustom(Minecraft.getInstance().getTextureManager()));
        event.registerReloadListener(new ReloadListenerResetGuidebook());
    }

    @SubscribeEvent
    public static void registerGeometryLoaders(final ModelEvent.RegisterGeometryLoaders event) {
        event.register(CableModelLoader.ID, CableModelLoader.INSTANCE);
    }

    public static TextureAtlasSprite getSprite(ResourceLocation sprite) {
        return CACHED_TEXTUREATLASSPRITES.getOrDefault(sprite, CACHED_TEXTUREATLASSPRITES.get(TEXTURE_WHITE));
    }

    public static final TextureAtlasSprite whiteSprite() {
        return CACHED_TEXTUREATLASSPRITES.get(TEXTURE_WHITE);
    }

}
