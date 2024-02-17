package paul.fallen.module.modules.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.setting.Setting;
import paul.fallen.utils.entity.EntityUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class LongJump extends Module {

    private final Setting mode;
    private boolean jump = false;

    public LongJump(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        mode = new Setting("Mode", "Mode", this, "oldaac", new ArrayList<>(Arrays.asList("oldaac", "ncp")));
        addSetting(mode);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (mode.sval == "oldaac") {
            this.mc.gameSettings.keyBindForward.setPressed(false);
            if (this.mc.player.isOnGround()) {
                this.jump = true;
            }
            if (this.mc.player.isOnGround() && mc.player.ticksExisted % 10 == 0) {
                EntityUtils.setMotionY(0.42);
                toFwd(2.3);
            } else if (!this.mc.player.isOnGround() && this.jump) {
                EntityUtils.setMotionZ(0);
                EntityUtils.setMotionX(0);
                this.jump = false;
            }
        } else if (mode.sval == "ncp") {
            if (movementInput() && this.mc.player.fallDistance < 1.0f) {
                float direction = this.mc.player.rotationYaw;
                float x = (float) Math.cos((double) (direction + 90.0f) * 3.141592653589793 / 180.0);
                float z = (float) Math.sin((double) (direction + 90.0f) * 3.141592653589793 / 180.0);
                if (this.mc.player.collidedVertically && this.mc.gameSettings.keyBindJump.isKeyDown()) {
                    EntityUtils.setMotionX(x * 0.29f);
                    EntityUtils.setMotionZ(z * 0.29f);
                }
                if (this.mc.player.getMotion().y == 0.33319999363422365) {
                    EntityUtils.setMotionX((double) x * 1.261);
                    EntityUtils.setMotionZ((double) z * 1.261);
                }
            }
        }
    }

    private boolean movementInput() {
        return mc.player.moveForward != 0 || mc.player.moveStrafing != 0;
    }

    private void toFwd(double speed) {
        float f = Minecraft.getInstance().player.rotationYaw * 0.017453292F;
        EntityUtils.setMotionX(mc.player.getMotion().x - (double) MathHelper.sin(f) * speed);
        EntityUtils.setMotionZ(mc.player.getMotion().z + (double) MathHelper.cos(f) * speed);
    }
}
