package voltaic.api.radiation;

import voltaic.api.radiation.util.*;
import voltaic.common.reloadlistener.RadiationShieldingRegister;
import voltaic.registers.VoltaicAttachmentTypes;
import voltaic.registers.VoltaicCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;

import java.util.*;

public class RadiationManager implements IRadiationManager {

    public RadiationManager() {

    }

    @Override
    public List<SimpleRadiationSource> getPermanentSources(Level world) {
        return new ArrayList<>(world.getData(VoltaicAttachmentTypes.PERMANENT_RADIATION_SOURCES).values());
    }

    @Override
    public List<TemporaryRadiationSource> getTemporarySources(Level world) {
        return new ArrayList<>(world.getData(VoltaicAttachmentTypes.TEMPORARY_RADIATION_SOURCES).values());
    }

    @Override
    public List<FadingRadiationSource> getFadingSources(Level world) {
        return new ArrayList<>(world.getData(VoltaicAttachmentTypes.FADING_RADIATION_SOURCES).values());
    }

    @Override
    public List<BlockPos> getPermanentLocations(Level world) {
        return new ArrayList<>(world.getData(VoltaicAttachmentTypes.PERMANENT_RADIATION_SOURCES).keySet());
    }

    @Override
    public List<BlockPos> getTemporaryLocations(Level world) {
        return new ArrayList<>(world.getData(VoltaicAttachmentTypes.TEMPORARY_RADIATION_SOURCES).keySet());
    }

    @Override
    public List<BlockPos> getFadingLocations(Level world) {
        return new ArrayList<>(world.getData(VoltaicAttachmentTypes.FADING_RADIATION_SOURCES).keySet());
    }

    @Override
    public void addRadiationSource(SimpleRadiationSource source, Level world) {
        if (source.isTemporary()) {
            HashMap<BlockPos, TemporaryRadiationSource> sources = world.getData(VoltaicAttachmentTypes.TEMPORARY_RADIATION_SOURCES);
            TemporaryRadiationSource existing = sources.getOrDefault(source.getSourceLocation(), TemporaryRadiationSource.NONE);
            TemporaryRadiationSource combined = new TemporaryRadiationSource(source.ticks() + existing.ticks, Math.max(source.getRadiationStrength(), existing.strength), source.getRadiationAmount() + existing.radiation, existing.leaveFading || source.shouldLeaveLingeringSource(), Math.max(source.distance(), existing.distance));
            sources.put(source.getSourceLocation(), combined);
            world.setData(VoltaicAttachmentTypes.TEMPORARY_RADIATION_SOURCES, sources);
        } else {
            HashMap<BlockPos, SimpleRadiationSource> sources = world.getData(VoltaicAttachmentTypes.PERMANENT_RADIATION_SOURCES);
            SimpleRadiationSource existing = sources.getOrDefault(source.getSourceLocation(), SimpleRadiationSource.NONE);
            sources.put(source.getSourceLocation(), new SimpleRadiationSource(existing.getRadiationAmount() + source.getRadiationAmount(), Math.max(existing.getRadiationStrength(), source.getRadiationStrength()), Math.max(existing.getDistanceSpread(), source.getDistanceSpread()), false, existing.getPersistanceTicks() + source.getPersistanceTicks(), source.getSourceLocation(), existing.shouldLinger() || source.shouldLinger()));
            world.setData(VoltaicAttachmentTypes.PERMANENT_RADIATION_SOURCES, sources);
        }
    }

    @Override
    public int getReachOfSource(Level world, BlockPos pos) {
        return Math.max(world.getData(VoltaicAttachmentTypes.TEMPORARY_RADIATION_SOURCES).getOrDefault(pos, TemporaryRadiationSource.NONE).distance, Math.max(world.getData(VoltaicAttachmentTypes.PERMANENT_RADIATION_SOURCES).getOrDefault(pos, SimpleRadiationSource.NONE).getDistanceSpread(), world.getData(VoltaicAttachmentTypes.FADING_RADIATION_SOURCES).getOrDefault(pos, FadingRadiationSource.NONE).distance));
    }

    @Override
    public void setDisipation(double radiationDisipation, Level world) {
        world.setData(VoltaicAttachmentTypes.DEFAULT_DISSIPATION, radiationDisipation);
    }

    @Override
    public void setLocalizedDisipation(double disipation, BlockPosVolume area, Level world) {
        HashMap<BlockPosVolume, Double> values = world.getData(VoltaicAttachmentTypes.LOCALIZED_DISSIPATIONS);
        values.put(area, disipation + values.getOrDefault(area, 0.0));
        world.setData(VoltaicAttachmentTypes.LOCALIZED_DISSIPATIONS, values);
    }

    @Override
    public void removeLocalizedDisipation(BlockPosVolume area, Level world) {
        HashMap<BlockPosVolume, Double> values = world.getData(VoltaicAttachmentTypes.LOCALIZED_DISSIPATIONS);
        values.remove(area);
        world.setData(VoltaicAttachmentTypes.LOCALIZED_DISSIPATIONS, values);
    }

    @Override
    public boolean removeRadiationSource(BlockPos pos, boolean shouldLeaveFadingSource, Level world) {
        HashMap<BlockPos, SimpleRadiationSource> sources = world.getData(VoltaicAttachmentTypes.PERMANENT_RADIATION_SOURCES);
        SimpleRadiationSource source = sources.remove(pos);
        world.setData(VoltaicAttachmentTypes.PERMANENT_RADIATION_SOURCES, sources);
        if (source == null) {
            return false;
        }
        if (shouldLeaveFadingSource) {
            HashMap<BlockPos, FadingRadiationSource> fadingSources = world.getData(VoltaicAttachmentTypes.FADING_RADIATION_SOURCES);
            fadingSources.put(pos, new FadingRadiationSource(source.getDistanceSpread(), source.getRadiationStrength(), source.getRadiationAmount()));
            world.setData(VoltaicAttachmentTypes.FADING_RADIATION_SOURCES, fadingSources);
        }
        return true;
    }

    @Override
    public void wipeAllSources(Level level) {
        level.removeData(VoltaicAttachmentTypes.PERMANENT_RADIATION_SOURCES);
        level.removeData(VoltaicAttachmentTypes.TEMPORARY_RADIATION_SOURCES);
        level.removeData(VoltaicAttachmentTypes.FADING_RADIATION_SOURCES);
    }

    @Override
    public void tick(Level world) {

        /* Apply Radiation */

        BlockPos position;
        SimpleRadiationSource permanentSource;

        for (Map.Entry<BlockPos, SimpleRadiationSource> entry : world.getData(VoltaicAttachmentTypes.PERMANENT_RADIATION_SOURCES).entrySet()) {
            position = entry.getKey();
            permanentSource = entry.getValue();
            for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, new AABB(position.getX() - permanentSource.distance(), position.getY() - permanentSource.distance(), position.getZ() - permanentSource.distance(), position.getX() + permanentSource.distance() + 1, position.getY() + permanentSource.distance() + 1, position.getZ() + permanentSource.distance() + 1))) {
                IRadiationRecipient capability = entity.getCapability(VoltaicCapabilities.CAPABILITY_RADIATIONRECIPIENT);
                if (capability == null) {
                    continue;
                }

                for (int i = 0; i < (int) Math.ceil(entity.getBbHeight()); i++) {
                    capability.recieveRadiation(entity, getAppliedRadiation(world, position, entity.getOnPos().above(i + 1), permanentSource.getRadiationAmount(), permanentSource.getRadiationStrength()), permanentSource.getRadiationStrength());
                }
            }
        }

        TemporaryRadiationSource temporarySource;

        HashMap<BlockPos, TemporaryRadiationSource> temporarySources = world.getData(VoltaicAttachmentTypes.TEMPORARY_RADIATION_SOURCES);

        for (Map.Entry<BlockPos, TemporaryRadiationSource> entry : temporarySources.entrySet()) {
            position = entry.getKey();
            temporarySource = entry.getValue();

            for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, new AABB(position.getX() - temporarySource.distance, position.getY() - temporarySource.distance, position.getZ() - temporarySource.distance, position.getX() + temporarySource.distance + 1, position.getY() + temporarySource.distance + 1, position.getZ() + temporarySource.distance + 1))) {

                IRadiationRecipient capability = entity.getCapability(VoltaicCapabilities.CAPABILITY_RADIATIONRECIPIENT);
                if (capability == null) {
                    continue;
                }

                for (int i = 0; i < (int) Math.ceil(entity.getBbHeight()); i++) {
                    capability.recieveRadiation(entity, getAppliedRadiation(world, position, entity.getOnPos().above(i + 1), temporarySource.radiation, temporarySource.strength), temporarySource.strength);
                }


            }


        }

        FadingRadiationSource fadingSource;

        HashMap<BlockPos, FadingRadiationSource> fadingSources = world.getData(VoltaicAttachmentTypes.FADING_RADIATION_SOURCES);

        for (Map.Entry<BlockPos, FadingRadiationSource> entry : fadingSources.entrySet()) {

            position = entry.getKey();
            fadingSource = entry.getValue();

            for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, new AABB(position.getX() - fadingSource.distance, position.getY() - fadingSource.distance, position.getZ() - fadingSource.distance, position.getX() + fadingSource.distance + 1, position.getY() + fadingSource.distance + 1, position.getZ() + fadingSource.distance + 1))) {
                IRadiationRecipient capability = entity.getCapability(VoltaicCapabilities.CAPABILITY_RADIATIONRECIPIENT);
                if (capability == null) {
                    continue;
                }

                for (int i = 0; i < (int) Math.ceil(entity.getBbHeight()); i++) {
                    capability.recieveRadiation(entity, getAppliedRadiation(world, position, entity.getOnPos().above(i + 1), fadingSource.radiation, fadingSource.strength), fadingSource.strength);
                }

            }

        }


        /* Handle Temporary Sources */


        boolean changed = false;
        for(Iterator<Map.Entry<BlockPos, TemporaryRadiationSource>> it = temporarySources.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<BlockPos, TemporaryRadiationSource> entry = it.next();
            position = entry.getKey();
            temporarySource = entry.getValue();
	
            temporarySource.ticks = temporarySource.ticks - 1;	
            if (temporarySource.ticks < 0) {
                changed = true;
                it.remove();
                if (temporarySource.leaveFading) {
                    FadingRadiationSource existing = fadingSources.getOrDefault(position, FadingRadiationSource.NONE);
                    fadingSources.put(position, new FadingRadiationSource(Math.max(temporarySource.distance, existing.distance), Math.max(temporarySource.strength, existing.strength), temporarySource.radiation + existing.radiation));
                }
            }

        }

        if (changed) {
            world.setData(VoltaicAttachmentTypes.TEMPORARY_RADIATION_SOURCES, temporarySources);
        }


        /* Handle Fading Sources */

        changed = false;

        double defaultRadiationDisipation = world.getData(VoltaicAttachmentTypes.DEFAULT_DISSIPATION);

        HashMap<BlockPosVolume, Double> localizedDissipations = world.getData(VoltaicAttachmentTypes.LOCALIZED_DISSIPATIONS);

        for(Iterator<Map.Entry<BlockPos, FadingRadiationSource>> it = fadingSources.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<BlockPos, FadingRadiationSource> entry = it.next();
            
            changed = true;

            position = entry.getKey();
            fadingSource = entry.getValue();

            boolean hit = false;

            for (Map.Entry<BlockPosVolume, Double> localized : localizedDissipations.entrySet()) {
                if (localized.getKey().isIn(position)) {
                    fadingSource.radiation = fadingSource.radiation - localized.getValue();
                    hit = true;
                    break;
                }
            }

            if (!hit) {
                fadingSource.radiation = fadingSource.radiation - defaultRadiationDisipation;
            }

            if (fadingSource.radiation <= 0) {
                it.remove();
            }

        }

        if (changed) {
            world.setData(VoltaicAttachmentTypes.FADING_RADIATION_SOURCES, fadingSources);
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
