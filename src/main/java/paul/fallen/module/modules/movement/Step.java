package paul.fallen.module.modules.movement;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public final class Step extends Module {

    private Setting mode;

    public Step(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        mode = new Setting("Mode", this, "ncp", new ArrayList<>(Arrays.asList("ncp", "aac")));
        addSetting(mode);
    }

    private boolean b = false;

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            PlayerEntity player = mc.player;

            if (!player.isOnGround() || player.isInWater() || player.isInLava()) {
                return;
            }

            if (player.moveForward == 0 && player.moveStrafing == 0) {
                return;
            }

            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                return;
            }

            AxisAlignedBB box = player.getBoundingBox().offset(0, 0.05, 0).grow(0.05);

            double stepHeight = Double.NEGATIVE_INFINITY;

            Stream<VoxelShape> blockShapes = player.world.getBlockCollisionShapes(player, box);
            List<AxisAlignedBB> blockCollisions = new ArrayList<>();
            blockShapes.forEach(shape -> blockCollisions.add(shape.getBoundingBox()));

            for (AxisAlignedBB bb : blockCollisions) {
                if (bb.maxY > stepHeight) {
                    stepHeight = bb.maxY;
                }
            }

            stepHeight = stepHeight - player.getPosY();

            if (mc.player.collidedHorizontally && mc.player.isOnGround() && mc.player.fallDistance == 0.0f && !mc.player.isOnLadder() && !mc.player.movementInput.jump) {
                if (!b) {
                    if (mode.getValString() == "ncp") {
                        ncpStep(stepHeight);
                    } else if (mode.getValString() == "aac") {
                        aacStep(stepHeight);
                    }
                    b = true;
                }
            } else {
                b = false;
            }
        } catch (Exception ignored) {
        }
    }

    public void ncpStep(double height){
        List<Double> offset = Arrays.asList(0.42,0.333,0.248,0.083,-0.078);
        double posX = mc.player.getPosX(); double posZ = mc.player.getPosZ();
        double y = mc.player.getPosY();
        if(height < 1.1){
            double first = 0.42;
            double second = 0.75;
            if(height != 1){
                first *= height;
                second *= height;
                if(first > 0.425){
                    first = 0.425;
                }
                if(second > 0.78){
                    second = 0.78;
                }
                if(second < 0.49){
                    second = 0.49;
                }
            }
            if(first == 0.42)
                first = 0.41999998688698;
            mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(posX, y + first, posZ, false));
            if(y+second < y + height)
                mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(posX, y + second, posZ, false));
        }else if(height <1.6){
            for(int i = 0; i < offset.size(); i++){
                double off = offset.get(i);
                y += off;
                mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(posX, y, posZ, false));
            }
        }else if(height < 2.1){
            double[] heights = {0.425,0.821,0.699,0.599,1.022,1.372,1.652,1.869};
            for(double off : heights){
                mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(posX, y + off, posZ, false));
            }
        }else{
            double[] heights = {0.425,0.821,0.699,0.599,1.022,1.372,1.652,1.869,2.019,1.907};
            for(double off : heights){
                mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(posX, y + off, posZ, false));
            }
        }

        if (height <= 0) {
            mc.player.setPosition(mc.player.getPosX(), mc.player.getPosY() + 0.42D, mc.player.getPosZ());
        } else if (height > 0) {
            mc.player.setPosition(mc.player.getPosX(), mc.player.getPosY() + height - 0.58, mc.player.getPosZ());
        }
    }

    public void aacStep(double height){
        double posX = mc.player.getPosX(); double posY = mc.player.getPosY(); double posZ = mc.player.getPosZ();
        if (height > 0) {
            mc.player.setPosition(mc.player.getPosX(), mc.player.getPosY() + height - 0.58, mc.player.getPosZ());
        }
        if(height < 1.1){
            double first = 0.42;
            double second = 0.75;
            if(height > 1){
                first *= height;
                second *= height;
                if(first > 0.4349){
                    first = 0.4349;
                }else if(first < 0.405){
                    first = 0.405;
                }
            }
            mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(posX, posY + first, posZ, false));
            if(posY+second < posY + height)
                mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(posX, posY + second, posZ, false));
            return;
        }
        List<Double> offset = Arrays.asList(0.434999999999998,0.360899999999992,0.290241999999991,0.220997159999987,0.13786084000003104,0.055);
        double y = mc.player.getPosY();
        for(int i = 0; i < offset.size(); i++){
            double off = offset.get(i);
            y += off;
            if(y > mc.player.getPosY() + height){
                double x = mc.player.getPosX(); double z = mc.player.getPosZ();
                double forward = mc.player.movementInput.moveForward;
                double strafe = mc.player.movementInput.moveStrafe;
                float YAW = mc.player.rotationYaw;
                double speed = 0.3;
                if(forward != 0 && strafe != 0)
                    speed -= 0.09;
                x += (forward * speed * Math.cos(Math.toRadians(YAW + 90.0f)) + strafe * speed * Math.sin(Math.toRadians(YAW + 90.0f))) *1;
                z += (forward * speed * Math.sin(Math.toRadians(YAW + 90.0f)) - strafe * speed * Math.cos(Math.toRadians(YAW + 90.0f))) *1;
                mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(
                        x, y,z, false));
                break;
            }
            if(i== offset.size() - 1){
                double x = mc.player.getPosX(); double z = mc.player.getPosZ();
                double forward = mc.player.movementInput.moveForward;
                double strafe = mc.player.movementInput.moveStrafe;
                float YAW = mc.player.rotationYaw;
                double speed = 0.3;
                if(forward != 0 && strafe != 0)
                    speed -= 0.09;
                x += (forward * speed * Math.cos(Math.toRadians(YAW + 90.0f)) + strafe * speed * Math.sin(Math.toRadians(YAW + 90.0f))) *1;
                z += (forward * speed * Math.sin(Math.toRadians(YAW + 90.0f)) - strafe * speed * Math.cos(Math.toRadians(YAW + 90.0f))) *1;
                mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(
                        x, y,z, false));
            }else{
                mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(posX, y, posZ, false));
            }
        }
    }
}

