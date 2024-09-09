package roger.pathfind.main.processor.impl;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import roger.pathfind.main.path.PathElm;
import roger.pathfind.main.path.impl.TravelNode;
import roger.pathfind.main.path.impl.TravelVector;
import roger.pathfind.main.processor.Processor;
import roger.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TravelProcessor extends Processor {

    private final Map<BlockPos, Boolean> blockSolidityCache = new HashMap<>();
    private final Map<BlockPos, BlockPos[]> surroundingsCache = new HashMap<>();

    @Override
    public void process(List<PathElm> elms) {
        List<PathElm> newPath = new ArrayList<>();

        PathIter:
        for (int a = 0; a < elms.size(); a++) {
            PathElm elm = elms.get(a);

            if (!(elm instanceof TravelNode)) {
                newPath.add(elm);
                continue;
            }

            TravelNode start = (TravelNode)elms.get(a);

            for(int b = elms.size() - 1 ; b > a ; b--) {
                if(!(elms.get(b) instanceof TravelNode)) {
                    continue;
                }

                TravelNode end = (TravelNode)elms.get(b);

                if(shouldOptimise(start, end)) {
                    a = b;
                    newPath.add(new TravelVector(start, end));
                    continue PathIter;
                }
            }

            newPath.add(elm);
        }

        elms.clear();
        elms.addAll(newPath);
    }

    public boolean shouldOptimise(TravelNode start, TravelNode end) {
        if (start.getY() != end.getY()) {
            return false;
        }

        Vector3d startVec = new Vector3d(start.getBlockPos().getX(), start.getBlockPos().getY(), start.getBlockPos().getZ());
        Vector3d endVec = new Vector3d(end.getBlockPos().getX(), end.getBlockPos().getY(), end.getBlockPos().getZ());

        Vector3d differenceVector = endVec.subtract(startVec);
        Vector3d normalDelta = differenceVector.normalize();

        List<BlockPos> blocksWithinVector = new ArrayList<>();

        for (int scale = 0; scale < endVec.distanceTo(startVec); scale++) {
            Vector3d blockVec = startVec.add(Util.vecMultiply(normalDelta, scale));
            BlockPos blockPos = Util.toBlockPos(blockVec);

            if (!blocksWithinVector.contains(blockPos))
                blocksWithinVector.add(blockPos);
        }
        if (!blocksWithinVector.contains(Util.toBlockPos(endVec)))
            blocksWithinVector.add(Util.toBlockPos(endVec));

        blocksWithinVector.remove(Util.toBlockPos(startVec));

        for (BlockPos block : blocksWithinVector) {
            if (!isBlockSurroundingsOptimizable(block)) {
                return false;
            }

            if (!isBlockSolidCached(block.subtract(new Vector3i(0, 1, 0)))) {
                return false;
            }
        }

        return true;
    }

    private boolean isBlockSolidCached(BlockPos pos) {
        return blockSolidityCache.computeIfAbsent(pos, Util::isBlockSolid);
    }

    private BlockPos[] getSurroundings(BlockPos block) {
        return surroundingsCache.computeIfAbsent(block, this::calculateSurroundings);
    }

    private BlockPos[] calculateSurroundings(BlockPos block) {
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        return new BlockPos[]{
                new BlockPos(x + 1, y, z + 1),
                new BlockPos(x, y, z + 1),
                new BlockPos(x - 1, y, z + 1),

                new BlockPos(x + 1, y, z),
                new BlockPos(x, y, z),
                new BlockPos(x - 1, y, z),

                new BlockPos(x + 1, y, z - 1),
                new BlockPos(x, y, z - 1),
                new BlockPos(x - 1, y, z - 1),

                new BlockPos(x + 1, y + 1, z + 1),
                new BlockPos(x, y + 1, z + 1),
                new BlockPos(x - 1, y + 1, z + 1),

                new BlockPos(x + 1, y + 1, z),
                new BlockPos(x, y + 1, z),
                new BlockPos(x - 1, y + 1, z),

                new BlockPos(x + 1, y + 1, z - 1),
                new BlockPos(x, y + 1, z - 1),
                new BlockPos(x - 1, y + 1, z - 1),
        };
    }

    private boolean isBlockSurroundingsOptimizable(BlockPos block) {
        BlockPos[] surroundings = getSurroundings(block);

        for (BlockPos surroundingBlock : surroundings) {
            if (isBlockSolidCached(surroundingBlock)) {
                return false;
            }
        }

        return true;
    }
}
