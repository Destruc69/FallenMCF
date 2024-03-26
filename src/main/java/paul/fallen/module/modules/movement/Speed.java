package paul.fallen.module.modules.movement;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.setting.Setting;
import paul.fallen.utils.client.MathUtils;

public final class Speed extends Module {

    private Setting ncp;

    private boolean collided;
    private int stage;
    private int stair;
    private double less;
    private boolean lessSlow;
    private double speed;

    public Speed(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        ncp = new Setting("NCP", this, false);
        addSetting(ncp);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        mc.gameSettings.keyBindSprint.setPressed(false);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            mc.gameSettings.keyBindSprint.setPressed(true);
            if (ncp.bval) {
                if (mc.player.collidedHorizontally) {
                    collided = true;
                }
                if (collided) {
                    stage = -1;
                }
                if (stair > 0)
                    stair -= 0.25;
                less -= less > 1 ? 0.12 : 0.11;
                if (less < 0)
                    less = 0;
                double s = Math.sqrt(mc.player.getMotion().x * mc.player.getMotion().x + mc.player.getMotion().z * mc.player.getMotion().z);
                if (mc.player.isOnGround() && s > 0) {
                    collided = mc.player.collidedHorizontally;
                    if (stage >= 0 || collided) {
                        stage = 0;

                        if (stair == 0) {
                            mc.player.jump();
                        } else {
                        }

                        less++;
                        if (less > 1 && !lessSlow)
                            lessSlow = true;
                        else
                            lessSlow = false;
                        if (less > 1.12)
                            less = 1.12;
                    }
                }
                speed = getHypixelSpeed(stage) + 0.0331;
                speed *= 0.91;
                if (stair > 0) {
                    speed *= 0.7;
                }

                if (stage < 0)
                    speed = 0.26;
                if (lessSlow) {
                    speed *= 0.95;
                }


                if (mc.player.isInWater()) {
                    speed = 0.12;
                }

                if (mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f) {
                    MathUtils.setSpeed(speed);
                    ++stage;
                }
            } else {
                if (mc.player.moveForward != 0) {
                    if (mc.player.isOnGround()) {
                        mc.player.jump();
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    private double getHypixelSpeed(int stage) {
        double value = 0.26 / 15;
        double firstValue = 0.4145 / 12.5;
        double decr = (((double) stage / 500) * 2);

        if (stage == 0) {
            value = 0.64 * 0.134;
        } else if (stage == 1) {
            value = firstValue;
        } else if (stage >= 2) {
            value = firstValue - decr;
        }
        if (collided) {
            value = 0.2;
            if (stage == 0)
                value = 0;
        }

        return Math.max(value, 0.26);
    }
}
