package voltaic.prefab.tile.types;

import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.utils.IComponentFluidHandler;
import voltaic.prefab.utilities.CapabilityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidTank;

//You come up with a better name :D
public class GenericMaterialTile extends GenericTile {

    public GenericMaterialTile(BlockEntityType<?> tileEntityTypeIn, BlockPos worldPos, BlockState blockState) {
        super(tileEntityTypeIn, worldPos, blockState);
    }
    
    @Override
    public InteractionResult use(Player player, InteractionHand handIn, BlockHitResult hit) {
    	
    	ItemStack used = player.getItemInHand(handIn);
    	
    	if(used.isEmpty()) {
    		return super.use(player, handIn, hit);
    	}
    	
    	Level world = getLevel();

        IFluidHandlerItem handlerFluidItem = used.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).resolve().orElse(CapabilityUtils.EMPTY_FLUID_ITEM);

        if (handlerFluidItem != CapabilityUtils.EMPTY_FLUID_ITEM && hasComponent(IComponentType.FluidHandler)) {

            IComponentFluidHandler fluidHandler = getComponent(IComponentType.FluidHandler);

            // first try to drain the item
            for (FluidTank tank : fluidHandler.getInputTanks()) {

                int space = tank.getSpace();

                FluidStack containedFluid = handlerFluidItem.drain(space, FluidAction.SIMULATE);

                if (containedFluid.isEmpty()) {
                    continue;
                }

                if (!world.isClientSide) {

                    tank.fill(containedFluid, FluidAction.EXECUTE);

                    if (!player.isCreative()) {

                        handlerFluidItem.drain(space, FluidAction.EXECUTE);

                    }

                    world.playSound(null, player.blockPosition(), SoundEvents.BUCKET_EMPTY, SoundSource.PLAYERS, 1, 1);

                    player.setItemInHand(handIn, handlerFluidItem.getContainer());

                }

                return InteractionResult.CONSUME;

            }
            // now try to fill it
            for (FluidTank tank : fluidHandler.getOutputTanks()) {

                FluidStack tankFluid = tank.getFluid();

                int taken = handlerFluidItem.fill(tankFluid, FluidAction.EXECUTE);

                if (taken <= 0) {
                    continue;
                }

                if (!world.isClientSide) {

                    tank.drain(taken, FluidAction.EXECUTE);

                    world.playSound(null, player.blockPosition(), SoundEvents.BUCKET_FILL, SoundSource.PLAYERS, 1, 1);

                    player.setItemInHand(handIn, handlerFluidItem.getContainer());

                }

                return InteractionResult.CONSUME;

            }
        }

        return super.use(player, handIn, hit);
    }

}
