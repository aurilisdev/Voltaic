package voltaic.api.radiation;

import voltaic.api.radiation.util.*;
import voltaic.common.reloadlistener.RadiationShieldingRegister;
import voltaic.common.settings.VoltaicConstants;
import voltaic.prefab.utilities.CapabilityUtils;
import voltaic.registers.VoltaicCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import java.util.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Dynamic;

public class RadiationManager implements IRadiationManager, ICapabilitySerializable<CompoundTag> {
	
	private final LazyOptional<IRadiationManager> lazyOptional = LazyOptional.of(() -> this);
	
	private final HashMap<BlockPos, SimpleRadiationSource> permanentSources = new HashMap<>();
	private final HashMap<BlockPos, IRadiationManager.TemporaryRadiationSource> temporarySources = new HashMap<>();
	private final HashMap<BlockPos, IRadiationManager.FadingRadiationSource> fadingSources = new HashMap<>();
	private final HashMap<BlockPosVolume, Double> localizedDisapations = new HashMap<>();
	private double defaultDisipation = VoltaicConstants.BACKROUND_RADIATION_DISSIPATION;

    public RadiationManager() {

    }
    
    @Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if(cap == VoltaicCapabilities.CAPABILITY_RADIATIONMANAGER) {
			return lazyOptional.cast();
		}
		return LazyOptional.empty();
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag total = new CompoundTag();
		
		// Permanent 
		
		CompoundTag permanent = new CompoundTag();
        int size = permanentSources.size();
        permanent.putInt("size", size);
        int i = 0;
        for (Map.Entry<BlockPos, SimpleRadiationSource> entry : permanentSources.entrySet()) {
            CompoundTag store = new CompoundTag();
            BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, entry.getKey()).result().ifPresent(tag -> store.put("pos", tag));
            SimpleRadiationSource.CODEC.encodeStart(NbtOps.INSTANCE, entry.getValue()).result().ifPresent(tag -> store.put("radiation", tag));
            permanent.put(i + "", store);
            i++;
        }
       
        total.put("permanentradiationsources", permanent);
        
        // Temporary
        
        CompoundTag temporary = new CompoundTag();
        size = temporarySources.size();
        temporary.putInt("size", size);
        i = 0;
        for (Map.Entry<BlockPos, IRadiationManager.TemporaryRadiationSource> entry : temporarySources.entrySet()) {
            CompoundTag store = new CompoundTag();
            BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, entry.getKey()).result().ifPresent(tag -> store.put("pos", tag));
            IRadiationManager.TemporaryRadiationSource.CODEC.encodeStart(NbtOps.INSTANCE, entry.getValue()).result().ifPresent(tag -> store.put("radiation", tag));
            temporary.put(i + "", store);
            i++;
        }
        
		total.put("temporaryradiationsources", temporary);
		
		// Fading
		
		CompoundTag fading = new CompoundTag();
        size = fadingSources.size();
        fading.putInt("size", size);
        i = 0;
        for (Map.Entry<BlockPos, IRadiationManager.FadingRadiationSource> entry : fadingSources.entrySet()) {
            CompoundTag store = new CompoundTag();
            BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, entry.getKey()).result().ifPresent(tag -> store.put("pos", tag));
            IRadiationManager.FadingRadiationSource.CODEC.encodeStart(NbtOps.INSTANCE, entry.getValue()).result().ifPresent(tag -> store.put("radiation", tag));
            fading.put(i + "", store);
            i++;
        }
		
		total.put("fadingradiationsources", fading);
		
		// Localized
		
		CompoundTag localized = new CompoundTag();
        size = localizedDisapations.size();
        localized.putInt("size", size);
        i = 0;
        for (Map.Entry<BlockPosVolume, Double> entry : localizedDisapations.entrySet()) {
            CompoundTag store = new CompoundTag();
            BlockPosVolume.CODEC.encodeStart(NbtOps.INSTANCE, entry.getKey()).result().ifPresent(tag -> store.put("pos", tag));
            store.putDouble("amount", entry.getValue());
            localized.put(i + "", store);
            i++;
        }
        
        total.put("localizeddissipations", localized);
        
        // Default Dissipation
        
        total.putDouble("defaultdissipation", defaultDisipation);

		return total;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		if(VoltaicCapabilities.CAPABILITY_RADIATIONMANAGER == null || !nbt.contains("permanentradiationsources")) {
			return;
		}
		
		// Permanent
		
		CompoundTag permanent = nbt.getCompound("permanentradiationsources");
		
		permanentSources.clear();

        int size = permanent.getInt("size");
        for (int i = 0; i < size; i++) {

            CompoundTag stored = permanent.getCompound("" + i);
            permanentSources.put(BlockPos.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, stored.get("pos"))).result().get(), SimpleRadiationSource.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, stored.getCompound("radiation"))).result().get());
        }
		
		// Temporary
        
        temporarySources.clear();
		
		CompoundTag temporary = nbt.getCompound("temporaryradiationsources");

        size = temporary.getInt("size");
        for (int i = 0; i < size; i++) {

            CompoundTag stored = temporary.getCompound("" + i);
            temporarySources.put(BlockPos.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, stored.get("pos"))).result().get(), IRadiationManager.TemporaryRadiationSource.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, stored.getCompound("radiation"))).result().get());
        }
        
        // Fading
        
        fadingSources.clear();
        
        CompoundTag fading = nbt.getCompound("temporaryradiationsources");
        
        size = fading.getInt("size");
        for (int i = 0; i < size; i++) {

            CompoundTag stored = fading.getCompound("" + i);
            fadingSources.put(BlockPos.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, stored.get("pos"))).result().get(), IRadiationManager.FadingRadiationSource.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, stored.getCompound("radiation"))).result().get());
        }
        
        // Localized Dissipations 
        
        localizedDisapations.clear();
        
        CompoundTag localized = nbt.getCompound("localizeddissipations");
        
        size = localized.getInt("size");
        for (int i = 0; i < size; i++) {

            CompoundTag stored = localized.getCompound("" + i);
            localizedDisapations.put(BlockPosVolume.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, stored.get("pos"))).result().get(), stored.getDouble("amount"));
        }
        
        // Default Dissipation
        
        defaultDisipation = nbt.getDouble("defaultdissipation");

	
	}

    @Override
    public List<SimpleRadiationSource> getPermanentSources(Level world) {
        return new ArrayList<>(permanentSources.values());
    }

    @Override
    public List<TemporaryRadiationSource> getTemporarySources(Level world) {
        return new ArrayList<>(temporarySources.values());
    }

    @Override
    public List<FadingRadiationSource> getFadingSources(Level world) {
        return new ArrayList<>(fadingSources.values());
    }

    @Override
    public List<BlockPos> getPermanentLocations(Level world) {
        return new ArrayList<>(permanentSources.keySet());
    }

    @Override
    public List<BlockPos> getTemporaryLocations(Level world) {
        return new ArrayList<>(temporarySources.keySet());
    }

    @Override
    public List<BlockPos> getFadingLocations(Level world) {
        return new ArrayList<>(fadingSources.keySet());
    }

    @Override
    public void addRadiationSource(SimpleRadiationSource source, Level world) {
        if (source.isTemporary()) {
            TemporaryRadiationSource existing = temporarySources.getOrDefault(source.getSourceLocation(), TemporaryRadiationSource.NONE);
            TemporaryRadiationSource combined = new TemporaryRadiationSource(source.ticks() + existing.ticks, Math.max(source.getRadiationStrength(), existing.strength), source.getRadiationAmount() + existing.radiation, existing.leaveFading || source.shouldLeaveLingeringSource(), Math.max(source.distance(), existing.distance));
            temporarySources.put(source.getSourceLocation(), combined);
        } else {
            SimpleRadiationSource existing = permanentSources.getOrDefault(source.getSourceLocation(), SimpleRadiationSource.NONE);
            permanentSources.put(source.getSourceLocation(), new SimpleRadiationSource(existing.getRadiationAmount() + source.getRadiationAmount(), Math.max(existing.getRadiationStrength(), source.getRadiationStrength()), Math.max(existing.getDistanceSpread(), source.getDistanceSpread()), false, existing.getPersistanceTicks() + source.getPersistanceTicks(), source.getSourceLocation(), existing.shouldLinger() || source.shouldLinger()));
        }
    }

    @Override
    public int getReachOfSource(Level world, BlockPos pos) {
        return Math.max(temporarySources.getOrDefault(pos, TemporaryRadiationSource.NONE).distance, Math.max(permanentSources.getOrDefault(pos, SimpleRadiationSource.NONE).getDistanceSpread(), fadingSources.getOrDefault(pos, FadingRadiationSource.NONE).distance));
    }

    @Override
    public void setDisipation(double radiationDisipation, Level world) {
        defaultDisipation = radiationDisipation;
    }

    @Override
    public void setLocalizedDisipation(double disipation, BlockPosVolume area, Level world) {
        localizedDisapations.put(area, disipation + localizedDisapations.getOrDefault(area, 0.0));
    }

    @Override
    public void removeLocalizedDisipation(BlockPosVolume area, Level world) {
        localizedDisapations.remove(area);
    }

    @Override
    public boolean removeRadiationSource(BlockPos pos, boolean shouldLeaveFadingSource, Level world) {
        SimpleRadiationSource source = permanentSources.remove(pos);
        if (source == null) {
            return false;
        }
        if (shouldLeaveFadingSource) {
            fadingSources.put(pos, new FadingRadiationSource(source.getDistanceSpread(), source.getRadiationStrength(), source.getRadiationAmount()));
        }
        return true;
    }

    @Override
    public void wipeAllSources(Level level) {
    	permanentSources.clear();
    	temporarySources.clear();
    	fadingSources.clear();
    }

    @Override
    public void tick(Level world) {

        /* Apply Radiation */

        BlockPos position;
        SimpleRadiationSource permanentSource;

        for (Map.Entry<BlockPos, SimpleRadiationSource> entry : permanentSources.entrySet()) {
            position = entry.getKey();
            permanentSource = entry.getValue();
            for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, new AABB(position.getX() - permanentSource.distance(), position.getY() - permanentSource.distance(), position.getZ() - permanentSource.distance(), position.getX() + permanentSource.distance() + 1, position.getY() + permanentSource.distance() + 1, position.getZ() + permanentSource.distance() + 1))) {
                IRadiationRecipient capability = entity.getCapability(VoltaicCapabilities.CAPABILITY_RADIATIONRECIPIENT).orElse(CapabilityUtils.EMPTY_RADIATION_REPIPIENT);
                if (capability == CapabilityUtils.EMPTY_RADIATION_REPIPIENT) {
                    continue;
                }

                for (int i = 0; i < (int) Math.ceil(entity.getBbHeight()); i++) {
                    capability.recieveRadiation(entity, getAppliedRadiation(world, position, entity.getOnPos().above(i + 1), permanentSource.getRadiationAmount(), permanentSource.getRadiationStrength()), permanentSource.getRadiationStrength());
                }
            }
        }

        TemporaryRadiationSource temporarySource;

        for (Map.Entry<BlockPos, TemporaryRadiationSource> entry : temporarySources.entrySet()) {
            position = entry.getKey();
            temporarySource = entry.getValue();

            for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, new AABB(position.getX() - temporarySource.distance, position.getY() - temporarySource.distance, position.getZ() - temporarySource.distance, position.getX() + temporarySource.distance + 1, position.getY() + temporarySource.distance + 1, position.getZ() + temporarySource.distance + 1))) {

                IRadiationRecipient capability = entity.getCapability(VoltaicCapabilities.CAPABILITY_RADIATIONRECIPIENT).orElse(CapabilityUtils.EMPTY_RADIATION_REPIPIENT);
                if (capability == CapabilityUtils.EMPTY_RADIATION_REPIPIENT) {
                    continue;
                }

                for (int i = 0; i < (int) Math.ceil(entity.getBbHeight()); i++) {
                    capability.recieveRadiation(entity, getAppliedRadiation(world, position, entity.getOnPos().above(i + 1), temporarySource.radiation, temporarySource.strength), temporarySource.strength);
                }


            }


        }

        FadingRadiationSource fadingSource;

        for (Map.Entry<BlockPos, FadingRadiationSource> entry : fadingSources.entrySet()) {

            position = entry.getKey();
            fadingSource = entry.getValue();

            for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, new AABB(position.getX() - fadingSource.distance, position.getY() - fadingSource.distance, position.getZ() - fadingSource.distance, position.getX() + fadingSource.distance + 1, position.getY() + fadingSource.distance + 1, position.getZ() + fadingSource.distance + 1))) {
                IRadiationRecipient capability = entity.getCapability(VoltaicCapabilities.CAPABILITY_RADIATIONRECIPIENT).orElse(CapabilityUtils.EMPTY_RADIATION_REPIPIENT);
                if (capability == CapabilityUtils.EMPTY_RADIATION_REPIPIENT) {
                    continue;
                }

                for (int i = 0; i < (int) Math.ceil(entity.getBbHeight()); i++) {
                    capability.recieveRadiation(entity, getAppliedRadiation(world, position, entity.getOnPos().above(i + 1), fadingSource.radiation, fadingSource.strength), fadingSource.strength);
                }

            }

        }


        /* Handle Temporary Sources */


        for(Iterator<Map.Entry<BlockPos, TemporaryRadiationSource>> it = temporarySources.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<BlockPos, TemporaryRadiationSource> entry = it.next();
            position = entry.getKey();
            temporarySource = entry.getValue();
	
            temporarySource.ticks = temporarySource.ticks - 1;	
            if (temporarySource.ticks < 0) {
                it.remove();
                if (temporarySource.leaveFading) {
                    FadingRadiationSource existing = fadingSources.getOrDefault(position, FadingRadiationSource.NONE);
                    fadingSources.put(position, new FadingRadiationSource(Math.max(temporarySource.distance, existing.distance), Math.max(temporarySource.strength, existing.strength), temporarySource.radiation + existing.radiation));
                }
            }

        }


        /* Handle Fading Sources */

        for(Iterator<Map.Entry<BlockPos, FadingRadiationSource>> it = fadingSources.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<BlockPos, FadingRadiationSource> entry = it.next();

            position = entry.getKey();
            fadingSource = entry.getValue();

            boolean hit = false;

            for (Map.Entry<BlockPosVolume, Double> localized : localizedDisapations.entrySet()) {
                if (localized.getKey().isIn(position)) {
                    fadingSource.radiation = fadingSource.radiation - localized.getValue();
                    hit = true;
                    break;
                }
            }

            if (!hit) {
                fadingSource.radiation = fadingSource.radiation - defaultDisipation;
            }

            if (fadingSource.radiation <= 0) {
                it.remove();
            }

        }

    }

    public static boolean isWithinRange(BlockPos start, BlockPos end, int distance) {
        if (Math.abs(end.getX() - start.getX()) > distance || Math.abs(end.getY() - start.getY()) > distance || Math.abs(end.getZ() - start.getZ()) > distance) {
            return false;
        }
	return true;
    }

    public static List<Block> raycastToBlockPos(Level world, BlockPos start, BlockPos end) {

        List<Block> blocks = new ArrayList<>();

        int deltaX = end.getX() - start.getX();
        int deltaY = end.getY() - start.getY();
        int deltaZ = end.getZ() - start.getZ();

        double magnitude = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

        int maxChecks = (int) magnitude;

        double incX = deltaX / magnitude;
        double incY = deltaY / magnitude;
        double incZ = deltaZ / magnitude;

        double x = 0;
        double y = 0;
        double z = 0;

        BlockPos toCheck = start;

        int i = 0;

        while (i < maxChecks) {

            x += incX;
            y += incY;
            z += incZ;
            toCheck = new BlockPos((int) (start.getX() + x), (int) (start.getY() + y), (int) (start.getZ() + z));
            if (!toCheck.equals(start) && !toCheck.equals(end)) {
                blocks.add(world.getBlockState(toCheck).getBlock());
                //world.setBlockAndUpdate(toCheck, Blocks.COBBLESTONE.defaultBlockState());
            }

            i++;

        }

        return blocks;
    }

    public static double getAppliedRadiation(Level world, BlockPos source, BlockPos entity, double amount, double strength) {

        List<Block> blocks = raycastToBlockPos(world, source, entity);

        if (blocks.isEmpty()) {
            return amount;
        }

        RadiationShielding shielding;

        for (Block block : blocks) {
            shielding = RadiationShieldingRegister.getValue(block);
            if (shielding.level() < strength) {
                continue;
            }
            amount -= shielding.amount();
            if (amount <= 0) {
                return 0;
            }

        }

        return amount / entity.distSqr(source);

    }

}
