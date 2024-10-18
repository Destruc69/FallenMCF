package paul.fallen.module.modules.movement;

import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CConfirmTeleportPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;
import paul.fallen.packetevent.PacketEvent;
import paul.fallen.utils.client.MathUtils;
import paul.fallen.utils.entity.EntityUtils;

import java.util.ArrayList;
import java.util.Arrays;

public final class Flight extends Module {

    private final ArrayList<IPacket> packets = new ArrayList<>();
    private final Setting mode;
    private final Setting upSpeed;
    private final Setting baseSpeed;
    private final Setting downSpeed;
    private final Setting antiKick;

    public Flight(int bind, String name, String displayName, Category category, String description) {
        super(bind, name, displayName, category, description);

        mode = new Setting("Mode", this, "ncp", new ArrayList<>(Arrays.asList("ncp", "vanilla", "blink")));
        upSpeed = new Setting("Up-Speed", this, 1.0F, 0.0005F, 10.0F, false);
        baseSpeed = new Setting("Base-Speed", this, 1.0F, 0.0005F, 10.0F, false);
        downSpeed = new Setting("Down-Speed", this, 1.0F, 0.0005F, 10.0F, false);
        antiKick = new Setting("AntiKick", this, false);
        addSetting(mode);
        addSetting(upSpeed);
        addSetting(baseSpeed);
        addSetting(downSpeed);
        addSetting(antiKick);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mode.getValString().equals("blink")) {
            for (IPacket p : packets) {
                mc.player.connection.sendPacket(p);
            }
        }
        packets.clear();
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        try {
            if (mc.player == null || mc.player.getRidingEntity() != null) return;

            double[] dir = MathUtils.directionSpeed(baseSpeed.getValDouble());
            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                mc.player.setMotion(mc.player.getMotion().x, upSpeed.getValDouble(), mc.player.getMotion().z);
            } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                mc.player.setMotion(mc.player.getMotion().x, -downSpeed.getValDouble(), mc.player.getMotion().z);
            } else {
                mc.player.setMotion(mc.player.getMotion().x, 0, mc.player.getMotion().z);
            }

            if (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() ||
                    mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown()) {
                MathUtils.setSpeed(baseSpeed.getValDouble());
            } else {
                mc.player.setMotion(0, mc.player.getMotion().y, 0);
            }

            if (mode.getValString().equals("ncp")) {
                handleNcpMode();
            } else if (mode.getValString().equals("blink")) {
                handleBlinkMode();
            } else if (mode.getValString().equals("vanilla") && antiKick.getValBoolean()) {
                handleVanillaKickBypass();
            }

        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if (mode.getValString().equals("ncp")) {
            if (event.getPacket() instanceof SPlayerPositionLookPacket) {
                SPlayerPositionLookPacket packet = (SPlayerPositionLookPacket) event.getPacket();
                mc.player.connection.sendPacket(new CConfirmTeleportPacket(packet.getTeleportId()));
                mc.player.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch(), false));
                mc.player.setPosition(packet.getX(), packet.getY(), packet.getZ());
                event.setCanceled(true);
            }
        } else if (mode.getValString().equals("blink") && event instanceof PacketEvent.Outgoing) {
            packets.add(event.getPacket());
            event.setCanceled(true);
        }
    }

    private void handleNcpMode() {
        if (mc.player.ticksExisted % 2 == 0) {
            mc.player.fallDistance = 50000 + Math.round(Math.random() * 50000);
        } else {
            mc.player.fallDistance = 50000 - Math.round(Math.random() * 50000);
        }
        mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX() + mc.player.getMotion().x, mc.player.getPosY() + mc.player.getMotion().y, mc.player.getPosZ() + mc.player.getMotion().z, false));
        mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX() + mc.player.getMotion().x, mc.player.getPosY() + (MathUtils.generateRandomNumber(0, 1) == 0 ? Integer.MAX_VALUE : -Integer.MAX_VALUE), mc.player.getPosZ() + mc.player.getMotion().z, true));
    }

    private void handleBlinkMode() {
        // Placeholder for blink mode logic if needed
    }

    private void handleVanillaKickBypass() {
        double x = mc.player.getPosX();
        double y = mc.player.getPosY();
        double z = mc.player.getPosZ();
        double ground = EntityUtils.getFallDistance(mc.player);

        if (mc.player.ticksExisted % 2 == 0) {
            for (double posY = y; posY > ground; posY -= 8D) {
                mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(x, posY, z, true));
                if (posY - 8D < ground) break;
            }
            mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(x, ground, z, true));
            for (double posY = ground; posY < y; posY += 8D) {
                mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(x, posY, z, true));
                if (posY + 8D > y) break;
            }
            mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(x, y, z, true));
        }
    }
}