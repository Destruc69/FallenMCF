package roger.pathfind.main.walk.target.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import roger.pathfind.main.path.PathElm;
import roger.pathfind.main.path.impl.PlaceNode;
import roger.pathfind.main.walk.target.WalkTarget;

public class PlaceTarget extends WalkTarget {

    PlaceNode node;

    public PlaceTarget(PlaceNode node) {
        this.node = node;
    }

    @Override
    public boolean tick(Vector3d predictedMotionOnStop, Vector3d playerPos) {
        setCurrentTarget(node.getBlockPos());

        return Minecraft.getInstance().player.getDistanceSq(node.getX() + 0.5, node.getY() + 0.5, node.getZ() + 0.5) <= 0.8;
    }

    public BlockPos getNodeBlockPos() {
        return node.getBlockPos();
    }

    public PathElm getElm() {
        return node;
    }
}
