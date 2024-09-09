package roger.pathfind.main.processor.impl;

import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import roger.pathfind.main.path.PathElm;
import roger.pathfind.main.path.impl.TravelNode;
import roger.pathfind.main.path.impl.TravelVector;
import roger.pathfind.main.processor.Processor;
import roger.util.Util;

import java.util.ArrayList;
import java.util.List;

public class JumpProcessor extends Processor {

    @Override
    public void process(List<PathElm> elms) {
        List<PathElm> newPath = new ArrayList<>();

        for (int i = 0; i < elms.size(); i++) {
            PathElm currentElm = elms.get(i);

            if (!(currentElm instanceof TravelNode)) {
                newPath.add(currentElm);
                continue;
            }

            TravelNode currentNode = (TravelNode) currentElm;

            // Check the next node to see if we can optimize the jump
            if (i + 1 < elms.size() && elms.get(i + 1) instanceof TravelNode) {
                TravelNode nextNode = (TravelNode) elms.get(i + 1);

                if (canWalkUp(currentNode, nextNode)) {
                    // If the next node is accessible via slab or stair, connect the nodes and skip the next one
                    newPath.add(new TravelVector(currentNode, nextNode));
                    i++;  // Skip the next node since we've connected them
                    continue;
                }
            }

            newPath.add(currentElm);
        }

        elms.clear();
        elms.addAll(newPath);
    }

    /**
     * Checks if the path between two nodes is walkable (e.g., can be walked up via a slab or stair).
     */
    private boolean canWalkUp(TravelNode currentNode, TravelNode nextNode) {
        // Check if the Y-level difference between the two nodes is exactly 1 (indicating a step up)
        int yDifference = nextNode.getY() - currentNode.getY();
        if (yDifference == 1) {
            BlockPos currentPos = currentNode.getBlockPos();
            BlockPos nextPos = nextNode.getBlockPos();

            // Check if the block at the current node is a slab or stair
            if (isSlabOrStair(currentPos) && !isBlockSolidAbove(currentPos)) {
                return true;
            }

            // Check if the block at the next node is a slab or stair
            return isSlabOrStair(nextPos) && !isBlockSolidAbove(nextPos);
        }

        return false;
    }

    /**
     * Checks if a block at a given position is a slab or stair.
     */
    private boolean isSlabOrStair(BlockPos pos) {
        return Minecraft.getInstance().world.getBlockState(pos).getBlock() instanceof SlabBlock || Minecraft.getInstance().world.getBlockState(pos).getBlock() instanceof StairsBlock;
    }

    /**
     * Checks if there is a solid block above the given position.
     */
    private boolean isBlockSolidAbove(BlockPos pos) {
        BlockPos abovePos = pos.add(0, 1, 0);
        return Util.isBlockSolid(abovePos);
    }
}
