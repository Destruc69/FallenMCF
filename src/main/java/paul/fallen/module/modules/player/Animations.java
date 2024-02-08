package paul.fallen.module.modules.player;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.util.Hand;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;

public class Animations extends Module {
    public Animations(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        ItemRenderer ir = mc.getItemRenderer();
        if (mc.player.swingingHand == Hand.MAIN_HAND) {
            if (event.getHand() == Hand.MAIN_HAND) {

            }
        } else if (mc.player.swingingHand == Hand.OFF_HAND) {
            if (event.getHand() == Hand.OFF_HAND) {
                GlStateManager.translated(-0.15f, 0.2f, -0.2f);

                GlStateManager.translated(0, 0, -0.2f);
                if (-mc.player.swingProgress > -0.5) {
                    GlStateManager.translated(0, -mc.player.swingProgress, 0);
                } else {
                    GlStateManager.translated(0, mc.player.swingProgress - 1f, 0);
                }

            }
        }
    }
}
