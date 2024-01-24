/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.movement;

import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.events.PacketSendEvent;
import paul.fallen.module.Module;
import paul.fallen.setting.Setting;
import paul.fallen.utils.client.MathUtils;

public final class AntiHunger extends Module {
    private final Setting cancelSprintPacket;
    private final Setting groundSpoof;

    public AntiHunger(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
        cancelSprintPacket = new Setting("CancelSprintPacket", this, false);
        groundSpoof = new Setting("GroundSpoof", this, false);
        addSetting(cancelSprintPacket);
        addSetting(groundSpoof);
        setState(true);
    }

    @Override
    public void onEnable() {
        if (cancelSprintPacket.bval) {
            try {
                assert mc.player != null;
                if (mc.player.isSprinting()) {
                    mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
                }
            } catch (Exception ignored) {
            }
        }
    }

    @SubscribeEvent
    public void onPacketOut(PacketSendEvent event) {
        if (cancelSprintPacket.bval && event.getPacket() instanceof CEntityActionPacket) {
            CEntityActionPacket cPacketEntityAction = (CEntityActionPacket) event.getPacket();
            if (cPacketEntityAction.getAction().equals(CEntityActionPacket.Action.START_SPRINTING) || cPacketEntityAction.getAction().equals(CEntityActionPacket.Action.STOP_SPRINTING)) {
                event.setCanceled(true);
            }
        }

        if (groundSpoof.bval && event.getPacket() instanceof CPlayerPacket) {
            CPlayerPacket oldPacket = (CPlayerPacket) event.getPacket();

            double x = oldPacket.getX(-1);
            double y = oldPacket.getY(-1);
            double z = oldPacket.getZ(-1);
            float yaw = oldPacket.getYaw(-1);
            float pitch = oldPacket.getPitch(-1);

            assert mc.playerController != null;
            if (mc.playerController.getIsHittingBlock())
                return;

            if (MathUtils.calculateFallDistance(x, y, z) > 0.5)
                return;

            if (changesPosition() && changesLook())
                event.setPacket(new CPlayerPacket.PositionRotationPacket(x, y, z, yaw, pitch, false));
            else if (changesPosition())
                event.setPacket(new CPlayerPacket.PositionPacket(x, y, z, false));
            else if (changesLook())
                event.setPacket(new CPlayerPacket.RotationPacket(yaw, pitch, false));
            else
                event.setPacket(new CPlayerPacket(false));
        }
    }

    private boolean changesPosition() {
        assert mc.player != null;
        return mc.player.getPosX() != mc.player.prevPosX || mc.player.getPosZ() != mc.player.prevPosZ || mc.player.getPosY() != mc.player.prevPosY;
    }

    private boolean changesLook() {
        assert mc.player != null;
        return mc.player.rotationYaw != mc.player.prevRotationYaw || mc.player.rotationPitch != mc.player.prevRotationPitch;
    }
}
