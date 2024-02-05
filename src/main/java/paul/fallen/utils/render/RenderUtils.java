package paul.fallen.utils.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;
import paul.fallen.ClientSupport;

import java.util.ArrayList;

public class RenderUtils implements ClientSupport {

    public static void drawOutlinedBox(BlockPos pos, int red, int green, int blue, RenderWorldLastEvent event) {
        final GameRenderer gameRenderer = Minecraft.getInstance().gameRenderer;
        gameRenderer.resetProjectionMatrix(event.getProjectionMatrix());

        final AxisAlignedBB aab = new AxisAlignedBB(0, 0, 0, 1, 1, 1);
        drawBoundingBoxAtBlockPos(event.getMatrixStack(), aab, red, green, blue, 1.0F, pos);
    }

    public static void drawPath(ArrayList<Vector3d> vecPosArrayList, int red, int green, int blue, RenderWorldLastEvent event) {
        final GameRenderer gameRenderer = Minecraft.getInstance().gameRenderer;
        gameRenderer.resetProjectionMatrix(event.getProjectionMatrix());

        final AxisAlignedBB aab = new AxisAlignedBB(0, 1, 0, 1, 1.1, 1);

        for (Vector3d v : vecPosArrayList) {
            drawBoundingBoxAtBlockPos(event.getMatrixStack(), aab, red, green, blue, 1.0F, new BlockPos(v.x, v.y, v.z));
        }
    }

    private static void drawBoundingBoxAtBlockPos(MatrixStack matrixStackIn, AxisAlignedBB aabbIn, float red, float green, float blue, float alpha, BlockPos pos) {
        Vector3d cam = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();

        double camX = cam.getX(), camY = cam.getY(), camZ = cam.getZ();

        matrixStackIn.push();

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        drawShapeOutline(matrixStackIn, VoxelShapes.create(aabbIn), pos.getX() - camX, pos.getY() - camY, pos.getZ() - camZ, red, green, blue, alpha);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        matrixStackIn.pop();
    }

    private static void drawShapeOutline(MatrixStack matrixStack, VoxelShape voxelShape, double originX, double originY, double originZ, float red, float green, float blue, float alpha) {
        Matrix4f matrix4f = matrixStack.getLast().getMatrix();

        IRenderTypeBuffer.Impl renderTypeBuffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        IVertexBuilder bufferIn = renderTypeBuffer.getBuffer(RenderType.LINES);

        voxelShape.forEachEdge((x0, y0, z0, x1, y1, z1) -> {
            bufferIn.pos(matrix4f, (float) (x0 + originX), (float) (y0 + originY), (float) (z0 + originZ)).color(red, green, blue, alpha).endVertex();
            bufferIn.pos(matrix4f, (float) (x1 + originX), (float) (y1 + originY), (float) (z1 + originZ)).color(red, green, blue, alpha).endVertex();
        });

        renderTypeBuffer.finish(RenderType.LINES);
    }





    public static void drawLine(BlockPos a, BlockPos b, int red, int green, int blue, RenderWorldLastEvent event) {
        final GameRenderer gameRenderer = Minecraft.getInstance().gameRenderer;
        gameRenderer.resetProjectionMatrix(event.getProjectionMatrix());

        final AxisAlignedBB lineAABB = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

        drawLineBoundingBox(event.getMatrixStack(), lineAABB, red, green, blue, 1.0F, a, b);
    }

    private static void drawLineBoundingBox(MatrixStack matrixStackIn, AxisAlignedBB aabbIn, float red, float green, float blue, float alpha, BlockPos posA, BlockPos posB) {
        Vector3d cam = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();

        double camX = cam.getX(), camY = cam.getY(), camZ = cam.getZ();

        matrixStackIn.push();

        GL11.glDisable(GL11.GL_DEPTH_TEST);

        drawLineShapeOutline(matrixStackIn, VoxelShapes.create(aabbIn), posA, posB, camX, camY, camZ, red, green, blue, alpha);

        GL11.glEnable(GL11.GL_DEPTH_TEST);

        matrixStackIn.pop();
    }

    private static void drawLineShapeOutline(MatrixStack matrixStack, VoxelShape voxelShape, BlockPos posA, BlockPos posB, double camX, double camY, double camZ, float red, float green, float blue, float alpha) {
        Matrix4f matrix4f = matrixStack.getLast().getMatrix();

        IRenderTypeBuffer.Impl renderTypeBuffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        IVertexBuilder bufferIn = renderTypeBuffer.getBuffer(RenderType.LINES);

        bufferIn.pos(matrix4f, (float) (posA.getX() - camX), (float) (posA.getY() - camY), (float) (posA.getZ() - camZ)).color(red, green, blue, alpha).endVertex();
        bufferIn.pos(matrix4f, (float) (posB.getX() - camX), (float) (posB.getY() - camY), (float) (posB.getZ() - camZ)).color(red, green, blue, alpha).endVertex();

        renderTypeBuffer.finish(RenderType.LINES);
    }
}