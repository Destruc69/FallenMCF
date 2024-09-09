package roger.pathfind.main.walk.target.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import roger.pathfind.main.path.PathElm;
import roger.pathfind.main.path.impl.FallNode;
import roger.pathfind.main.walk.target.WalkTarget;
import roger.util.Util;

public class FallTarget extends WalkTarget {

    FallNode node;

    public FallTarget(FallNode node) {
        this.node = node;
    }

    @Override
    public boolean tick(Vector3d predictedMotionOnStop, Vector3d playerPos) {
        setCurrentTarget(node.getBlockPos());

        playerPos = new Vector3d(playerPos.x, 0, playerPos.z);
        Vector3d dest = new Vector3d(node.getX(), 0, node.getZ()).add(0.5d, 0d, 0.5d);
        double predicatedPositionDistance = playerPos.distanceTo(playerPos.add(predictedMotionOnStop));
        double destPositionDistance = playerPos.distanceTo(dest);

        // Just stop moving this tick, will refresh next tick
        KeyBinding.setKeyBindState(InputMappings.getInputByCode(Minecraft.getInstance().gameSettings.keyBindForward.getKey().getKeyCode(), 0), false);

        double angle = calculateAnglePredictionDest(predictedMotionOnStop, dest.subtract(playerPos));
        System.out.println("TEST" + " " + Util.toBlockPos(playerPos) + " " + (Util.toBlockPos(dest)));
        return (predicatedPositionDistance > destPositionDistance && angle < PREDICTED_MOTION_ANGLE) || Util.toBlockPos(playerPos).equals(Util.toBlockPos(dest));
    }

    public BlockPos getNodeBlockPos() {
        return node.getBlockPos();
    }

    public PathElm getElm() {
        return node;
    }
}
