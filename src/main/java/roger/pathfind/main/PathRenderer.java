package roger.pathfind.main;

import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import roger.pathfind.main.path.Node;
import roger.pathfind.main.path.PathElm;
import roger.pathfind.main.path.impl.JumpNode;
import roger.pathfind.main.path.impl.TravelVector;
import roger.pathfind.main.walk.Walker;
import roger.util.RenderUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PathRenderer {


    private static PathRenderer renderer;
    private List<PathElm> path = new ArrayList<>();

    public PathRenderer() {
        renderer = this;
    }

    public void render(List<PathElm> elms) {
        path = elms;
    }


    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if(!Walker.getInstance().isActive())
            return;

        if (!path.isEmpty()) {
            Node lastNode = null;
            for (PathElm elm : path) {

                if(elm instanceof Node) {
                    if (elm instanceof JumpNode) {
                        RenderUtil.drawFilledEsp(((Node) elm).getBlockPosUnder().subtract(new Vector3i(0, 1, 0)), Color.GREEN, event);
                    }

                    if (lastNode != null) {
                        List<Vector3d> lines = new ArrayList<>();
                        lines.add(new Vector3d(lastNode.getBlockPos().getX(), lastNode.getBlockPos().getY(), lastNode.getBlockPos().getZ()).subtract(0, 0.5, 0));
                        lines.add(new Vector3d(((Node) elm).getBlockPos().getX(), ((Node) elm).getBlockPos().getY(), ((Node) elm).getBlockPos().getZ()).subtract(0, 0.5, 0));
                        RenderUtil.drawLines(lines, 2f, event.getPartialTicks(), new Color(138, 206, 255).getRGB(), event);
                    }

                    lastNode = (Node) elm;
                }

                if(elm instanceof TravelVector) {
                    Node from = ((TravelVector) elm).getFrom();
                    Node to = ((TravelVector) elm).getTo();

                    List<Vector3d> lines = new ArrayList<>();
                    if (lastNode != null)
                        lines.add(new Vector3d(lastNode.getBlockPos().getX(), lastNode.getBlockPos().getY(), lastNode.getBlockPos().getZ()).subtract(0, 0.5, 0));

                    lines.add(new Vector3d(from.getBlockPos().getX(), from.getBlockPos().getY(), from.getBlockPos().getZ()).subtract(0, 0.5, 0));
                    lines.add(new Vector3d(to.getBlockPos().getX(), to.getBlockPos().getY(), to.getBlockPos().getZ()).subtract(0, 0.5, 0));

                    RenderUtil.drawLines(lines, 2f, event.getPartialTicks(), new Color(138, 206, 255).getRGB(), event);

                    RenderUtil.drawFilledEsp(from.getBlockPosUnder(), new Color(138, 206, 255), event);
                    RenderUtil.drawFilledEsp(to.getBlockPosUnder(), new Color(138, 206, 255), event);

                    lastNode = to;
                }
            }
        }
    }

    public static PathRenderer getInstance() {
        return renderer;
    }
}
