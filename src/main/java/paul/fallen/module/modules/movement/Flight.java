package paul.fallen.module.modules.movement;

import net.minecraft.network.play.client.CConfirmTeleportPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.packetevent.PacketEvent;
import paul.fallen.setting.Setting;
import paul.fallen.utils.client.MathUtils;

public final class Flight extends Module {

    private final Setting ncp;
    private final Setting upSpeed;
    private final Setting baseSpeed;
    private final Setting downSpeed;

    public Flight(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        ncp = new Setting("NCP", this, false);
        upSpeed = new Setting("up-speed", "Up-Speed", this, 1.0F, (float) 0.005, 10.0F);
        baseSpeed = new Setting("base-speed", "Base-Speed", this, 1.0F, (float) 0.005, 10.0F);
        downSpeed = new Setting("down-speed", "Down-Speed", this, 1.0F, (float) 0.005, 10.0F);
        addSetting(ncp);
        addSetting(upSpeed);
        addSetting(baseSpeed);
        addSetting(downSpeed);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            if (!ncp.bval) {
                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.player.setMotion(mc.player.getMotion().x, upSpeed.dval, mc.player.getMotion().z);
                } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                    mc.player.setMotion(mc.player.getMotion().x, -downSpeed.dval, mc.player.getMotion().z);
                } else {
                    mc.player.setMotion(mc.player.getMotion().x, 0, mc.player.getMotion().z);
                }
                if (mc.gameSettings.keyBindForward.isKeyDown() ||
                        mc.gameSettings.keyBindRight.isKeyDown() ||
                        mc.gameSettings.keyBindBack.isKeyDown() ||
                        mc.gameSettings.keyBindLeft.isKeyDown()) {
                    MathUtils.setSpeed(baseSpeed.dval);
                } else {
                    mc.player.setMotion(0, mc.player.getMotion().y, 0);
                }
                MathUtils.setSpeed(baseSpeed.dval);
            } else {
                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.player.setMotion(mc.player.getMotion().x, upSpeed.dval, mc.player.getMotion().z);
                } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                    mc.player.setMotion(mc.player.getMotion().x, -downSpeed.dval, mc.player.getMotion().z);
                } else {
                    mc.player.setMotion(mc.player.getMotion().x, 0, mc.player.getMotion().z);
                }
                if (mc.gameSettings.keyBindForward.isKeyDown() ||
                mc.gameSettings.keyBindRight.isKeyDown() ||
                mc.gameSettings.keyBindBack.isKeyDown() ||
                mc.gameSettings.keyBindLeft.isKeyDown()) {
                    MathUtils.setSpeed(baseSpeed.dval);
                } else {
                    mc.player.setMotion(0, mc.player.getMotion().y, 0);
                }

                if (mc.player.ticksExisted % 2 == 0) {
                    mc.player.fallDistance = 50000 + Math.round(Math.random() * 50000);
                } else {
                    mc.player.fallDistance = 50000 - Math.round(Math.random() * 50000);
                }

                mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX() + mc.player.getMotion().x, mc.player.getPosY() + mc.player.getMotion().y, mc.player.getPosZ() + mc.player.getMotion().z, false));
                mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX() + mc.player.getMotion().x, MathUtils.generateRandomNumber(0, 1) == 0 ? mc.player.getMotion().y + Integer.MAX_VALUE : mc.player.getMotion().y - Integer.MAX_VALUE, mc.player.getPosZ() + mc.player.getMotion().z, true));
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if (ncp.bval) {
            if (event.getPacket() instanceof SPlayerPositionLookPacket) {
                SPlayerPositionLookPacket sPlayerPositionLookPacket = (SPlayerPositionLookPacket) event.getPacket();
                mc.player.connection.sendPacket(new CConfirmTeleportPacket(sPlayerPositionLookPacket.getTeleportId()));
                mc.player.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(sPlayerPositionLookPacket.getX(), sPlayerPositionLookPacket.getY(), sPlayerPositionLookPacket.getZ(), sPlayerPositionLookPacket.getYaw(), sPlayerPositionLookPacket.getPitch(), false));
                mc.player.setPosition(sPlayerPositionLookPacket.getX(), sPlayerPositionLookPacket.getY(), sPlayerPositionLookPacket.getZ());
                event.setCanceled(true);
            }
        }
    }
}