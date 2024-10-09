package paul.fallen.module.modules.render;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;

public class NameTags extends Module {

    public NameTags(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @SubscribeEvent
    public void onRenderHUD(RenderGameOverlayEvent.Post event) {
        try {
            if (mc.world == null || mc.player == null) {
                return;
            }

            // Get the width and height of the screen
            int screenWidth = mc.getMainWindow().getWidth();
            int screenHeight = mc.getMainWindow().getHeight();

            // Iterate through all entities in the world
            for (Entity entity : mc.world.getAllEntities()) {
                if (entity instanceof PlayerEntity || entity == mc.player) {
                    continue;
                }

                Vector3d entityPos = entity.getPositionVec();
                Vector3d playerPos = mc.player.getPositionVec();

                // Calculate the difference between the player and entity positions
                double deltaX = entityPos.x - playerPos.x;
                double deltaY = entityPos.y - playerPos.y;
                double deltaZ = entityPos.z - playerPos.z;

                // Project the entity position to screen coordinates
                double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
                double scale = 1.0 / distance; // Scale factor to normalize the distance


            }

        } catch (Exception ignored) {
        }
    }
}
