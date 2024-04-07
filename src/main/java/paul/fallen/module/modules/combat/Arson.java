package paul.fallen.module.modules.combat;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;
import paul.fallen.utils.entity.PlayerControllerUtils;
import paul.fallen.utils.entity.RotationUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public final class Arson extends Module {

    private final Setting mode;

    public Arson(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        mode = new Setting("Mode", this, "packet", new ArrayList<>(Arrays.asList("packet", "legit")));

        addSetting(mode);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            Entity entity = findClosestEntity();

            if (entity != null) {
                assert mc.player != null;
                if (mc.player.getHeldItemMainhand().getItem() == Items.FLINT_AND_STEEL) {
                    BlockPos posToLight = getPosToLight(entity);
                    if (posToLight != null) {
                        assert mc.world != null;
                        if (mc.world.getBlockState(posToLight).getBlock() != Blocks.FIRE) {
                            //mc.playerController.processRightClickBlock(mc.player, mc.world, posToLight, EnumFacing.UP, new Vec3d(0.5, 0, 0.5), EnumHand.MAIN_HAND);
                            assert mc.playerController != null;
                            PlayerControllerUtils.rightClickBlock(new Vector3d(0.5, 0, 0.5), Direction.DOWN, posToLight);
                            mc.player.swingArm(Hand.MAIN_HAND);

                            float[] rot = RotationUtils.getYawAndPitch(new Vector3d(posToLight.getX() + 0.5, posToLight.getY(), posToLight.getZ() + 0.5));
                            if (mode.getValString() == "packet") {
                                //mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rot[0], rot[1], mc.player.onGround));
                                mc.player.connection.sendPacket(new CPlayerPacket.RotationPacket(rot[0], rot[1], mc.player.isOnGround()));
                            } else {
                                mc.player.rotationYaw = rot[0];
                                mc.player.rotationPitch = rot[1];
                            }
                        }
                    }
                } else {
                    int slot = getSlot(Items.FLINT_AND_STEEL);
                    if (slot != -1) {
                        mc.player.inventory.currentItem = slot;
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    private Entity findClosestEntity() {
        Entity closestEntity = null;
        double closestDistance = Double.MAX_VALUE;

        assert mc.world != null;
        for (Entity entity : mc.world.getAllEntities()) {
            if (entity != null && entity != mc.player) {
                assert mc.player != null;
                double distance = mc.player.getDistanceSq(entity);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestEntity = entity;
                }
            }
        }
        assert closestEntity != null;
        if (mc.player.getDistance(closestEntity) < 5) {
            return closestEntity;
        } else {
            return null;
        }
    }

    private int getSlot(Item item) {
        for (int i = 0; i < Objects.requireNonNull(mc.player).inventory.mainInventory.size(); i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == item) {
                return i;
            }
        }
        return -1;
    }

    private BlockPos getPosToLight(Entity entity) {
        BlockPos blockPos = null;
        BlockPos ePos = entity.getPosition();

        assert mc.world != null;
        if (!mc.world.getBlockState(ePos.add(1, -1, 0)).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(ePos.add(1, 0, 0)).getBlock().equals(Blocks.AIR)) {
            blockPos = ePos.add(1, -1, 0);
        } else if (!mc.world.getBlockState(ePos.add(-1, -1, 0)).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(ePos.add(-1, 0, 0)).getBlock().equals(Blocks.AIR)) {
            blockPos = ePos.add(-1, -1, 0);
        } else if (!mc.world.getBlockState(ePos.add(0, -1, 1)).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(ePos.add(0, 0, 1)).getBlock().equals(Blocks.AIR)) {
            blockPos = ePos.add(0, -1, 1);
        } else if (!mc.world.getBlockState(ePos.add(0, -1, -1)).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(ePos.add(0, 0, -1)).getBlock().equals(Blocks.AIR)) {
            blockPos = ePos.add(0, -1, -1);
        }

        return blockPos;
    }

    private enum Mode {
        PACKET("Packet"),
        LEGIT("Legit");

        private final String name;

        Mode(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }
}