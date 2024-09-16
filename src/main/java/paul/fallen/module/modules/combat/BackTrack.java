package paul.fallen.module.modules.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;
import paul.fallen.packetevent.PacketEvent;
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

    private final Setting amount;
    private final Setting forward;

    private int ticks;

    public BackTrack(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        amount = new Setting("Amount", this, 20, 1, 100, true);
        forward = new Setting("Forward", this, 20, 1, 100, true);
        addSetting(amount);
        addSetting(forward);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        target = null;
        positions.clear();
        pastPositions.clear();
        forwardPositions.clear();
        packets.clear();
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (target == null) return;

        try {
            // Update past positions
            pastPositions.add(new Vector3d(target.getPosX(), target.getPosY(), target.getPosZ()));

            // Calculate forward positions
            double deltaX = (target.getPosX() - target.lastTickPosX) * 2;
            double deltaZ = (target.getPosZ() - target.lastTickPosZ) * 2;

            forwardPositions.clear();
            int steps = (int) forward.getValDouble();
            for (int i = 1; i <= steps; i++) {
                forwardPositions.add(new Vector3d(target.getPosX() + deltaX * i, target.getPosY(), target.getPosZ() + deltaZ * i));
            }

            // Trim past positions list
            if (pastPositions.size() > (int) amount.getValDouble()) {
                pastPositions = pastPositions.subList(pastPositions.size() - (int) amount.getValDouble(), pastPositions.size());
            }

            // Update positions
            positions.clear();
            positions.addAll(forwardPositions);
            positions.addAll(pastPositions);

            ticks++;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onRender3DEvent(RenderWorldLastEvent event) {
        if (target != null && !positions.isEmpty()) {
            RenderUtils.renderPath(new ArrayList<>(positions), event);
        }
    }

    @SubscribeEvent
    public void onAttackEvent(AttackEntityEvent event) {
        if (event.getTarget() instanceof PlayerEntity) {
            target = (LivingEntity) event.getTarget();
            ticks = 0;
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Outgoing event) {
        if (target == null) return;

        try {
            IPacket packet = event.getPacket();
            packets.add(packet);
            event.setCanceled(true);

            if (pastPositions.size() >= (int) amount.getValDouble()) {
                for (IPacket thisPacket : packets) {
                    Minecraft.getInstance().player.connection.sendPacket(thisPacket);
                }
                packets.clear();
                pastPositions.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
