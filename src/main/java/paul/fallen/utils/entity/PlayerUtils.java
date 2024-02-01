package paul.fallen.utils.entity;

import net.minecraft.BlockUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.levelgen.structure.templatesystem.AxisAlignedLinearPosTest;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3d;
import paul.fallen.ClientSupport;
import paul.fallen.utils.client.MathUtils;
import paul.fallen.utils.world.BlockUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class PlayerUtils implements ClientSupport {

    public static boolean isMoving() {
        return PlayerUtils.getForward() != 0.0f || PlayerUtils.getStrafe() != 0.0f;
    }

    public static double getBaseMoveSpeed() {
        return 0.2873D;
    }

    public static double[] calculateMotion(double speed, double increment) {
        double[] motion = new double[2];
        double currentSpeed = 0.0;

        while (currentSpeed < speed) {
            // Increment motion gradually
            motion[0] += increment;
            motion[1] += increment;

            // Calculate current speed (hypotenuse of motion in x and z directions)
            currentSpeed = Math.sqrt(motion[0] * motion[0] + motion[1] * motion[1]);
        }

        return motion;
    }

    public static double calculateMotionY(double speed, double increment) {
        double motion = 0;
        double currentSpeed = 0.0;

        // Use a for loop to increment motion until currentSpeed reaches speed
        for (; currentSpeed < speed; currentSpeed += increment) {
            // Increment motion gradually
            motion += increment;
        }

        return motion;
    }

    public static void setMoveSpeed(double speed) {
        double forward = PlayerUtils.getForward();
        double strafe = PlayerUtils.getStrafe();
        float yaw = mc.player.rotA;
        if (forward != 0) {
            if (strafe > 0) {
                yaw += ((forward > 0) ? -45 : 45);
            } else if (strafe < 0) {
                yaw += ((forward > 0) ? 45 : -45);
            }
            strafe = 0;
            if (forward > 0) {
                forward = 1;
            } else {
                forward = -1;
            }
        }
        mc.player.setDeltaMovement(forward * speed * Math.cos(Math.toRadians((yaw + 90.0F))) + strafe * speed * Math.sin(Math.toRadians((yaw + 90.0F))), mc.player.getDeltaMovement().y, forward * speed * Math.sin(Math.toRadians((yaw + 90.0F))) - strafe * speed * Math.cos(Math.toRadians((yaw + 90.0F))));
    }

    public static double[] getMotions(double speed) {
        double forward = getForward();
        double strafe = getStrafe();
        float yaw = mc.player.rotA;
        if (forward != 0) {
            if (strafe > 0) {
                yaw += ((forward > 0) ? -45 : 45);
            } else if (strafe < 0) {
                yaw += ((forward > 0) ? 45 : -45);
            }
            strafe = 0;
            if (forward > 0) {
                forward = 1;
            } else {
                forward = -1;
            }
        }
        return new double[]{forward * speed * Math.cos(Math.toRadians((yaw + 90.0F))) + strafe * speed * Math.sin(Math.toRadians((yaw + 90.0F))), forward * speed * Math.sin(Math.toRadians((yaw + 90.0F))) - strafe * speed * Math.cos(Math.toRadians((yaw + 90.0F)))};
    }

    public static int getForward() {
        if (mc.options.keyUp.isDown()) {
            return 1;
        } else if (mc.options.keyDown.isDown()) {
            return -1;
        } else {
            return 0;
        }
    }

    public static int getStrafe() {
        if (mc.options.keyRight.isDown()) {
            return 1;
        } else if (mc.options.keyLeft.isDown()) {
            return -1;
        } else {
            return 0;
        }
    }


    public static float getDirection() {
        float yaw = mc.player.rotA;
        float forward = PlayerUtils.getForward();
        float strafe = PlayerUtils.getStrafe();
        yaw += (forward < 0.0F ? 180 : 0);
        if (strafe < 0.0F) {
            yaw += (forward == 0.0F ? 90 : forward < 0.0F ? -45 : 45);
        }
        if (strafe > 0.0F) {
            yaw -= (forward == 0.0F ? 90 : forward < 0.0F ? -45 : 45);
        }
        return yaw * 0.017453292F;
    }

    public static float getSpeed() {
        return (float) Math.sqrt(mc.player.getDeltaMovement().x * mc.player.getDeltaMovement().x + mc.player.getDeltaMovement().z * mc.player.getDeltaMovement().z);
    }

    public static void setSpeed(double speed) {
        mc.player.setDeltaMovement(-Math.sin(getDirection()) * speed, mc.player.getDeltaMovement().y, Math.cos(getDirection()) * speed);
    }

    public static void setCockSpeed(final double moveSpeed, final float pseudoYaw, final double pseudoStrafe, final double pseudoForward) {
        double forward = pseudoForward;
        double strafe = pseudoStrafe;
        float yaw = pseudoYaw;

        if (forward == 0.0 && strafe == 0.0) {
            mc.player.setDeltaMovement(0, mc.player.getDeltaMovement().y, 0);
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            Random random = new Random();
            int min = 1;
            int max = 15;
            int delay = random.nextInt(max + 1 - min) + min;

            final double cos = Math.cos(Math.toRadians(yaw + 90.0f + delay));
            final double sin = Math.sin(Math.toRadians(yaw + 90.0f + delay));

            mc.player.setDeltaMovement(forward * moveSpeed * cos + strafe * moveSpeed * sin, mc.player.getDeltaMovement().y, forward * moveSpeed * sin - strafe * moveSpeed * cos);
        }
    }
}
