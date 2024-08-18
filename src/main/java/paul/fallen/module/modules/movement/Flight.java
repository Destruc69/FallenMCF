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
import java.util.Objects;

public final class Flight extends Module {

    private final ArrayList<IPacket> packets = new ArrayList<>();

    private final Setting mode;
    private final Setting upSpeed;
    private final Setting baseSpeed;
    private final Setting downSpeed;
    private final Setting antiKick;

    public Flight(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        mode = new Setting("Mode", this, "ncp", new ArrayList<>(Arrays.asList("ncp", "vanilla", "blink")));
        upSpeed = new Setting("Up-Speed", this, 1.0F, (float) 0.0005, 10.0F, false);
        baseSpeed = new Setting("Base-Speed", this, 1.0F, (float) 0.0005, 10.0F, false);
        downSpeed = new Setting("Down-Speed", this, 1.0F, (float) 0.0005, 10.0F, false);
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

        if (packets.size() > 0 && mode.getValString() == "blink")
        for (IPacket p : packets) {
            mc.player.connection.sendPacket(p);
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            if (event.phase == TickEvent.Phase.START) {
                if (Objects.equals(mode.getValString(), "vanilla")) {
                    if (mc.gameSettings.keyBindJump.isKeyDown()) {
                        mc.player.setMotion(mc.player.getMotion().x, upSpeed.getValDouble(), mc.player.getMotion().z);
                    } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                        mc.player.setMotion(mc.player.getMotion().x, -downSpeed.getValDouble(), mc.player.getMotion().z);
                    } else {
                        mc.player.setMotion(mc.player.getMotion().x, 0, mc.player.getMotion().z);
                    }
                    if (mc.gameSettings.keyBindForward.isKeyDown() ||
                            mc.gameSettings.keyBindRight.isKeyDown() ||
                            mc.gameSettings.keyBindBack.isKeyDown() ||
                            mc.gameSettings.keyBindLeft.isKeyDown()) {
                        MathUtils.setSpeed(baseSpeed.getValDouble());
                    } else {
                        mc.player.setMotion(0, mc.player.getMotion().y, 0);
                    }
                    MathUtils.setSpeed(baseSpeed.getValDouble());
                    if (antiKick.getValBoolean()) {
                        handleVanillaKickBypass();
                    }
                } else if (Objects.equals(mode.getValString(), "ncp")) {
                    if (mc.gameSettings.keyBindJump.isKeyDown()) {
                        mc.player.setMotion(mc.player.getMotion().x, upSpeed.getValDouble(), mc.player.getMotion().z);
                    } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                        mc.player.setMotion(mc.player.getMotion().x, -downSpeed.getValDouble(), mc.player.getMotion().z);
                    } else {
                        mc.player.setMotion(mc.player.getMotion().x, 0, mc.player.getMotion().z);
                    }
                    if (mc.gameSettings.keyBindForward.isKeyDown() ||
                            mc.gameSettings.keyBindRight.isKeyDown() ||
                            mc.gameSettings.keyBindBack.isKeyDown() ||
                            mc.gameSettings.keyBindLeft.isKeyDown()) {
                        MathUtils.setSpeed(baseSpeed.getValDouble());
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
                } else if (Objects.equals(mode.getValString(), "blink")) {
                    if (mc.gameSettings.keyBindJump.isKeyDown()) {
                        mc.player.setMotion(mc.player.getMotion().x, upSpeed.getValDouble(), mc.player.getMotion().z);
                    } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                        mc.player.setMotion(mc.player.getMotion().x, -downSpeed.getValDouble(), mc.player.getMotion().z);
                    } else {
                        mc.player.setMotion(mc.player.getMotion().x, 0, mc.player.getMotion().z);
                    }
                    if (mc.gameSettings.keyBindForward.isKeyDown() ||
                            mc.gameSettings.keyBindRight.isKeyDown() ||
                            mc.gameSettings.keyBindBack.isKeyDown() ||
                            mc.gameSettings.keyBindLeft.isKeyDown()) {
                        MathUtils.setSpeed(baseSpeed.getValDouble());
                    } else {
                        mc.player.setMotion(0, mc.player.getMotion().y, 0);
                    }
                    MathUtils.setSpeed(baseSpeed.getValDouble());
                    if (antiKick.getValBoolean()) {
                        handleVanillaKickBypass();
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if (Objects.equals(mode.getValString(), "ncp")) {
            if (event.getPacket() instanceof SPlayerPositionLookPacket) {
                SPlayerPositionLookPacket sPlayerPositionLookPacket = (SPlayerPositionLookPacket) event.getPacket();
                mc.player.connection.sendPacket(new CConfirmTeleportPacket(sPlayerPositionLookPacket.getTeleportId()));
                mc.player.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(sPlayerPositionLookPacket.getX(), sPlayerPositionLookPacket.getY(), sPlayerPositionLookPacket.getZ(), sPlayerPositionLookPacket.getYaw(), sPlayerPositionLookPacket.getPitch(), false));
                mc.player.setPosition(sPlayerPositionLookPacket.getX(), sPlayerPositionLookPacket.getY(), sPlayerPositionLookPacket.getZ());
                event.setCanceled(true);
            }
        } else if (Objects.equals(mode.getValString(), "blink")) {
            if (event instanceof PacketEvent.Outgoing) {
                packets.add(event.getPacket());
                event.setCanceled(true);
            }
        }
    }

    private void handleVanillaKickBypass() {
        final double x = mc.player.getPosX();
        final double y = mc.player.getPosY();
        final double z = mc.player.getPosZ();

        final double ground = EntityUtils.getFallDistance(mc.player);

        if (mc.player.ticksExisted % 2 == 0) {
            for (double posY = y; posY > ground; posY -= 8D) {
                mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(x, posY, z, true));

                if (posY - 8D < ground) break; // Prevent next step
            }

            mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(x, ground, z, true));


            for (double posY = ground; posY < y; posY += 8D) {
                mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(x, posY, z, true));

                if (posY + 8D > y) break; // Prevent next step
            }

            mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(x, y, z, true));
        }
    }
}