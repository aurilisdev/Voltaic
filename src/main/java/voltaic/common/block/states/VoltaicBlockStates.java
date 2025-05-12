package voltaic.common.block.states;

import net.minecraft.block.HorizontalBlock;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.properties.BlockStateProperties;

public class VoltaicBlockStates {

    public static void init() {
    }

    public static final BooleanProperty HAS_SCAFFOLDING = BooleanProperty.create("hasscaffolding");
    public static final DirectionProperty FACING = HorizontalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

}
