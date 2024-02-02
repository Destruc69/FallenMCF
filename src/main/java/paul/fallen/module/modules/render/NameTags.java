package paul.fallen.module.modules.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import paul.fallen.module.Module;
import paul.fallen.utils.render.UIUtils;

import java.util.Formatter;

public final class NameTags extends Module {

    public NameTags(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        try {
            assert mc.world != null;
            for (Entity entity : mc.world.getAllEntities()) {
                if (entity instanceof PlayerEntity) {
                    PlayerEntity player = (PlayerEntity) entity;
                    float partialTicks = event.getPartialTicks();
                    assert mc.player != null;
                    double x = player.lastTickPosX + (player.getPosX() - player.lastTickPosX) * partialTicks - mc.player.getPosX();
                    assert mc.player != null;
                    double y = player.lastTickPosY + (player.getPosY() - player.lastTickPosY) * partialTicks - mc.player.getPosYEye();
                    double z = player.lastTickPosZ + (player.getPosZ() - player.lastTickPosZ) * partialTicks - mc.player.getPosZ();
                    drawNametags(player, x, y, z);
                }
            }
        } catch (Exception ignored) {
        }
    }

    public void drawNametags(PlayerEntity entity, double x, double y, double z) {

        String entityName = entity.getDisplayName().getUnformattedComponentText();
        if (entity == mc.player)
            return;

        double health = entity.getHealth() / 2;
        double maxHealth = entity.getMaxHealth() / 2;
        double percentage = 100 * (health / maxHealth);
        String healthColor;

        if (percentage > 75) healthColor = "a";
        else if (percentage > 50) healthColor = "e";
        else if (percentage > 25) healthColor = "4";
        else healthColor = "4";

        Formatter formatter = new Formatter();
        String healthDisplay = String.valueOf(formatter.format(String.valueOf(Math.floor((health + (double) 0.5F / 2) / 0.5F) * 0.5F)));

        entityName = String.format("  %s \247%s%s ", entityName, healthColor, healthDisplay);

        assert mc.player != null;
        float distance = mc.player.getDistance(entity);
        float var13 = (distance / 5 <= 2 ? 2.0F : distance / 5) * 0.7F;
        float var14 = 0.016666668F * var13;
        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.translated(x + 0.0F, y + entity.getEyeHeight() + 0.4F, z);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        //if (mc.gameSettings.thirdPersonView == 2) {
        GlStateManager.rotatef((float) -mc.player.getPosYEye(), 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef((float) mc.player.getPosX(), -1.0F, 0.0F, 0.0F);
        //} else {
        //	GlStateManager.rotatef(-(float) -mc.player.getPosYEye(), 0.0F, 1.0F, 0.0F);
        //	GlStateManager.rotatef((float) mc.player.getPosX(), 1.0F, 0.0F, 0.0F);
        //}
        GlStateManager.scaled(-var14, -var14, var14);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepthTest();
        GlStateManager.enableBlend();
        GlStateManager.glBlendFuncSeparate(770, 771, 1, 0);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldRenderer = tessellator.getBuffer();
        int var17 = 0;
        if (entity.isSneaking()) {
            var17 += 4;
        }
        var17 -= distance / 5;
        if (var17 < -8) {
            var17 = -8;
        }
        GlStateManager.disableTexture();
        float var18 = mc.fontRenderer.getStringWidth(entityName) / 2;
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(-var18 + 3, -3 + var17, 0.0D).color(0.0F, 0.0F, 0.0F, 0.5F).endVertex();
        worldRenderer.pos(-var18 + 3, 9 + var17, 0.0D).color(0.0F, 0.0F, 0.0F, 0.5F).endVertex();
        worldRenderer.pos(var18 - 1, 8 + var17, 0.0D).color(0.0F, 0.0F, 0.0F, 0.5F).endVertex();
        worldRenderer.pos(var18 - 1, -3 + var17, 0.0D).color(0.0F, 0.0F, 0.0F, 0.2F).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture();
        //mc.fontRenderer.drawStringWithShadow(entityName, -var18, var17 - 1,0xFFFFFFFF);
        UIUtils.drawTextOnScreen(entityName, (int) -var18, var17 - 1, 0xFFFFFFFF);
        GlStateManager.enableDepthTest();
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }

}