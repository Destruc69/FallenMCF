package paul.fallen.utils.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import paul.fallen.ClientSupport;

import java.util.ArrayList;

public class RenderUtils implements ClientSupport {

    public static void renderBox(AxisAlignedBB bb, float red, float green, float blue, int width, float alpha, RenderWorldLastEvent event) {
        RenderSystem.lineWidth(width);
        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        if (buffer == null) return;

        IVertexBuilder builder = buffer.getBuffer(RenderType.getLines());
        MatrixStack matrixStack = event.getMatrixStack();
        ClientPlayerEntity player = Minecraft.getInstance().player;
        double x = player.lastTickPosX + (player.getPosX() - player.lastTickPosX) * event.getPartialTicks();
        double y = player.lastTickPosY + (player.getPosY() - player.lastTickPosY) * event.getPartialTicks();
        double z = player.lastTickPosZ + (player.getPosZ() - player.lastTickPosZ) * event.getPartialTicks();

        matrixStack.push();
        matrixStack.translate(-x, -y, -z);
        RenderSystem.disableDepthTest(); // Disable depth testing to render through blocks
        WorldRenderer.drawBoundingBox(matrixStack, builder, bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, red, green, blue, alpha);
        RenderSystem.enableDepthTest(); // Re-enable depth testing after rendering
        matrixStack.pop();
        buffer.finish(RenderType.getLines()); // Finish buffer to avoid state issues
    }

    public static void drawOutlinedBox(BlockPos pos, float red, float green, float blue, RenderWorldLastEvent event) {
        drawOutlinedBox(pos, new AxisAlignedBB(0, 0, 0, 1, 1, 1), red, green, blue, event);
    }

    public static void drawOutlinedBox(BlockPos pos, AxisAlignedBB axisAlignedBB, float red, float green, float blue, RenderWorldLastEvent event) {
        final GameRenderer gameRenderer = Minecraft.getInstance().gameRenderer;
        gameRenderer.resetProjectionMatrix(event.getProjectionMatrix());

        drawBoundingBoxAtBlockPos(event.getMatrixStack(), axisAlignedBB, red, green, blue, 1.0F, pos);
    }

    public static void drawPath(ArrayList<Vector3d> vecPosArrayList, float red, float green, float blue, RenderWorldLastEvent event) {
        for (int i = 0; i < vecPosArrayList.size() - 1; i++) {
            drawLine(new BlockPos(vecPosArrayList.get(i).x + 0.5, vecPosArrayList.get(i).y, vecPosArrayList.get(i).z + 0.5),
                    new BlockPos(vecPosArrayList.get(i + 1).x + 0.5, vecPosArrayList.get(i + 1).y, vecPosArrayList.get(i + 1).z + 0.5),
                    red, green, blue, event);
        }
    }

    private static void drawBoundingBoxAtBlockPos(MatrixStack matrixStack, AxisAlignedBB aabb, float red, float green, float blue, float alpha, BlockPos pos) {
        Vector3d cam = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        double camX = cam.getX(), camY = cam.getY(), camZ = cam.getZ();

        matrixStack.push();
        RenderSystem.disableDepthTest();
        drawShapeOutline(matrixStack, VoxelShapes.create(aabb), pos.getX() - camX, pos.getY() - camY, pos.getZ() - camZ, red, green, blue, alpha);
        RenderSystem.enableDepthTest();
        matrixStack.pop();
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

    public static void drawLine(BlockPos a, BlockPos b, float red, float green, float blue, RenderWorldLastEvent event) {
        drawLineBoundingBox(event.getMatrixStack(), new AxisAlignedBB(0, 0, 0, 1, 1, 1), red, green, blue, 1.0F, a, b);
    }

    private static void drawLineBoundingBox(MatrixStack matrixStack, AxisAlignedBB aabb, float red, float green, float blue, float alpha, BlockPos posA, BlockPos posB) {
        Vector3d cam = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        double camX = cam.getX(), camY = cam.getY(), camZ = cam.getZ();

        matrixStack.push();
        RenderSystem.disableDepthTest();
        drawLineShapeOutline(matrixStack, VoxelShapes.create(aabb), posA, posB, camX, camY, camZ, red, green, blue, alpha);
        RenderSystem.enableDepthTest();
        matrixStack.pop();
    }

    private static void drawLineShapeOutline(MatrixStack matrixStack, VoxelShape voxelShape, BlockPos posA, BlockPos posB, double camX, double camY, double camZ, float red, float green, float blue, float alpha) {
        Matrix4f matrix4f = matrixStack.getLast().getMatrix();
        IRenderTypeBuffer.Impl renderTypeBuffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        IVertexBuilder bufferIn = renderTypeBuffer.getBuffer(RenderType.LINES);

        bufferIn.pos(matrix4f, (float) (posA.getX() - camX), (float) (posA.getY() - camY), (float) (posA.getZ() - camZ)).color(red, green, blue, alpha).endVertex();
        bufferIn.pos(matrix4f, (float) (posB.getX() - camX), (float) (posB.getY() - camY), (float) (posB.getZ() - camZ)).color(red, green, blue, alpha).endVertex();

        renderTypeBuffer.finish(RenderType.LINES);
    }

    public static void drawPath(ArrayList<Vector3d> vecPosArrayList, int red, int green, int blue, RenderWorldLastEvent event) {
        final GameRenderer gameRenderer = Minecraft.getInstance().gameRenderer;
        gameRenderer.resetProjectionMatrix(event.getProjectionMatrix());

        final AxisAlignedBB aab = new AxisAlignedBB(0, 1, 0, 1, 1.1, 1);

        for (Vector3d v : vecPosArrayList) {
            drawBoundingBoxAtBlockPos(event.getMatrixStack(), aab, red, green, blue, 1.0F, new BlockPos(v.x, v.y, v.z));
        }
    }

    public static void renderPath(ArrayList<Vector3d> path, RenderWorldLastEvent event) {
        if (path.size() > 0) {
            for (int i = 0; i < path.size() - 1; i++) {
                if (path.get(i + 1) != null) {
                    RenderUtils.drawLine(new BlockPos(path.get(i).x + 0.5, path.get(i).y, path.get(i).z + 0.5), new BlockPos(path.get(i + 1).x + 0.5, path.get(i + 1).y, path.get(i + 1).z + 0.5), 0, 1, 0, event);
                }
            }
        }
    }

    public static void renderPathB(ArrayList<BlockPos> path, RenderWorldLastEvent event) {
        if (path.size() > 0) {
            for (int i = 0; i < path.size() - 1; i++) {
                if (path.get(i + 1) != null) {
                    RenderUtils.drawLine(new BlockPos(path.get(i).getX() + 0.5, path.get(i).getY(), path.get(i).getZ() + 0.5), new BlockPos(path.get(i + 1).getX() + 0.5, path.get(i + 1).getY(), path.get(i + 1).getZ() + 0.5), 0, 1, 0, event);
                }
            }
        }
    }
}
