package roger.pathfind.main.walk.target.impl;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import roger.pathfind.main.path.PathElm;
import roger.pathfind.main.path.impl.LadderNode;
import roger.pathfind.main.walk.target.WalkTarget;

public class LadderTarget extends WalkTarget {

    private LadderNode ladderNode;

    public LadderTarget(LadderNode ladderNode) {
        this.ladderNode = ladderNode;
    }

    @Override
    public boolean tick(Vector3d predictedMotionOnStop, Vector3d playerPos) {
        return false;
    }

    @Override
    public BlockPos getNodeBlockPos() {
        return ladderNode.getBlockPos();
    }

    @Override
    public PathElm getElm() {
        return ladderNode;
    }
}