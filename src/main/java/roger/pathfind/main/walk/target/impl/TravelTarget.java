package roger.pathfind.main.walk.target.impl;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import roger.pathfind.main.path.PathElm;
import roger.pathfind.main.path.impl.TravelNode;
import roger.pathfind.main.walk.target.WalkTarget;
import roger.util.Util;

public class TravelTarget extends WalkTarget {

    TravelNode node;

    public TravelTarget(TravelNode node) {
        this.node = node;
    }

    @Override
    public boolean tick(Vector3d predictedMotionOnStop, Vector3d playerPos) {
        setCurrentTarget(node.getBlockPos());

        Vector3d dest = new Vector3d(node.getBlockPos().getX(), node.getBlockPos().getY(), node.getBlockPos().getZ()).add(0.5d, 0d, 0.5d);
        double predicatedPositionDistance = playerPos.distanceTo(playerPos.add(predictedMotionOnStop));
        double destPositionDistance = playerPos.distanceTo(dest);

        double angle = calculateAnglePredictionDest(predictedMotionOnStop, dest.subtract(playerPos));

        return (predicatedPositionDistance > destPositionDistance && angle < PREDICTED_MOTION_ANGLE) || Util.getPlayerBlockPos().equals(Util.toBlockPos(dest));
    }

    public BlockPos getNodeBlockPos() {
        return node.getBlockPos();
    }

    public PathElm getElm() {
        return node;
    }
}
