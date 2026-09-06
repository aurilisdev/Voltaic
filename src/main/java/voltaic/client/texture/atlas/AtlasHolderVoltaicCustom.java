package voltaic.client.texture.atlas;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.ResourceLocation;
import voltaic.Voltaic;

public class AtlasHolderVoltaicCustom extends TextureAtlasHolder {

    private static @Nullable AtlasHolderVoltaicCustom instance;

    public static AtlasHolderVoltaicCustom initialize(TextureManager textureManager) {
	if (instance != null)
	    throw new IllegalStateException("Atlas holder has already been initialized");
	return instance = new AtlasHolderVoltaicCustom(textureManager);
    }

    public static AtlasHolderVoltaicCustom getInstance() {
	AtlasHolderVoltaicCustom instance = AtlasHolderVoltaicCustom.instance;
	if (instance == null)
	    throw new IllegalStateException("Atlas holder has not been initialized");
	return instance;
    }
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
	return getInstance().getSprite(loc);
    }

    @SuppressWarnings("unused")
    private static ResourceLocation create(String name) {
	return Voltaic.rl("custom/" + name);
    }

}
