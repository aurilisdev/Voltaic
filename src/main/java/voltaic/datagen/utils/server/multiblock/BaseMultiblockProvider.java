package voltaic.datagen.utils.server.multiblock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import voltaic.Voltaic;
import voltaic.api.multiblock.assemblybased.Multiblock;
import voltaic.api.multiblock.assemblybased.MultiblockSlaveNode;
import voltaic.common.block.states.VoltaicBlockStates;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.conditions.WithConditions;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.JsonCodecProvider;

public abstract class BaseMultiblockProvider extends JsonCodecProvider<Multiblock> {

    public BaseMultiblockProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper, String modid) {
        super(output, PackOutput.Target.DATA_PACK, Voltaic.ID + "/" + Multiblock.FOLDER, PackType.SERVER_DATA, Multiblock.CODEC, lookupProvider, modid, existingFileHelper);
    }

    public void addMultiblock(ResourceLocation id, List<MultiblockSlaveNode> northFacingNodes) {

        HashMap<Direction, List<MultiblockSlaveNode>> nodeMap = new HashMap<>();

        nodeMap.put(Direction.NORTH, northFacingNodes);

        nodeMap.put(Direction.WEST, getRotatedNodes(northFacingNodes, Direction.WEST.get2DDataValue() - Direction.NORTH.get2DDataValue()));

        nodeMap.put(Direction.SOUTH, getRotatedNodes(nodeMap.get(Direction.WEST), Direction.SOUTH.get2DDataValue() - Direction.WEST.get2DDataValue()));

        nodeMap.put(Direction.EAST, getRotatedNodes(nodeMap.get(Direction.SOUTH), Direction.EAST.get2DDataValue() - Direction.SOUTH.get2DDataValue()));

        if (conditions.containsKey(id)) {
            throw new UnsupportedOperationException("Multiblock with id " + id.toString() + " already provided!");
        }

        conditions.put(id, new WithConditions<>(List.of(), new Multiblock(nodeMap)));
    }

    public static List<MultiblockSlaveNode> getRotatedNodes(List<MultiblockSlaveNode> slaveNodes, int delta2d) {

        List<MultiblockSlaveNode> returner = new ArrayList<>();
        BlockState placeState, replaceState;

        VoxelShape shape;

        final VoxelShape[] buffer = {Shapes.empty(), Shapes.empty()};

        Vec3i offset;

        int times = (delta2d + 4) % 4;

        for (MultiblockSlaveNode slaveNode : slaveNodes) {

            placeState = slaveNode.placeState();

            if (placeState.hasProperty(VoltaicBlockStates.FACING)) {
                placeState = placeState.setValue(VoltaicBlockStates.FACING, placeState.getValue(VoltaicBlockStates.FACING).getCounterClockWise());
            }

            replaceState = slaveNode.replaceState();

            if (replaceState.hasProperty(VoltaicBlockStates.FACING)) {
                replaceState = replaceState.setValue(VoltaicBlockStates.FACING, replaceState.getValue(VoltaicBlockStates.FACING).getCounterClockWise());
            }

            shape = slaveNode.renderShape();

            buffer[0] = shape;
            buffer[1] = Shapes.empty();

            for (int i = 0; i < times; i++) {
                buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(buffer[1], Shapes.create(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
                buffer[0] = buffer[1];
                buffer[1] = Shapes.empty();
            }

            shape = buffer[0];

            offset = rotateVector(Rotation.COUNTERCLOCKWISE_90, slaveNode.offset());

            returner.add(new MultiblockSlaveNode(placeState, replaceState, slaveNode.taggedBlocks(), offset, shape, slaveNode.model()));
        }

        return returner;

    }

    public static Vec3i rotateVector(Rotation rot, Vec3i original) {

        switch (rot) {
            case NONE:
            default:
                return original;
            case CLOCKWISE_90:
                return new Vec3i(-original.getZ(), original.getY(), original.getX());
            case CLOCKWISE_180:
                return new Vec3i(-original.getX(), original.getY(), -original.getZ());
            case COUNTERCLOCKWISE_90:
                return new Vec3i(original.getZ(), original.getY(), -original.getX());
        }

    }

}
