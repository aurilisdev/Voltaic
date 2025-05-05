package voltaic.api.tile;

import net.minecraft.block.BlockRenderType;
import net.minecraft.tileentity.TileEntity;
import voltaic.api.multiblock.subnodebased.parent.IMultiblockParentBlock;
import voltaic.common.block.voxelshapes.VoxelShapeProvider;

public interface IMachine {

    public TileEntitySupplier<TileEntity> getBlockEntitySupplier();

    public int getLitBrightness();

    public BlockRenderType getRenderShape();

    public boolean isMultiblock();

    public boolean propegatesLightDown();

    public boolean isPlayerStorable();

    public default IMultiblockParentBlock.SubnodeWrapper getSubnodes() {
        return IMultiblockParentBlock.SubnodeWrapper.EMPTY;
    }

    public VoxelShapeProvider getVoxelShapeProvider();

    public boolean usesLit();

}
