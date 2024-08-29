package roger.pathfind.main.path.impl;

import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import roger.pathfind.main.path.Node;
import roger.pathfind.main.path.PathElm;
import roger.util.Util;

public class TravelVector implements PathElm {

    private final Node from;
    private final Node to;

    public TravelVector(Node from, Node to) {
        this.from = from;
        this.to = to;
    }

    public Node getFrom() {
        return from;
    }

    public Node getTo() {
        return to;
    }

    @Override
    public boolean playerOn(Vector3d playerPos) {
        Vector3d fromVec = new Vector3d(new Vector3f(from.getBlockPos().getX(), from.getBlockPos().getY(), from.getBlockPos().getZ())).add(new Vector3d(0.5, 0, 0.5));
        Vector3d toVec = new Vector3d(to.getBlockPos().getX(), to.getBlockPos().getY(), to.getBlockPos().getZ()).add(new Vector3d(0.5, 0, 0.5));

        Vector3d travelVec = toVec.subtract(fromVec);
        Vector3d playerVecFrom = playerPos.subtract(fromVec);

        double playerMagnitude = playerVecFrom.distanceTo(new Vector3d(0, 0, 0));
        double destMagnitude = travelVec.distanceTo(new Vector3d(0, 0, 0));
        double angle = Util.calculateAngleVec2D(travelVec, playerVecFrom);

        // if the magnitude of the vector of the base to the player is greater than the magnitude of the vector of the base to the dest, it is clear that it has already been exceeded.
        // Then if the magnitude is less and the two vectors are more or less in the same direction we can tell the player is on the path element.

        if (playerMagnitude <= destMagnitude && angle < 20) {
            System.out.println("thingy: " + playerMagnitude + " " + destMagnitude + " " + angle + " for " + this);
            return true;
        }
        return false;
    }
}
