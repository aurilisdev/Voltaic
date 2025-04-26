package voltaic.datagen.utils.client;

import javax.annotation.Nullable;

import voltaic.Voltaic;
import voltaic.common.block.states.VoltaicBlockStates;
import voltaic.datagen.utils.client.model.SlaveNodeModelBuilder;
import voltaic.datagen.utils.client.model.WireModelBuilder;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.ModelFile.ExistingModelFile;
import net.neoforged.neoforge.client.model.generators.loaders.ObjModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import voltaic.registers.VoltaicBlocks;

public abstract class BaseBlockstateProvider extends BlockStateProvider {

    public final String modID;

    public BaseBlockstateProvider(PackOutput output, ExistingFileHelper exFileHelper, String modID) {
        super(output, modID, exFileHelper);
        this.modID = modID;
    }

    public ItemModelBuilder simpleBlock(DeferredHolder<Block, ? extends Block> block, ModelFile file, boolean registerItem) {
        return simpleBlock(block.get(), file, registerItem);
    }

    public ItemModelBuilder simpleBlock(Block block, ModelFile file, boolean registerItem) {
        simpleBlock(block, file);
        if (registerItem) {
            return blockItem(block, file);
        }
        return null;
    }

    public ItemModelBuilder simpleBlock(DeferredHolder<Block, ? extends Block> block, ResourceLocation texture, boolean registerItem) {
        return simpleBlock(block.get(), texture, registerItem);
    }

    public ItemModelBuilder simpleBlock(Block block, ResourceLocation texture, boolean registerItem) {
        return simpleBlock(block, models().cubeAll(name(block), texture), registerItem);
    }

    public ItemModelBuilder glassBlock(DeferredHolder<Block, ? extends Block> block, ResourceLocation texture, boolean registerItem) {
        return glassBlock(block.get(), texture, registerItem);
    }

    public ItemModelBuilder glassBlock(Block block, ResourceLocation texture, boolean registerItem) {
        return simpleBlockCustomRenderType(block, texture, Voltaic.vanillarl("cutout"), registerItem);
    }

    public ItemModelBuilder simpleBlockCustomRenderType(Block block, ResourceLocation texture, ResourceLocation renderType, boolean registerItem) {
        BlockModelBuilder builder = models().cubeAll(name(block), texture).renderType(renderType);
        getVariantBuilder(block).partialState().setModels(new ConfiguredModel(builder));
        if (registerItem) {
            return blockItem(block, builder);
        }
        return null;
    }

    public ItemModelBuilder simpleBlockCustomRenderType(DeferredHolder<Block, ? extends Block> block, ResourceLocation texture, ResourceLocation renderType, boolean registerItem) {
        return simpleBlockCustomRenderType(block.get(), texture, renderType, registerItem);
    }

    public ItemModelBuilder airBlock(DeferredHolder<Block, ? extends Block> block, String particleTexture, boolean registerItem) {
        return airBlock(block.get(), particleTexture, registerItem);
    }

    public ItemModelBuilder airBlock(Block block, String particleTexture, boolean registerItem) {
        BlockModelBuilder builder = models().getBuilder(name(block)).texture("particle", modLoc(particleTexture)).renderType("cutout");
        getVariantBuilder(block).partialState().setModels(new ConfiguredModel(builder));
        if (registerItem) {
            return blockItem(block, builder);
        }
        return null;
    }

    public ItemModelBuilder bottomSlabBlock(DeferredHolder<Block, ? extends Block> block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, boolean registerItem) {
        return bottomSlabBlock(block.get(), side, bottom, top, registerItem);
    }

    public ItemModelBuilder bottomSlabBlock(Block block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, boolean registerItem) {
        BlockModelBuilder builder = models().slab(name(block), side, bottom, top);
        getVariantBuilder(block).partialState().setModels(new ConfiguredModel(builder));
        if (registerItem) {
            return blockItem(block, builder);
        }
        return null;
    }

    public ItemModelBuilder horrRotatedBlock(DeferredHolder<Block, ? extends Block> block, ModelFile modelFile, boolean registerItem) {
        return horrRotatedBlock(block, modelFile, 0, 0, registerItem);
    }

    public ItemModelBuilder horrRotatedBlock(DeferredHolder<Block, ? extends Block> block, ModelFile modelFile, int yRotationOffset, int xRotation, boolean registerItem) {
        return horrRotatedBlock(block.get(), modelFile, yRotationOffset, xRotation, registerItem);
    }

    public ItemModelBuilder horrRotatedBlock(Block block, ModelFile file, boolean registerItem) {
        return horrRotatedBlock(block, file, 0, 0, registerItem);
    }

    public ItemModelBuilder horrRotatedBlock(Block block, ModelFile file, int yRotationOffset, int xRotation, boolean registerItem) {
        getVariantBuilder(block)//
                .partialState().with(VoltaicBlockStates.FACING, Direction.NORTH).modelForState().modelFile(file).rotationY((270 + yRotationOffset) % 360).rotationX(xRotation).addModel()//
                .partialState().with(VoltaicBlockStates.FACING, Direction.EAST).modelForState().modelFile(file).rotationY((0 + yRotationOffset) % 360).rotationX(xRotation).addModel()//
                .partialState().with(VoltaicBlockStates.FACING, Direction.SOUTH).modelForState().modelFile(file).rotationY((90 + yRotationOffset) % 360).rotationX(xRotation).addModel()//
                .partialState().with(VoltaicBlockStates.FACING, Direction.WEST).modelForState().modelFile(file).rotationY((180 + yRotationOffset) % 360).rotationX(xRotation).addModel();
        if (registerItem) {
            return blockItem(block, file);
        }
        return null;
    }

    public ItemModelBuilder horrRotatedLitBlock(DeferredHolder<Block, ? extends Block> block, ModelFile off, ModelFile on, boolean registerItem) {
        return horrRotatedLitBlock(block, off, on, 0, 0, registerItem);
    }

    public ItemModelBuilder horrRotatedLitBlock(DeferredHolder<Block, ? extends Block> block, ModelFile off, ModelFile on, int yRotationOffset, int xRotation, boolean registerItem) {
        return horrRotatedLitBlock(block.get(), off, on, yRotationOffset, xRotation, registerItem);
    }

    public ItemModelBuilder horrRotatedLitBlock(Block block, ModelFile off, ModelFile on, boolean registerItem) {
        return horrRotatedLitBlock(block, off, on, 0, 0, registerItem);
    }

    public ItemModelBuilder horrRotatedLitBlock(Block block, ModelFile off, ModelFile on, int yRotationOffset, int xRotation, boolean registerItem) {
        getVariantBuilder(block)//
                .partialState().with(VoltaicBlockStates.FACING, Direction.NORTH).with(VoltaicBlockStates.LIT, false).modelForState().modelFile(off).rotationY((270 + yRotationOffset) % 360).rotationX(xRotation).addModel()//
                .partialState().with(VoltaicBlockStates.FACING, Direction.EAST).with(VoltaicBlockStates.LIT, false).modelForState().modelFile(off).rotationY((0 + yRotationOffset) % 360).rotationX(xRotation).addModel()//
                .partialState().with(VoltaicBlockStates.FACING, Direction.SOUTH).with(VoltaicBlockStates.LIT, false).modelForState().modelFile(off).rotationY((90 + yRotationOffset) % 360).rotationX(xRotation).addModel()//
                .partialState().with(VoltaicBlockStates.FACING, Direction.WEST).with(VoltaicBlockStates.LIT, false).modelForState().modelFile(off).rotationY((180 + yRotationOffset) % 360).rotationX(xRotation).addModel()//
                .partialState().with(VoltaicBlockStates.FACING, Direction.NORTH).with(VoltaicBlockStates.LIT, true).modelForState().modelFile(on).rotationY((270 + yRotationOffset) % 360).rotationX(xRotation).addModel()//
                .partialState().with(VoltaicBlockStates.FACING, Direction.EAST).with(VoltaicBlockStates.LIT, true).modelForState().modelFile(on).rotationY((0 + yRotationOffset) % 360).rotationX(xRotation).addModel()//
                .partialState().with(VoltaicBlockStates.FACING, Direction.SOUTH).with(VoltaicBlockStates.LIT, true).modelForState().modelFile(on).rotationY((90 + yRotationOffset) % 360).rotationX(xRotation).addModel()//
                .partialState().with(VoltaicBlockStates.FACING, Direction.WEST).with(VoltaicBlockStates.LIT, true).modelForState().modelFile(on).rotationY((180 + yRotationOffset) % 360).rotationX(xRotation).addModel();
        if (registerItem) {
            return blockItem(block, off);
        }
        return null;

    }

    public ItemModelBuilder redstoneToggleBlock(DeferredHolder<Block, ? extends Block> block, ModelFile off, ModelFile on, boolean registerItem) {
        return redstoneToggleBlock(block.get(), off, on, registerItem);
    }

    public ItemModelBuilder redstoneToggleBlock(Block block, ModelFile off, ModelFile on, boolean registerItem) {
        getVariantBuilder(block).partialState().with(VoltaicBlockStates.LIT, false).modelForState().modelFile(off).addModel().partialState().with(VoltaicBlockStates.LIT, true).modelForState().modelFile(on).addModel();
        if (registerItem) {
            return blockItem(block, off);
        }
        return null;

    }

    /*
     * private void omniDirBlock(DeferredHolder<Block, Block> block, ModelFile model, boolean registerItem) { getVariantBuilder(block.get()).partialState().with(ElectrodynamicsBlockStates.FACING, Direction.NORTH) .with(OverdriveBlockStates.VERTICAL_FACING, VerticalFacing.NONE).modelForState().modelFile(model) .rotationY(0).addModel().partialState().with(ElectrodynamicsBlockStates.FACING, Direction.EAST) .with(OverdriveBlockStates.VERTICAL_FACING, VerticalFacing.NONE).modelForState().modelFile(model) .rotationY(90).addModel().partialState().with(ElectrodynamicsBlockStates.FACING, Direction.SOUTH) .with(OverdriveBlockStates.VERTICAL_FACING, VerticalFacing.NONE).modelForState().modelFile(model) .rotationY(180).addModel().partialState().with(ElectrodynamicsBlockStates.FACING, Direction.WEST) .with(OverdriveBlockStates.VERTICAL_FACING, VerticalFacing.NONE).modelForState().modelFile(model) .rotationY(270).addModel().partialState().with(ElectrodynamicsBlockStates.FACING, Direction.NORTH) .with(OverdriveBlockStates.VERTICAL_FACING, VerticalFacing.UP).modelForState().modelFile(model) .rotationY(0).rotationX(270).addModel().partialState().with(ElectrodynamicsBlockStates.FACING, Direction.EAST) .with(OverdriveBlockStates.VERTICAL_FACING,
     * VerticalFacing.UP).modelForState().modelFile(model) .rotationY(90).rotationX(270).addModel().partialState().with(ElectrodynamicsBlockStates.FACING, Direction.SOUTH) .with(OverdriveBlockStates.VERTICAL_FACING, VerticalFacing.UP).modelForState().modelFile(model) .rotationY(180).rotationX(270).addModel().partialState().with(ElectrodynamicsBlockStates.FACING, Direction.WEST) .with(OverdriveBlockStates.VERTICAL_FACING, VerticalFacing.UP).modelForState().modelFile(model) .rotationY(270).rotationX(270).addModel().partialState() .with(ElectrodynamicsBlockStates.FACING, Direction.NORTH) .with(OverdriveBlockStates.VERTICAL_FACING, VerticalFacing.DOWN).modelForState().modelFile(model) .rotationY(0).rotationX(90).addModel().partialState().with(ElectrodynamicsBlockStates.FACING, Direction.EAST) .with(OverdriveBlockStates.VERTICAL_FACING, VerticalFacing.DOWN).modelForState().modelFile(model) .rotationY(90).rotationX(90).addModel().partialState().with(ElectrodynamicsBlockStates.FACING, Direction.SOUTH) .with(OverdriveBlockStates.VERTICAL_FACING, VerticalFacing.DOWN).modelForState().modelFile(model) .rotationY(180).rotationX(90).addModel().partialState().with(ElectrodynamicsBlockStates.FACING, Direction.WEST)
     * .with(OverdriveBlockStates.VERTICAL_FACING, VerticalFacing.DOWN).modelForState().modelFile(model) .rotationY(270).rotationX(90).addModel(); if (registerItem) simpleBlockItem(block.get(), model); }
     *
     */
    public void wire(Block block, ModelFile none, ModelFile side, boolean registerItem) {
        ModelFile model = models().withExistingParent(name(block), Voltaic.vanillarl("cube")).customLoader(WireModelBuilder::begin).models(none, side, side).end().renderType(Voltaic.vanillarl("cutout"));

        getVariantBuilder(block).partialState().addModels(new ConfiguredModel(model));

        if (registerItem) {
            simpleBlockItem(block, none);
        }

    }

    public void slaveNode(Block block, String particleTexture) {
        BlockModelBuilder builder = models().getBuilder(name(block)).customLoader(SlaveNodeModelBuilder::begin).model(existingBlock(VoltaicBlocks.BLOCK_MULTISUBNODE.get())).end().texture("particle", modLoc(particleTexture)).renderType("cutout");
        getVariantBuilder(block).partialState().setModels(new ConfiguredModel(builder));
    }

    public ItemModelBuilder snowyBlock(Block block, ModelFile noSnow, ModelFile withSnow, boolean registerItem) {
        getVariantBuilder(block).partialState().with(SnowyDirtBlock.SNOWY, false).modelForState().modelFile(noSnow).addModel().partialState().with(SnowyDirtBlock.SNOWY, true).modelForState().modelFile(withSnow).rotationY(0).addModel();

        if (registerItem) {
            return blockItem(block, noSnow);
        }
        return null;
    }

    // gotta love dealing with mojank
    public ItemModelBuilder pressurePlateBlock(PressurePlateBlock block, ResourceLocation texture, @Nullable ResourceLocation renderType, boolean registerItem) {
        ModelFile pressurePlate = models().pressurePlate(name(block), texture);
        ModelFile pressurePlateDown = models().pressurePlateDown(name(block) + "_down", texture);
        if (renderType != null) {
            pressurePlate = models().pressurePlate(name(block), texture).renderType(renderType);
            pressurePlateDown = models().pressurePlateDown(name(block) + "_down", texture).renderType(renderType);
        }
        return pressurePlateBlock(block, pressurePlate, pressurePlateDown, renderType, registerItem);
    }

    public ItemModelBuilder pressurePlateBlock(PressurePlateBlock block, ModelFile pressurePlate, ModelFile pressurePlateDown, @Nullable ResourceLocation renderType, boolean registerItem) {
        getVariantBuilder(block).partialState().with(PressurePlateBlock.POWERED, true).addModels(new ConfiguredModel(pressurePlateDown)).partialState().with(PressurePlateBlock.POWERED, false).addModels(new ConfiguredModel(pressurePlate));
        if (registerItem) {
            return blockItem(block, pressurePlate);
        }
        return null;
    }

    public ItemModelBuilder simpleColumnBlock(Block block, ResourceLocation side, ResourceLocation top, boolean registerItem) {
        BlockModelBuilder builder = models().cubeColumn(name(block), side, top);
        getVariantBuilder(block).partialState().setModels(new ConfiguredModel(builder));
        if (registerItem) {
            return blockItem(block, builder);
        }
        return null;
    }

    public ItemModelBuilder crossBlock(DeferredHolder<Block, ? extends Block> block, ResourceLocation texture, @Nullable ResourceLocation renderType, boolean registerItem) {
        return crossBlock(block.get(), texture, renderType, registerItem);
    }

    public ItemModelBuilder crossBlock(Block block, ResourceLocation texture, @Nullable ResourceLocation renderType, boolean registerItem) {
        ModelFile cross;
        if (renderType == null) {
            cross = models().cross(name(block), texture);
        } else {
            cross = models().cross(name(block), texture).renderType(renderType);
        }
        getVariantBuilder(block).partialState().setModels(new ConfiguredModel(cross));
        if (registerItem) {
            return blockItem(block, cross);
        }
        return null;
    }

    public BlockModelBuilder getObjModel(String name, String modelLoc) {
        return models().withExistingParent("block/" + name, "cube").customLoader(ObjModelBuilder::begin).flipV(true).modelLocation(modLoc("models/" + modelLoc + ".obj")).end();
    }

    public BlockModelBuilder blockTopBottom(DeferredHolder<Block, ? extends Block> block, String top, String bottom, String side) {
        return models().cubeBottomTop(BuiltInRegistries.BLOCK.getKey(block.get()).getPath(), ResourceLocation.fromNamespaceAndPath(modID, side), ResourceLocation.fromNamespaceAndPath(modID, bottom), ResourceLocation.fromNamespaceAndPath(modID, top));
    }

    public ItemModelBuilder blockItem(Block block, ModelFile model) {
        return itemModels().getBuilder(key(block).getPath()).parent(model);
    }

    public ResourceLocation key(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    public String name(Block block) {
        return key(block).getPath();
    }

    public ExistingModelFile existingBlock(DeferredHolder<Block, ? extends Block> block) {
        return existingBlock(block.getId());
    }

    public ExistingModelFile existingBlock(Block block) {
        return existingBlock(BuiltInRegistries.BLOCK.getKey(block));
    }

    public ExistingModelFile existingBlock(ResourceLocation loc) {
        return models().getExistingFile(loc);
    }

    public ResourceLocation blockLoc(String texture) {
        return modLoc("block/" + texture);
    }

    public ResourceLocation modelLoc(String texture) {
        return modLoc("model/" + texture);
    }

}
