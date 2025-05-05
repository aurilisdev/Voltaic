package voltaic.prefab.tile.types;

import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.utils.IComponentFluidHandler;
import voltaic.prefab.utilities.CapabilityUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidTank;

//You come up with a better name :D
public class GenericMaterialTile extends GenericTile {

    public GenericMaterialTile(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }
    
    @Override
    public ActionResultType use(PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
    	
    	ItemStack used = player.getItemInHand(handIn);
    	
    	if(used.isEmpty()) {
    		return super.use(player, handIn, hit);
    	}
    	
    	World world = getLevel();

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

                    world.playSound(null, player.blockPosition(), SoundEvents.BUCKET_EMPTY, SoundCategory.PLAYERS, 1, 1);

                    player.setItemInHand(handIn, handlerFluidItem.getContainer());

                }

                return ActionResultType.CONSUME;

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

                    world.playSound(null, player.blockPosition(), SoundEvents.BUCKET_FILL, SoundCategory.PLAYERS, 1, 1);

                    player.setItemInHand(handIn, handlerFluidItem.getContainer());

                }

                return ActionResultType.CONSUME;

            }
        }

        return super.use(player, handIn, hit);
    }

}
