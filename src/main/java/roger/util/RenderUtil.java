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

    public static void drawLines(List<Vector3d> poses, float thickness, float partialTicks, int color) {

    }
}