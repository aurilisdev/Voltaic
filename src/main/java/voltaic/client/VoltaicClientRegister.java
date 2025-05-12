package voltaic.client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import voltaic.Voltaic;
import voltaic.client.guidebook.ScreenGuidebook;
import voltaic.client.model.block.bakerytypes.CableModelLoader;
import voltaic.client.particle.fluiddrop.ParticleFluidDrop;
import voltaic.client.particle.lavawithphysics.ParticleLavaWithPhysics;
import voltaic.client.particle.plasmaball.ParticlePlasmaBall;

import voltaic.client.screen.ScreenDO2OProcessor;
import voltaic.client.screen.ScreenO2OProcessor;
import voltaic.client.screen.ScreenO2OProcessorDouble;
import voltaic.client.screen.ScreenO2OProcessorTriple;
import voltaic.registers.VoltaicBlocks;
import voltaic.registers.VoltaicMenuTypes;
import voltaic.registers.VoltaicParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
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

    private static final HashMap<ResourceLocation, TextureAtlasSprite> CACHED_TEXTUREATLASSPRITES = new HashMap<>();
    // for registration purposes only!
    private static final List<ResourceLocation> CUSTOM_TEXTURES = Arrays.asList(VoltaicClientRegister.TEXTURE_WHITE, VoltaicClientRegister.TEXTURE_MERCURY, VoltaicClientRegister.TEXTURE_GAS, VoltaicClientRegister.TEXTURE_MULTISUBNODE);

    public static void setup() {
    	ScreenManager.register(VoltaicMenuTypes.CONTAINER_GUIDEBOOK.get(), ScreenGuidebook::new);
        ScreenManager.register(VoltaicMenuTypes.CONTAINER_O2OPROCESSOR.get(), ScreenO2OProcessor::new);
        ScreenManager.register(VoltaicMenuTypes.CONTAINER_O2OPROCESSORDOUBLE.get(), ScreenO2OProcessorDouble::new);
        ScreenManager.register(VoltaicMenuTypes.CONTAINER_O2OPROCESSORTRIPLE.get(), ScreenO2OProcessorTriple::new);
        ScreenManager.register(VoltaicMenuTypes.CONTAINER_DO2OPROCESSOR.get(), ScreenDO2OProcessor::new);
        
        RenderTypeLookup.setRenderLayer(VoltaicBlocks.BLOCK_MULTISUBNODE.get(), RenderType.cutout());
    }
    
    @SubscribeEvent
	public static void addCustomTextureAtlases(TextureStitchEvent.Pre event) {
		if (event.getMap().location().equals(AtlasTexture.LOCATION_BLOCKS)) {
			CUSTOM_TEXTURES.forEach(event::addSprite);
		}
	}

    @SubscribeEvent
	public static void cacheCustomTextureAtlases(TextureStitchEvent.Post event) {
		if (event.getMap().location().equals(AtlasTexture.LOCATION_BLOCKS)) {
            CACHED_TEXTUREATLASSPRITES.clear();
            for (ResourceLocation loc : CUSTOM_TEXTURES) {
                VoltaicClientRegister.CACHED_TEXTUREATLASSPRITES.put(loc, event.getMap().getSprite(loc));
            }
        }
    }

    @SubscribeEvent
    public static void registerParticles(ParticleFactoryRegisterEvent event) {
    	ParticleManager engine = Minecraft.getInstance().particleEngine;
        engine.register(VoltaicParticles.PARTICLE_PLASMA_BALL.get(), ParticlePlasmaBall.Factory::new);
        engine.register(VoltaicParticles.PARTICLE_LAVAWITHPHYSICS.get(), ParticleLavaWithPhysics.Factory::new);
        engine.register(VoltaicParticles.PARTICLE_FLUIDDROP.get(), ParticleFluidDrop.Factory::new);
    }
    
    @SubscribeEvent
    public static void registerGeometryLoaders(final ModelRegistryEvent event) {
        ModelLoaderRegistry.registerLoader(Voltaic.rl(CableModelLoader.ID), CableModelLoader.INSTANCE);
    }

    public static TextureAtlasSprite getSprite(ResourceLocation sprite) {
        return CACHED_TEXTUREATLASSPRITES.getOrDefault(sprite, CACHED_TEXTUREATLASSPRITES.get(TEXTURE_WHITE));
    }

    public static final TextureAtlasSprite whiteSprite() {
        return CACHED_TEXTUREATLASSPRITES.get(TEXTURE_WHITE);
    }

}
