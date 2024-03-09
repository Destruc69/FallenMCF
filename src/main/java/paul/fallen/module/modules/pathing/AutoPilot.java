package paul.fallen.module.modules.pathing;

import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.pathfinder.AStarCustomPathFinder;
import paul.fallen.setting.Setting;

public class AutoPilot extends Module {

    private AStarCustomPathFinder aStarCustomPathFinder;

    Setting x;
    Setting y;
    Setting z;

    public AutoPilot(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        x = new Setting("X", this, 0, -32000000, 32000000);
        y = new Setting("Y", this, 64, 0, 255);
        z = new Setting("Z", this, 0, -32000000, 32000000);

        addSetting(x);
        addSetting(y);
        addSetting(z);
    }

    private boolean initStart = true;

    @Override
    public void onEnable() {
        try {
            super.onEnable();

            aStarCustomPathFinder.getPath().clear();
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            if (initStart) {
                aStarCustomPathFinder = new AStarCustomPathFinder(mc.player.getPositionVec(), new Vector3d(x.dval, y.dval, z.dval));
                aStarCustomPathFinder.compute();
                initStart = false;
            }

            if (aStarCustomPathFinder.getPath().size() <= 0) {
                aStarCustomPathFinder = new AStarCustomPathFinder(mc.player.getPositionVec(), new Vector3d(x.dval, y.dval, z.dval));
                aStarCustomPathFinder.compute();
            }

            if (aStarCustomPathFinder.getPath().size() > 0) {
                if (aStarCustomPathFinder.hasReachedEndOfPath()) {
                    aStarCustomPathFinder = new AStarCustomPathFinder(mc.player.getPositionVec(), new Vector3d(x.dval, y.dval, z.dval));
                    aStarCustomPathFinder.compute();
                } else {
                    aStarCustomPathFinder.move();
                }
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        try {
            aStarCustomPathFinder.renderPath(event);
        } catch (Exception ignored) {
        }
    }
}