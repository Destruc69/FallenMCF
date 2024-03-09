package paul.fallen.module.modules.combat;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.packetevent.PacketEvent;
import paul.fallen.setting.Setting;
import paul.fallen.utils.render.RenderUtils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class BackTrack extends Module {

    public static LivingEntity target;
    public static List<Vector3d> pastPositions = new ArrayList<>();
    public static List<Vector3d> forwardPositions = new ArrayList<>();
    public static List<Vector3d> positions = new ArrayList<>();
    private final Deque<IPacket> packets = new ArrayDeque<>();

    private Setting amount;
    private Setting forward;

    private int ticks;

    public BackTrack(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        amount = new Setting("Amount", this, 20, 1, 100);
        forward = new Setting("Forward", this, 20, 1, 100);
        addSetting(amount);
        addSetting(forward);
    }

    @Override
    public void onDisable() {
        try {
            super.onDisable();
            target = null;
            positions.clear();
            pastPositions.clear();
            forwardPositions.clear();
            packets.clear();
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            if (target == null) return;

            pastPositions.add(new Vector3d(target.getPosX(), target.getPosY(), target.getPosZ()));

            final double deltaX = (target.getPosX() - target.lastTickPosX) * 2;
            final double deltaZ = (target.getPosZ() - target.lastTickPosZ) * 2;

            forwardPositions.clear();
            int i = 0;
            while (forward.dval > forwardPositions.size()) {
                i++;
                forwardPositions.add(new Vector3d(target.getPosX() + deltaX * i, target.getPosY(), target.getPosZ() + deltaZ * i));
            }

            while (pastPositions.size() > (int) amount.dval) {
                pastPositions.remove(0);
            }

            positions.clear();
            positions.addAll(forwardPositions);
            positions.addAll(pastPositions);

            ticks++;
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onRender3DEvent(RenderWorldLastEvent event) {
        try {
            if (target != null && !positions.isEmpty()) RenderUtils.renderPath(new ArrayList<>(positions), event);
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onAttackEvent(AttackEntityEvent event) {
        try {
            if (event.getTarget() instanceof PlayerEntity) target = (LivingEntity) event.getTarget();
            ticks = 0;
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Outgoing event) {
        try {
            if (target == null) return;

            IPacket p = event.getPacket();

            packets.add(p);
            event.setCanceled(true);

            if ((int) amount.dval <= pastPositions.size()) {

                for (final IPacket thisPacket : packets) mc.player.connection.sendPacket(thisPacket);

                pastPositions.clear();
                packets.clear();
            }
        } catch (Exception ignored) {
        }
    }
}
