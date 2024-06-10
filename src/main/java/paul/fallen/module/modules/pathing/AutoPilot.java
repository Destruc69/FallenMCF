package paul.fallen.module.modules.pathing;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;
import paul.fallen.pathfinding.LocomotionPathfinder;

public class AutoPilot extends Module {

    private LocomotionPathfinder aStarCustomPathFinder;

    Setting x;
    Setting y;
    Setting z;

    public AutoPilot(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        x = new Setting("X", this, 0, -32000000, 32000000, true);
        y = new Setting("Y", this, 64, 0, 255, true);
        z = new Setting("Z", this, 0, -32000000, 32000000, true);

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
                aStarCustomPathFinder = new LocomotionPathfinder(mc.player.getPosition(), new BlockPos(x.getValDouble(), y.getValDouble(), z.getValDouble()));
                aStarCustomPathFinder.compute();
                initStart = false;
            }

            if (aStarCustomPathFinder.getPath().size() <= 0) {
                aStarCustomPathFinder = new LocomotionPathfinder(mc.player.getPosition(), new BlockPos(x.getValDouble(), y.getValDouble(), z.getValDouble()));
                aStarCustomPathFinder.compute();
            }

            if (aStarCustomPathFinder.getPath().size() > 0) {
                aStarCustomPathFinder.dynamicRefresh();
                aStarCustomPathFinder.move();
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