package voltaic.client.texture.atlas;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.ResourceLocation;
import voltaic.Voltaic;

public class AtlasHolderVoltaicCustom extends TextureAtlasHolder {

    public static AtlasHolderVoltaicCustom INSTANCE;

    // Custom Textures
//	public static final ResourceLocation TEXTURE_QUARRYARM = create("quarryarm");
//	public static final ResourceLocation TEXTURE_QUARRYARM_DARK = create("quarrydark");
//	public static final ResourceLocation TEXTURE_MERCURY = create("mercury");
//	public static final ResourceLocation TEXTURE_GAS = create("gastexture");

    public AtlasHolderVoltaicCustom(TextureManager textureManager) {
	super(textureManager,
		Voltaic.rl("textures/" + Voltaic.ID + "/" + VoltaicTextureAtlases.ELECTRODYNAMICS_CUSTOM_NAME + ".png"),
		VoltaicTextureAtlases.ELECTRODYNAMICS_CUSTOM);
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
	return Voltaic.rl("custom/" + name);
    }

}
