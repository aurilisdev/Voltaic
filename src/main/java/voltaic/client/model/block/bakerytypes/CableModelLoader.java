package voltaic.client.model.block.bakerytypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;

import voltaic.client.model.block.ModelStateRotation;
import voltaic.client.model.block.modelproperties.ModelPropertyConnections;
import voltaic.common.block.connect.EnumConnectType;
import voltaic.prefab.tile.types.IConnectTile;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IResourceManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.geometry.IModelGeometry;

public class CableModelLoader implements IModelLoader<CableModelLoader.WirePartGeometry> {

    public static final String ID = "voltaiccableloader";

    public static final CableModelLoader INSTANCE = new CableModelLoader();

    @Override
    public WirePartGeometry read(JsonDeserializationContext owner, JsonObject json) throws JsonParseException {

        BlockModel none = owner.deserialize(JSONUtils.getAsJsonObject(json, EnumConnectType.NONE.toString()), BlockModel.class);
        BlockModel wire = owner.deserialize(JSONUtils.getAsJsonObject(json, EnumConnectType.WIRE.toString()), BlockModel.class);
        BlockModel inventory = owner.deserialize(JSONUtils.getAsJsonObject(json, EnumConnectType.INVENTORY.toString()), BlockModel.class);
        return new WirePartGeometry(none, wire, inventory);
    }
    
    @Override
    public void onResourceManagerReload(IResourceManager pResourceManager) {

    }

    public static class WirePartGeometry implements IModelGeometry<WirePartGeometry> {

        private final BlockModel none;
        private final BlockModel wire;
        private final BlockModel inventory;

        public WirePartGeometry(BlockModel none, BlockModel wire, BlockModel inventory) {
            this.none = none;
            this.wire = wire;
            this.inventory = inventory;

        }

        @Override
        public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
            boolean useBlockLight = owner.useSmoothLighting();

            IBakedModel none = this.none.bake(bakery, this.none, spriteGetter, modelTransform, modelLocation, useBlockLight);

            IBakedModel[] wires = new IBakedModel[6];
            IBakedModel[] inventories = new IBakedModel[6];

            for (Direction dir : Direction.values()) {

                IModelTransform transform = ModelStateRotation.ROTATIONS.get(dir);

                wires[dir.ordinal()] = this.wire.bake(bakery, this.wire, spriteGetter, transform, modelLocation, useBlockLight);
                inventories[dir.ordinal()] = this.inventory.bake(bakery, this.inventory, spriteGetter, transform, modelLocation, useBlockLight);

            }

            return new CableModel(owner.useSmoothLighting(), owner.isShadedInGui(), useBlockLight, spriteGetter.apply(this.none.getMaterial("particle")), none, wires, inventories);
        }
        
        @Override
        public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        	Set<RenderMaterial> set = new HashSet<>();
        	set.addAll(none.getMaterials(modelGetter, missingTextureErrors));
        	set.addAll(wire.getMaterials(modelGetter, missingTextureErrors));
        	set.addAll(inventory.getMaterials(modelGetter, missingTextureErrors));
        	return set;
        }

    }

    public static class CableModel implements IDynamicBakedModel {

        private static final List<BakedQuad> NO_QUADS = ImmutableList.of();

        private final boolean isAmbientOcclusion;
        private final boolean isGui3d;
        private final boolean isSideLit;
        private final TextureAtlasSprite particle;
        // render type for general model defined by this one
        private final IBakedModel none;
        private final IBakedModel[] wires;
        private final IBakedModel[] inventories;

        public CableModel(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, TextureAtlasSprite particle, IBakedModel none, IBakedModel[] wires, IBakedModel[] inventories) {
            this.isAmbientOcclusion = isAmbientOcclusion;
            this.isGui3d = isGui3d;
            this.isSideLit = isSideLit;
            this.particle = particle;
            this.none = none;
            this.wires = wires;
            this.inventories = inventories;
        }

        @Override
        public boolean useAmbientOcclusion() {
            return this.isAmbientOcclusion;
        }

        @Override
        public boolean isGui3d() {
            return this.isGui3d;
        }

        @Override
        public boolean usesBlockLight() {
            return isSideLit;
        }

        @Override
        public boolean isCustomRenderer() {
            return false;
        }

        @Override
        public TextureAtlasSprite getParticleIcon() {
            return this.particle;
        }

        @Override
        public ItemOverrideList getOverrides() {
        	return ItemOverrideList.EMPTY;
        }

        @Override
        public @Nonnull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
            @Nullable Supplier<EnumConnectType[]> m = extraData.getData(ModelPropertyConnections.INSTANCE);
            if (m == null) {
                return NO_QUADS;
            }
            EnumConnectType[] data = m.get();
            if (data == null) {
                return NO_QUADS;
            }

            boolean none = false;

            List<BakedQuad> quads = new ArrayList<>();

            for (int i = 0; i < data.length; i++) {

                switch (data[i]) {
                    case NONE:
                        none = true;
                        break;
                    case WIRE:
                        quads.addAll(this.wires[i].getQuads(state, side, rand, extraData));
                        break;
                    case INVENTORY:
                        quads.addAll(this.inventories[i].getQuads(state, side, rand, extraData));
                    default:
                        none = true;
                        break;
                }
            }

            if (none) {
                quads.addAll(this.none.getQuads(state, side, rand, extraData));
            }

            return quads;
        }

        @Override
        public @Nonnull IModelData getModelData(@Nonnull IBlockDisplayReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
        	TileEntity blockentity = world.getBlockEntity(pos);
            if (blockentity instanceof IConnectTile) {
                return new ModelDataMap.Builder().withInitial(ModelPropertyConnections.INSTANCE, () -> ((IConnectTile) blockentity).readConnections()).build();
            }
            return tileData;
        }

    }

}
