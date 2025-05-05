package voltaic.api.network.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import voltaic.prefab.network.AbstractNetwork;
import voltaic.prefab.tile.types.GenericRefreshingConnectTile;

public class AbstractNetworkFinder<C extends GenericRefreshingConnectTile<T, C, ?>, T, P> {
	private final World level;
	private final BlockPos start;
	private final AbstractNetwork<C, T, P, ?> net;
	private final HashSet<C> iteratedTiles = new HashSet<>();
	private final HashSet<BlockPos> toIgnore = new HashSet<>();

	public AbstractNetworkFinder(World world, BlockPos start, AbstractNetwork<C, T, P, ?> net, BlockPos... ignore) {
		level = world;
		this.start = start;
		this.net = net;
		if (ignore.length > 0) {
			toIgnore.addAll(Arrays.asList(ignore));
		}
	}

	private void loopAll(BlockPos location) {
		TileEntity curr = level.getBlockEntity(location);
		if (net.isConductor(curr, (C) curr)) {
			iteratedTiles.add((C) curr);
		}
		for (Direction direction : Direction.values()) {
			BlockPos relative = new BlockPos(location).relative(direction);
			if(toIgnore.contains(relative) || !level.hasChunkAt(relative)) {
				continue;
			}
			TileEntity tileEntity = level.getBlockEntity(relative);
			if(iteratedTiles.contains(tileEntity) || !net.isConductor(tileEntity, (C) curr)) {
				continue;
			}
			loopAll((C) tileEntity);
		}

	}

	private void loopAll(C cable) {
		iteratedTiles.add(cable);
		for (TileEntity connection : cable.getConnectedCables()) {
			if(connection == null) {
				continue;
			}
			BlockPos pos = connection.getBlockPos();
			if (iteratedTiles.contains(connection) || toIgnore.contains(pos) || !net.isConductor(connection, cable)) {
				continue;
			}
			loopAll((C) connection);
		}
	}

	public HashSet<C> exploreNetwork() {
		loopAll(start);
		return iteratedTiles;
	}
}
