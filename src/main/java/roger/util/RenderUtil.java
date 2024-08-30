package roger.util;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import paul.fallen.utils.render.RenderUtils;

import java.awt.*;
import java.util.List;

public class RenderUtil {
    public static void setColor(int color) {

    }

    public static void drawFilledEsp(BlockPos pos, Color color, RenderWorldLastEvent event) {
        RenderUtils.drawOutlinedBox(pos, 0, 1, 0, event);
    }

    public static void drawBox(AxisAlignedBB boundingBox, Color color, boolean outline, boolean box, int outlineWidth) {

    }

    private static void drawFilledBox(AxisAlignedBB axisAlignedBB) {

    }

    public static void drawSelectionBoundingBox(AxisAlignedBB boundingBox) {

    }

    public static void drawLines(List<Vector3d> poses, float thickness, float partialTicks, int color, RenderWorldLastEvent event) {
        for (int i = 0; i < poses.size() - 1; i++) {
            Vector3d a = poses.get(i);
            Vector3d b = poses.get(i + 1);

            RenderUtils.drawLine(new BlockPos(a.x + 0.5, a.y + 0.5, a.z + 0.5), new BlockPos(b.x + 0.5, b.y + 0.5, b.z + 0.5), 0, 1, 0, event);
        }
    }
}