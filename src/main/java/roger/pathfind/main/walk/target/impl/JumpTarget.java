package roger.pathfind.main.walk.target.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import roger.pathfind.main.path.PathElm;
import roger.pathfind.main.path.impl.JumpNode;
import roger.pathfind.main.walk.target.WalkTarget;
import roger.util.Util;

public class JumpTarget extends WalkTarget {

    JumpNode node;
    WalkTarget next;

    boolean originalYSet;
    int originalY;

    int wait = 0;

    public JumpTarget(JumpNode node, WalkTarget next) {
        this.node = node;
        this.next = next;
    }

    @Override
    public boolean tick(Vector3d predictedMotionOnStop, Vector3d playerPos) {
        if (!originalYSet) {
            originalYSet = true;
            originalY = (int) playerPos.y;
        }
        // last one
        if (next == null)
            return true;

        setCurrentTarget(next.getNodeBlockPos());

        wait++;

        // change value and stuff
        if (wait < 2)
            return false;


        KeyBinding.setKeyBindState(InputMappings.getInputByCode(Minecraft.getInstance().gameSettings.keyBindJump.getKey().getKeyCode(), 0), true);

        if ((int) playerPos.y - originalY == 1 && Util.isBlockSolid(new BlockPos(playerPos).subtract(new Vector3i(0, 1, 0)))) {
            KeyBinding.setKeyBindState(InputMappings.getInputByCode(Minecraft.getInstance().gameSettings.keyBindJump.getKey().getKeyCode(), 0), false);
            return true;
        }

        return false;
    }

    public BlockPos getNodeBlockPos() {
        return node.getBlockPos();
    }

    public PathElm getElm() {
        return node;
    }
}
