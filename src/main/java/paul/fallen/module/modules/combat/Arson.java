package paul.fallen.module.modules.combat;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
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

public final class Arson extends Module {

    private final Setting mode;

    public Arson(int bind, String name, String displayName, Category category, String description) {
        super(bind, name, displayName, category, description);
        mode = new Setting("Mode", this, "packet", new ArrayList<>(Arrays.asList("packet", "legit")));
        addSetting(mode);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            Entity entity = findClosestEntity();

            if (entity != null) {
                if (mc.player.getHeldItemMainhand().getItem() == Items.FLINT_AND_STEEL) {
                    BlockPos posToLight = getPosToLight(entity);
                    if (posToLight != null) {
                        if (mc.world.getBlockState(posToLight).getBlock() != Blocks.FIRE) {
                            PlayerControllerUtils.rightClickBlock(
                                    new Vector3d(0.5, 0, 0.5),
                                    Direction.DOWN,
                                    posToLight
                            );
                            mc.player.swingArm(Hand.MAIN_HAND);

                            float[] rot = RotationUtils.getYawAndPitch(new Vector3d(posToLight.getX() + 0.5, posToLight.getY(), posToLight.getZ() + 0.5));
                            if ("packet".equals(mode.getValString())) {
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
        if (mc.world == null || mc.player == null) return null;

        Entity closestEntity = null;
        double closestDistance = Double.MAX_VALUE;

        for (Entity entity : mc.world.getAllEntities()) {
            if (entity instanceof PlayerEntity && entity != mc.player) {
                double distance = mc.player.getDistanceSq(entity);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestEntity = entity;
                }
            }
        }

        return (closestEntity != null && mc.player.getDistance(closestEntity) < 5) ? closestEntity : null;
    }

    private int getSlot(Item item) {
        if (mc.player == null) return -1;
        for (int i = 0; i < mc.player.inventory.mainInventory.size(); i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == item) {
                return i;
            }
        }
        return -1;
    }

    private BlockPos getPosToLight(Entity entity) {
        BlockPos ePos = entity.getPosition();

        for (BlockPos offset : Arrays.asList(
                ePos.add(1, -1, 0), ePos.add(-1, -1, 0),
                ePos.add(0, -1, 1), ePos.add(0, -1, -1)
        )) {
            if (!mc.world.getBlockState(offset).getBlock().equals(Blocks.AIR) &&
                    mc.world.getBlockState(offset.add(0, 1, 0)).getBlock().equals(Blocks.AIR)) {
                return offset;
            }
        }
        return null;
    }

    private enum Mode {
        PACKET("Packet"),
        LEGIT("Legit");

        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}