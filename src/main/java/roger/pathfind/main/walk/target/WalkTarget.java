package roger.pathfind.main.walk.target;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import roger.pathfind.main.path.PathElm;
import roger.util.Util;

public abstract class WalkTarget {

    protected final int PREDICTED_MOTION_ANGLE = 20;
    private BlockPos currentTarget;

    public abstract boolean tick(Vector3d predictedMotionOnStop, Vector3d playerPos);

    public BlockPos getCurrentTarget() {
        return currentTarget;
    }
    protected void setCurrentTarget(BlockPos target) {
        this.currentTarget = target;
    }

    protected double calculateAnglePredictionDest(Vector3d predictedVec, Vector3d destVec) {
        return Util.calculateAngleVec2D(predictedVec, destVec);
    }

    public abstract BlockPos getNodeBlockPos();

    public abstract PathElm getElm();
}


