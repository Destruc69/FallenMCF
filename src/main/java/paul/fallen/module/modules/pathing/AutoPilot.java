package paul.fallen.module.modules.pathing;

import net.minecraft.util.math.BlockPos;
import paul.fallen.module.Module;
import roger.pathfind.main.walk.Walker;

public class AutoPilot extends Module {

    public AutoPilot(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        try {
            Walker.getInstance().walk(mc.player.getPosition(), new BlockPos(0, 64, 0), 1000);
        } catch (Exception ignored) {
        }
    }
}