/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.utils.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import org.joml.Vector3d;
import paul.fallen.utils.entity.PlayerUtils;

public final class MathUtils {
    private static float forward;
    private static float side;

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;

        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;

        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static int floor(float value) {
        int i = (int) value;
        return value < i ? i - 1 : i;
    }

    public static int floor(double value) {
        int i = (int) value;
        return value < i ? i - 1 : i;
    }

    public static int clamp(int num, int min, int max) {
        return num < min ? min : num > max ? max : num;
    }

    public static float clamp(float num, float min, float max) {
        return num < min ? min : num > max ? max : num;
    }

    public static double clamp(double num, double min, double max) {
        return num < min ? min : num > max ? max : num;
    }

    public static double[] directionSpeed(double speed) {
        try {
            final Minecraft mc = Minecraft.getInstance();
            if (Minecraft.getInstance().options.keyUp.isDown()) {
                forward = 1;
            } else if (Minecraft.getInstance().options.keyDown.isDown()) {
                forward = -1;
            } else {
                forward = 0;
            }
            if (Minecraft.getInstance().options.keyLeft.isDown()) {
                side = 1;
            } else if (Minecraft.getInstance().options.keyRight.isDown()) {
                side = -1;
            } else {
                side = 0;
            }
            float yaw = mc.player.rotA;

            if (forward != 0) {
                if (side > 0) {
                    yaw += (forward > 0 ? -45 : 45);
                } else if (side < 0) {
                    yaw += (forward > 0 ? 45 : -45);
                }
                side = 0;

                // forward = clamp(forward, 0, 1);
                if (forward > 0) {
                    forward = 1;
                } else if (forward < 0) {
                    forward = -1;
                }
            }

            final double sin = Math.sin(Math.toRadians(yaw + 90));
            final double cos = Math.cos(Math.toRadians(yaw + 90));
            final double posX = (forward * speed * cos + side * speed * sin);
            final double posZ = (forward * speed * sin - side * speed * cos);
            return new double[]
                    {posX, posZ};
        } catch (Exception ignored) {
        }
        return new double[0];
    }

    public static void setSpeed(final double speed) {
        if (Minecraft.getInstance().options.keyUp.isDown() ||
                Minecraft.getInstance().options.keyRight.isDown() ||
                Minecraft.getInstance().options.keyDown.isDown() ||
                Minecraft.getInstance().options.keyLeft.isDown()) {
            assert Minecraft.getInstance().player != null;
            Minecraft.getInstance().player.setDeltaMovement(-Math.sin(getDirection()) * speed, Minecraft.getInstance().player.getDeltaMovement().y, Math.cos(getDirection()) * speed);
        }
    }

    public static float getDirection() {
        assert Minecraft.getInstance().player != null;
        float yaw = Minecraft.getInstance().player.rotA;
        final float forward = PlayerUtils.getForward();
        final float strafe = PlayerUtils.getStrafe();
        yaw += ((forward < 0.0f) ? 180 : 0);
        int i = (forward < 0.0f) ? -45 : ((forward == 0.0f) ? 90 : 45);
        if (strafe < 0.0f) {
            yaw += i;
        }
        if (strafe > 0.0f) {
            yaw -= i;
        }
        return yaw * 0.017453292f;
    }

    public static double calculateFallDistance(int x, int y, int z) {
        int dist = 0;

        while (true) {
            assert Minecraft.getInstance().level != null;
            if (!Minecraft.getInstance().level.getBlockState(new BlockPos(x, y - dist, z)).getBlock().equals(Blocks.AIR))
                break;
            dist += 0.01;
        }

        return dist;
    }

    public static double getDistance(Vector3d vec1, Vector3d vec2) {
        double dx = vec2.x - vec1.x;
        double dy = vec2.y - vec1.y;
        double dz = vec2.z - vec1.z;

        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public static double lerp(double start, double end, double t) {
        if (t < 0) {
            return start;
        }
        if (t > 1) {
            return end;
        }
        return start + t * (end - start);
    }
}
