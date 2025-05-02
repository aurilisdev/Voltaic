package voltaic.client.model.block.bakerytypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;

import voltaic.client.model.block.ModelStateRotation;
import voltaic.client.model.block.modelproperties.ModelPropertyConnections;
import voltaic.common.block.connect.EnumConnectType;
import voltaic.prefab.tile.types.IConnectTile;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
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
    public WirePartGeometry read(JsonDeserializationContext context, JsonObject json) throws JsonParseException {

        BlockModel none = context.deserialize(GsonHelper.getAsJsonObject(json, EnumConnectType.NONE.toString()), BlockModel.class);
        BlockModel wire = context.deserialize(GsonHelper.getAsJsonObject(json, EnumConnectType.WIRE.toString()), BlockModel.class);
        BlockModel inventory = context.deserialize(GsonHelper.getAsJsonObject(json, EnumConnectType.INVENTORY.toString()), BlockModel.class);
        return new WirePartGeometry(none, wire, inventory);
    }
    
    @Override
    public void onResourceManagerReload(ResourceManager pResourceManager) {

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
        public BakedModel bake(IModelConfiguration context, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
            boolean useBlockLight = context.useSmoothLighting();

            BakedModel none = this.none.bake(bakery, this.none, spriteGetter, modelState, modelLocation, useBlockLight);

            BakedModel[] wires = new BakedModel[6];
            BakedModel[] inventories = new BakedModel[6];

            for (Direction dir : Direction.values()) {

                ModelState transform = ModelStateRotation.ROTATIONS.get(dir);

                wires[dir.ordinal()] = this.wire.bake(bakery, this.wire, spriteGetter, transform, modelLocation, useBlockLight);
                inventories[dir.ordinal()] = this.inventory.bake(bakery, this.inventory, spriteGetter, transform, modelLocation, useBlockLight);

            }

            return new CableModel(context.useSmoothLighting(), context.isShadedInGui(), useBlockLight, spriteGetter.apply(this.none.getMaterial("particle")), none, wires, inventories);
        }
        
        @Override
        public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        	Set<Material> set = new HashSet<>();
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
        private final BakedModel none;
        private final BakedModel[] wires;
        private final BakedModel[] inventories;

        public CableModel(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, TextureAtlasSprite particle, BakedModel none, BakedModel[] wires, BakedModel[] inventories) {
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
        public ItemOverrides getOverrides() {
            return ItemOverrides.EMPTY;
        }

        @Override
        public @NotNull List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData) {
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
        public @NotNull IModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull IModelData modelData) {
            if (level.getBlockEntity(pos) instanceof IConnectTile tile) {
                return new ModelDataMap.Builder().withInitial(ModelPropertyConnections.INSTANCE, () -> tile.readConnections()).build();
            }
            return modelData;
        }

    }

}
