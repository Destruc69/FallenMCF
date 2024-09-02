package roger.pathfind.main.walk;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import roger.pathfind.main.LookManager;
import roger.pathfind.main.PathRenderer;
import roger.pathfind.main.astar.AStarNode;
import roger.pathfind.main.astar.AStarPathFinder;
import roger.pathfind.main.path.PathElm;
import roger.pathfind.main.path.impl.*;
import roger.pathfind.main.processor.ProcessorManager;
import roger.pathfind.main.walk.target.WalkTarget;
import roger.pathfind.main.walk.target.impl.*;
import roger.util.LookUtil;
import roger.util.Util;

import java.util.List;

public class Walker {
    private static Walker instance;
    private boolean isActive;

    List<PathElm> path;
    WalkTarget currentTarget;

    public Walker() {
        instance = this;
    }

    public void walk(BlockPos start, BlockPos end, int nodeCount) {

        isActive = true;

        List<AStarNode> nodes = AStarPathFinder.compute(start, end, nodeCount);
        path = ProcessorManager.process(nodes);

        if(path.size() == 0) {
            isActive = false;
            currentTarget = null;
            return;
        }


        PathRenderer.getInstance().render(path);
        currentTarget = null;
    }


    // Key press in here
    @SubscribeEvent
    public void onClientTickPre(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END || Minecraft.getInstance().player == null)
            return;


        /*
        double motionX = Minecraft.getMinecraft().thePlayer.motionX;
        double motionZ = Minecraft.getMinecraft().thePlayer.motionZ;
        if(motionX > 0 || motionZ > 0)
             System.out.println("Motion: " + Minecraft.getMinecraft().thePlayer.motionX + " " + Minecraft.getMinecraft().thePlayer.motionZ);
        */

        if(!isActive)
            return;

        if (currentTarget == null && path != null)
            currentTarget = getCurrentTarget(path.get(0));

        WalkTarget playerOnTarget;
        if (!((playerOnTarget = onTarget()) == null) && onTarget() != null)
            currentTarget = playerOnTarget;

        // while, so we don't skip ticks
        while (tick(currentTarget)) {
            // removes it
            path.remove(0);

            if(path.isEmpty()) {
                isActive = false;
                currentTarget = null;
                KeyBinding.setKeyBindState(InputMappings.getInputByCode(Minecraft.getInstance().gameSettings.keyBindForward.getKey().getKeyCode(), 0), false);
                KeyBinding.setKeyBindState(InputMappings.getInputByCode(Minecraft.getInstance().gameSettings.keyBindLeft.getKey().getKeyCode(), 0), false);
                KeyBinding.setKeyBindState(InputMappings.getInputByCode(Minecraft.getInstance().gameSettings.keyBindRight.getKey().getKeyCode(), 0), false);
                KeyBinding.setKeyBindState(InputMappings.getInputByCode(Minecraft.getInstance().gameSettings.keyBindBack.getKey().getKeyCode(), 0), false);
                LookManager.getInstance().cancel();

                return;
            }

            currentTarget = getCurrentTarget(path.get(0));
        }

        //KeyBinding.setKeyBindState(InputMappings.getInputByCode(Minecraft.getInstance().gameSettings.keyBindSneak.getKey().getKeyCode(), 0), true);
        Tuple<Double, Double> angles = LookUtil.getAngles(currentTarget.getCurrentTarget());
        LookManager.getInstance().setTarget(angles.getA(), currentTarget instanceof JumpTarget ? -10 : 10);

        // This is to prevent the player from falling when not facing accurate enough
        if (currentTarget.getCurrentTarget().getY() == Minecraft.getInstance().player.getPosY()) {
            KeyBinding.setKeyBindState(InputMappings.getInputByCode(Minecraft.getInstance().gameSettings.keyBindSneak.getKey().getKeyCode(), 0), Minecraft.getInstance().player.isOnGround() && Minecraft.getInstance().world.getBlockState(Minecraft.getInstance().player.getPosition().down()).isAir());
        }

        float yawDifference = Math.abs(Math.round(angles.getA()) - Math.round(Minecraft.getInstance().player.rotationYaw));

        if (yawDifference <= 15) {
            pressKeys(angles.getA());
        } else {
            stopMovement();
        }
    }


    private void pressKeys(double targetYaw) {
        double difference = targetYaw - Minecraft.getInstance().player.rotationYaw;
        KeyBinding.setKeyBindState(InputMappings.getInputByCode(Minecraft.getInstance().gameSettings.keyBindForward.getKey().getKeyCode(), 0), false);
        KeyBinding.setKeyBindState(InputMappings.getInputByCode(Minecraft.getInstance().gameSettings.keyBindLeft.getKey().getKeyCode(), 0), false);
        KeyBinding.setKeyBindState(InputMappings.getInputByCode(Minecraft.getInstance().gameSettings.keyBindRight.getKey().getKeyCode(), 0), false);
        KeyBinding.setKeyBindState(InputMappings.getInputByCode(Minecraft.getInstance().gameSettings.keyBindBack.getKey().getKeyCode(), 0), false);

        if (22.5 > difference && difference > -22.5) {   // Forwards

            KeyBinding.setKeyBindState(InputMappings.getInputByCode(Minecraft.getInstance().gameSettings.keyBindForward.getKey().getKeyCode(), 0), true);
        } else if (-22.5 > difference && difference > -67.5) {   // Forwards+Right

            KeyBinding.setKeyBindState(InputMappings.getInputByCode(Minecraft.getInstance().gameSettings.keyBindForward.getKey().getKeyCode(), 0), true);
            KeyBinding.setKeyBindState(InputMappings.getInputByCode(Minecraft.getInstance().gameSettings.keyBindLeft.getKey().getKeyCode(), 0), true);
        } else if (-67.5 > difference && difference > -112.5) { // Right

            KeyBinding.setKeyBindState(InputMappings.getInputByCode(Minecraft.getInstance().gameSettings.keyBindRight.getKey().getKeyCode(), 0), true);
        } else if(-112.5 > difference && difference > -157.5) { // Backwards + Right

            KeyBinding.setKeyBindState(InputMappings.getInputByCode(Minecraft.getInstance().gameSettings.keyBindRight.getKey().getKeyCode(), 0), true);
            KeyBinding.setKeyBindState(InputMappings.getInputByCode(Minecraft.getInstance().gameSettings.keyBindBack.getKey().getKeyCode(), 0), true);
        } else if((-157.5 > difference && difference > -180) || (180 > difference && difference > 157.5)) { // Backwards

            KeyBinding.setKeyBindState(InputMappings.getInputByCode(Minecraft.getInstance().gameSettings.keyBindBack.getKey().getKeyCode(), 0), true);
        } else if(67.5 > difference && difference > 22.5) { // Forwards + Left

            KeyBinding.setKeyBindState(InputMappings.getInputByCode(Minecraft.getInstance().gameSettings.keyBindForward.getKey().getKeyCode(), 0), true);
            KeyBinding.setKeyBindState(InputMappings.getInputByCode(Minecraft.getInstance().gameSettings.keyBindLeft.getKey().getKeyCode(), 0), true);

        } else if (112.5 > difference && difference > 67.5) { // Left

            KeyBinding.setKeyBindState(InputMappings.getInputByCode(Minecraft.getInstance().gameSettings.keyBindLeft.getKey().getKeyCode(), 0), true);
        } else if (157.5 > difference && difference > 112.5) {  // Backwards+Left

            KeyBinding.setKeyBindState(InputMappings.getInputByCode(Minecraft.getInstance().gameSettings.keyBindBack.getKey().getKeyCode(), 0), true);
            KeyBinding.setKeyBindState(InputMappings.getInputByCode(Minecraft.getInstance().gameSettings.keyBindLeft.getKey().getKeyCode(), 0), true);
        }
    }

    private void stopMovement() {
        KeyBinding.setKeyBindState(InputMappings.getInputByCode(Minecraft.getInstance().gameSettings.keyBindForward.getKey().getKeyCode(), 0), false);
        KeyBinding.setKeyBindState(InputMappings.getInputByCode(Minecraft.getInstance().gameSettings.keyBindLeft.getKey().getKeyCode(), 0), false);
        KeyBinding.setKeyBindState(InputMappings.getInputByCode(Minecraft.getInstance().gameSettings.keyBindRight.getKey().getKeyCode(), 0), false);
        KeyBinding.setKeyBindState(InputMappings.getInputByCode(Minecraft.getInstance().gameSettings.keyBindBack.getKey().getKeyCode(), 0), false);
        KeyBinding.setKeyBindState(InputMappings.getInputByCode(Minecraft.getInstance().gameSettings.keyBindJump.getKey().getKeyCode(), 0), false);
        KeyBinding.setKeyBindState(InputMappings.getInputByCode(Minecraft.getInstance().gameSettings.keyBindSneak.getKey().getKeyCode(), 0), false);
    }

    // This checks if the player is on any nodes further in the queue, which means the player, due to probably high speed, has skipped some. Then
    // this removes the nodes behind it and sets it as the current target.
    private WalkTarget onTarget() {
        if (path != null) {
            for (int i = 0; i < path.size(); i++) {
                PathElm elm = path.get(i);

                if (elm.playerOn(Minecraft.getInstance().player.getPositionVec())) {
                    System.out.println("Returned true: " + elm);

                    if (elm == currentTarget.getElm())
                        return null;


                    // Get the next one if the player is on it
                    // if its travel vector, we don't get the next one, cos we need to go to the dest.
                    // if its jump, we don't get the next one, cos we need to jump.
                    if (path.size() > i + 1 && !(elm instanceof TravelVector) && !(elm instanceof JumpNode)) {
                        System.out.println("E");
                        path.subList(0, i + 1).clear();
                    } else {
                        path.subList(0, i).clear();
                    }

                    // cutting off might end jump target so stop jumping
                    KeyBinding.setKeyBindState(InputMappings.getInputByCode(Minecraft.getInstance().gameSettings.keyBindBack.getKey().getKeyCode(), 0), false);


                    return getCurrentTarget(path.get(0));
                }
            }
        }
        return null;
    }

    // The return value of this is if the node has been satisfied, and the next one should be polled.
    private boolean tick(WalkTarget current) {

        // We should improve the predicted motion calculation. Right now it's based on the estimate that the motion will last for 12 ticks, but this is different across speeds.
        Vector3d offset = new Vector3d(Minecraft.getInstance().player.getMotion().x, 0, Minecraft.getInstance().player.getMotion().z);
        Vector3d temp = offset;
        offset.add(temp);

        for (int i = 0; i < 12; i++) {

            // 0.54600006f is how much the motion stops after every tick after not moving.
            offset = offset.add((temp = Util.vecMultiply(temp, 0.54600006f)));
        }

        return current.tick(offset, Minecraft.getInstance().player.getPositionVec());
    }

    private WalkTarget getCurrentTarget(PathElm elm) {
        if (elm instanceof FallNode)
            return new FallTarget((FallNode) elm);
        if (elm instanceof TravelNode)
            return new TravelTarget((TravelNode) elm);
        if (elm instanceof TravelVector)
            return new TravelVectorTarget((TravelVector) elm);
        if (elm instanceof JumpNode) {
            if (path.size() > 1)
                return new JumpTarget((JumpNode) elm, getCurrentTarget(path.get(1)));
            return new JumpTarget((JumpNode) elm, null);
        }
        System.out.println("Wrong walk target");
        return null;
    }

    public boolean isActive() {
        return isActive;
    }

    public static Walker getInstance() {
        return instance;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }
}
