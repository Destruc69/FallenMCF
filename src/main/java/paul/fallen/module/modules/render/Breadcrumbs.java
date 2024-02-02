package paul.fallen.module.modules.render;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.utils.render.RenderUtils;

import java.util.ArrayList;

public class Breadcrumbs extends Module {

    private final ArrayList<BlockPos> blockPosArrayList = new ArrayList<>();

    public Breadcrumbs(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        BlockPos blockPos = mc.player.getPosition().down();

        if (!blockPosArrayList.contains(blockPos)) {
            blockPosArrayList.add(blockPos);
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (blockPosArrayList.size() > 0) {
            RenderUtils.drawPath(blockPosArrayList, 0, 1, 0, event);
        }
    }

}

