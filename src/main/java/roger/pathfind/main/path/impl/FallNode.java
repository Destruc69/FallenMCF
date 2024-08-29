package roger.pathfind.main.path.impl;

import net.minecraft.util.math.vector.Vector3d;
import roger.pathfind.main.path.Node;
import roger.pathfind.main.path.PathElm;
import roger.util.Util;

public class FallNode extends Node implements PathElm {
    public FallNode(int x, int y, int z) {
        super(x, y, z);
    }

    @Override
    public boolean playerOn(Vector3d playerPos) {
        return Util.toBlockPos(playerPos).equals(getBlockPos());
    }
}
