package voltaicapi.client.texture.atlas;

import voltaicapi.VoltaicAPI;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.ResourceLocation;

public class AtlasHolderElectrodynamicsCustom extends TextureAtlasHolder {

	public static AtlasHolderElectrodynamicsCustom INSTANCE;

	// Custom Textures
//	public static final ResourceLocation TEXTURE_QUARRYARM = create("quarryarm");
//	public static final ResourceLocation TEXTURE_QUARRYARM_DARK = create("quarrydark");
//	public static final ResourceLocation TEXTURE_MERCURY = create("mercury");
//	public static final ResourceLocation TEXTURE_GAS = create("gastexture");

	public AtlasHolderElectrodynamicsCustom(TextureManager textureManager) {
		super(textureManager, VoltaicAPI.rl("textures/" + VoltaicAPI.ID + "/" + ElectrodynamicsTextureAtlases.ELECTRODYNAMICS_CUSTOM_NAME + ".png"), ElectrodynamicsTextureAtlases.ELECTRODYNAMICS_CUSTOM);
	}

	@Override
	public TextureAtlasSprite getSprite(ResourceLocation location) {
		return super.getSprite(location);
	}

	public static TextureAtlasSprite get(ResourceLocation loc) {
		return INSTANCE.getSprite(loc);
	}

	@SuppressWarnings("unused")
	private static ResourceLocation create(String name) {
		return VoltaicAPI.rl("custom/" + name);
	}

}
