package voltaic.api.radiation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import voltaic.api.radiation.util.IRadiationManager;
import voltaic.api.radiation.util.IRadiationRecipient;
import voltaic.api.radiation.util.RadiationShielding;
import voltaic.common.reloadlistener.RadiationShieldingRegister;
import voltaic.registers.VoltaicAttachmentTypes;
import voltaic.registers.VoltaicCapabilities;

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
            if (source.shouldCombine()) {

                TemporaryRadiationSource existing = sources.getOrDefault(source.getSourceLocation(), TemporaryRadiationSource.NONE);
                TemporaryRadiationSource combined = new TemporaryRadiationSource(source.ticks() + existing.ticks, Math.max(source.getRadiationStrength(), existing.strength), source.getRadiationAmount() + existing.radiation, existing.leaveFading || source.shouldLeaveLingeringSource(), Math.max(source.distance(), existing.distance), source.getSourceLocation());
                sources.put(source.getSourceLocation(), combined);
            } else {
                sources.put(source.getSourceLocation(), new TemporaryRadiationSource(source.ticks(), source.strength(), source.amount(), source.shouldLeaveLingeringSource(), source.distance(), source.getSourceLocation()));
            }
            world.setData(VoltaicAttachmentTypes.TEMPORARY_RADIATION_SOURCES, sources);
        } else {
            HashMap<BlockPos, SimpleRadiationSource> sources = world.getData(VoltaicAttachmentTypes.PERMANENT_RADIATION_SOURCES);

            if (source.shouldCombine()) {
                SimpleRadiationSource existing = sources.getOrDefault(source.getSourceLocation(), SimpleRadiationSource.NONE);
                sources.put(source.getSourceLocation(), new SimpleRadiationSource(existing.getRadiationAmount() + source.getRadiationAmount(), Math.max(existing.getRadiationStrength(), source.getRadiationStrength()), Math.max(existing.getDistanceSpread(), source.getDistanceSpread()), false, existing.getPersistanceTicks() + source.getPersistanceTicks(), source.getSourceLocation(), existing.shouldLinger() || source.shouldLinger(), existing.shouldCombine() || source.shouldCombine()));
            } else {
                sources.put(source.getSourceLocation(), source);
            }
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
    public void setLocalizedDisipation(double disipation, AABB area, Level world) {
        HashMap<AABB, Double> values = world.getData(VoltaicAttachmentTypes.LOCALIZED_DISSIPATIONS);
        values.put(area, disipation + values.getOrDefault(area, 0.0));
        world.setData(VoltaicAttachmentTypes.LOCALIZED_DISSIPATIONS, values);
    }

    @Override
    public void removeLocalizedDisipation(AABB area, Level world) {
        HashMap<AABB, Double> values = world.getData(VoltaicAttachmentTypes.LOCALIZED_DISSIPATIONS);
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
            fadingSources.put(pos, new FadingRadiationSource(source.getDistanceSpread(), source.getRadiationStrength(), source.getRadiationAmount(), pos));
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

        Iterator<Entity> entities = ((ServerLevel) world).getAllEntities().iterator();
        Entity entity;

        BlockPos position;
        IRadiationRecipient capability;

        HashMap<BlockPos, SimpleRadiationSource> permanentSources = world.getData(VoltaicAttachmentTypes.PERMANENT_RADIATION_SOURCES);
        SimpleRadiationSource permanentSource;

        HashMap<BlockPos, FadingRadiationSource> fadingSources = world.getData(VoltaicAttachmentTypes.FADING_RADIATION_SOURCES);
        FadingRadiationSource fadingSource;

        HashMap<BlockPos, TemporaryRadiationSource> temporarySources = world.getData(VoltaicAttachmentTypes.TEMPORARY_RADIATION_SOURCES);
        TemporaryRadiationSource temporarySource;

        while(entities.hasNext()) {

            entity = entities.next();

            if(!(entity instanceof LivingEntity)) {
                continue;
            }

            LivingEntity living = (LivingEntity) entity;

            if(!living.isAlive()) {
                continue;
            }

            capability = living.getCapability(VoltaicCapabilities.CAPABILITY_RADIATIONRECIPIENT);

            if (capability == null) {
                continue;
            }

            /* Permanent Sources */

            for(Map.Entry<BlockPos, SimpleRadiationSource> entryPerm : permanentSources.entrySet()) {

                position = entryPerm.getKey();
                permanentSource = entryPerm.getValue();

                if(!living.getBoundingBox().intersects(permanentSource.getBoundingBox())) {
                    continue;
                }

		int splitTargets = (int) Math.ceil(living.getBbHeight());
	
                for (int i = 0; i < splitTargets; i++) {
                    capability.recieveRadiation(living, getAppliedRadiation(world, position, living.getOnPos().above(i + 1), permanentSource.getRadiationAmount() / splitTargets, permanentSource.getRadiationStrength()), permanentSource.getRadiationStrength());
                }

            }
            
            /* Temporary Sources */
            
            for(Map.Entry<BlockPos, TemporaryRadiationSource> entryTemp : temporarySources.entrySet()) {

                position = entryTemp.getKey();
                temporarySource = entryTemp.getValue();
                
                if(!living.getBoundingBox().intersects(temporarySource.boundingBox)) {
                    continue;
                }

		int splitTargets = (int) Math.ceil(living.getBbHeight());
		
		for (int i = 0; i < splitTargets; i++) {
		    capability
			    .recieveRadiation(living,
				    getAppliedRadiation(world, position, living.getOnPos().above(i + 1),
					    temporarySource.radiation /splitTargets, temporarySource.strength),
				    temporarySource.strength);
		}
                
                
            }

            /* Fading Sources */

            for(Map.Entry<BlockPos, FadingRadiationSource> entryFading : fadingSources.entrySet()) {

                position = entryFading.getKey();
                fadingSource = entryFading.getValue();

                if(!living.getBoundingBox().intersects(fadingSource.boundingBox)) {
                    continue;
                }

		int splitTargets = (int) Math.ceil(living.getBbHeight());
	
                for (int i = 0; i < (int) Math.ceil(living.getBbHeight()); i++) {
                    capability.recieveRadiation(living, getAppliedRadiation(world, position, living.getOnPos().above(i + 1), fadingSource.radiation / splitTargets, fadingSource.strength), fadingSource.strength);
                }

            }


        }

        /* Tick Temporary Radiation Sources */


        Iterator<Map.Entry<BlockPos, TemporaryRadiationSource>> iteratorTemp = temporarySources.entrySet().iterator();
        Map.Entry<BlockPos, TemporaryRadiationSource> entryTemp;

        while (iteratorTemp.hasNext()) {
            entryTemp = iteratorTemp.next();
            position = entryTemp.getKey();
            temporarySource = entryTemp.getValue();

            temporarySource.ticks = temporarySource.ticks - 1;

            if (temporarySource.ticks < 0) {
                iteratorTemp.remove();
                if (temporarySource.leaveFading) {
                    FadingRadiationSource existing = fadingSources.getOrDefault(position, FadingRadiationSource.NONE);
                    fadingSources.put(position, new FadingRadiationSource(Math.max(temporarySource.distance, existing.distance), Math.max(temporarySource.strength, existing.strength), Math.max(temporarySource.radiation, existing.radiation), position));
                }
            }


        }

        world.setData(VoltaicAttachmentTypes.TEMPORARY_RADIATION_SOURCES, temporarySources);

        /* Fading Radiation Sources */

        Iterator<Map.Entry<BlockPos, FadingRadiationSource>> iteratorFading = fadingSources.entrySet().iterator();
        Map.Entry<BlockPos, FadingRadiationSource> entryFading;

        double defaultRadiationDisipation = world.getData(VoltaicAttachmentTypes.DEFAULT_DISSIPATION);
        HashMap<AABB, Double> localizedDissipations = world.getData(VoltaicAttachmentTypes.LOCALIZED_DISSIPATIONS);

        while (iteratorFading.hasNext()) {

            entryFading = iteratorFading.next();
            fadingSource = entryFading.getValue();


            for (Map.Entry<AABB, Double> localized : localizedDissipations.entrySet()) {
                if (localized.getKey().intersects(fadingSource.boundingBox)) {
                    fadingSource.radiation = fadingSource.radiation - localized.getValue();
                }
            }

            fadingSource.radiation = fadingSource.radiation - defaultRadiationDisipation;

            if (fadingSource.radiation <= 0) {
                iteratorFading.remove();
            }

        }

        world.setData(VoltaicAttachmentTypes.FADING_RADIATION_SOURCES, fadingSources);

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
