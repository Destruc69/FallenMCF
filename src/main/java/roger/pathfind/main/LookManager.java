package roger.pathfind.main;

import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class LookManager {

    private static LookManager instance;

    private boolean active;
    double yawTarget;
    double pitchTarget;

    private long lastUpdate;


    public LookManager() {
        instance = this;
    }

    public void setTarget(double yaw, double pitch) {
        this.yawTarget = yaw;
        this.pitchTarget = pitch;
        this.active = true;
        this.lastUpdate = System.currentTimeMillis();
    }

    public void cancel() {
        this.active = false;
    }
    @SubscribeEvent
    public void onRenderTickEnd(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START || Minecraft.getInstance().player == null || !active)
            return;


        double yaw = Minecraft.getInstance().player.rotationYaw;
        double pitch = Minecraft.getInstance().player.rotationPitch;

        if (Math.abs(yawTarget - yaw) < 1) {
            Minecraft.getInstance().player.rotationYaw = (float) yawTarget;
            Minecraft.getInstance().player.rotationPitch = (float) pitchTarget;

            active = false;
        }


        long msElapsed = System.currentTimeMillis() - lastUpdate;

        double diff = (double) msElapsed / 200;


        Minecraft.getInstance().player.rotationYaw += (yawTarget - yaw) * diff;
        Minecraft.getInstance().player.rotationPitch += (pitchTarget - pitch) * diff;


        this.lastUpdate = System.currentTimeMillis();
    }

    public static LookManager getInstance() {
        return instance;
    }
}
