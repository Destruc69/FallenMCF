package paul.fallen.module.modules.pathing;

import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.pathfinder.AStarCustomPathFinder;
import paul.fallen.setting.Setting;
import paul.fallen.utils.entity.RotationUtils;
import paul.fallen.utils.render.RenderUtils;

public class AutoPilot extends Module {

    private int yaw = 0;

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
                if (mc.player.getDistanceSq(aStarCustomPathFinder.getPath().get(aStarCustomPathFinder.getPath().size() - 1)) < 1) {
                    aStarCustomPathFinder = new AStarCustomPathFinder(mc.player.getPositionVec(), new Vector3d(x.dval, y.dval, z.dval));
                    aStarCustomPathFinder.compute();
                } else {
                    Vector3d toLook = aStarCustomPathFinder.getTargetPositionInPathArray(aStarCustomPathFinder.getPath());
                    int[] l = RotationUtils.getYawAndPitch(toLook);

                    mc.player.rotationYaw = l[0];

                    if (aStarCustomPathFinder.getTargetPositionInPathArray(aStarCustomPathFinder.getPath()).y > mc.player.getPosY()) {
                        if (mc.player.isOnGround()) {
                            mc.player.jump();
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        try {
            if (aStarCustomPathFinder.getPath().size() > 0) {
                for (int i = 0; i < aStarCustomPathFinder.getPath().size() - 1; i++) {
                    if (aStarCustomPathFinder.getPath().get(i + 1) != null) {
                        RenderUtils.drawLine(new BlockPos(aStarCustomPathFinder.getPath().get(i).x, aStarCustomPathFinder.getPath().get(i).y, aStarCustomPathFinder.getPath().get(i).z), new BlockPos(aStarCustomPathFinder.getPath().get(i + 1).x, aStarCustomPathFinder.getPath().get(i + 1).y, aStarCustomPathFinder.getPath().get(i + 1).z), 0, 1, 0, event);
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }
}
