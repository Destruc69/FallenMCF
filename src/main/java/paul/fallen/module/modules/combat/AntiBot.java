/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.combat;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;
import paul.fallen.utils.client.ClientUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class AntiBot extends Module {

    private static final double IMPOSSIBLE_MOTION_THRESHOLD = 2.035;
    private static final double IMPOSSIBLE_MOTION_Y_THRESHOLD = 0.407;

    private final Setting impMotionCheck;
    private final Setting healthCheck;
    private final Setting immuneCheck;
    private final Setting impMotionY;
    private final Setting inGroundCheck;
    private final List<PlayerEntity> bots = new ArrayList<>();

    public AntiBot(int bind, String name, String displayName, Category category, String description) {
        super(bind, name, displayName, category, description);

        impMotionCheck = new Setting("ImpossibleMotionCheck", this, false);
        healthCheck = new Setting("HealthCheck", this, false);
        immuneCheck = new Setting("ImmuneCheck", this, false);
        impMotionY = new Setting("ImpossibleMotionYCheck", this, false);
        inGroundCheck = new Setting("InGroundCheck", this, false);

        addSetting(impMotionCheck);
        addSetting(healthCheck);
        addSetting(immuneCheck);
        addSetting(impMotionY);
        addSetting(inGroundCheck);
    }

    @Override
    public void onDisable() {
        bots.clear();
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (mc.world == null) return;

        // Remove all bots from the world
        bots.forEach(bot -> mc.world.removeEntityFromWorld(bot.getEntityId()));
        bots.clear();

        List<Entity> entities = (List<Entity>) mc.world.getAllEntities();
        List<PlayerEntity> players = entities.stream()
                .filter(PlayerEntity.class::isInstance)
                .map(PlayerEntity.class::cast)
                .filter(player -> player != mc.player)
                .collect(Collectors.toList());

        for (PlayerEntity player : players) {
            boolean isBot = false;
            String reason = "";

            if (impMotionCheck.getValBoolean() && isImpossibleMotion(player)) {
                isBot = true;
                reason = "ImpossibleMotion";
            } else if (healthCheck.getValBoolean() && isHealthCheck(player)) {
                isBot = true;
                reason = "HealthCheck";
            } else if (immuneCheck.getValBoolean() && isImmuneCheck(player)) {
                isBot = true;
                reason = "ImmuneCheck";
            } else if (impMotionY.getValBoolean() && isImpossibleMotionY(player)) {
                isBot = true;
                reason = "ImpossibleMotionY";
            } else if (inGroundCheck.getValBoolean() && isInGroundCheck(player)) {
                isBot = true;
                reason = "InGroundCheck";
            }

            if (isBot) {
                bots.add(player);
                ClientUtils.addChatMessage("[AB] We removed a bot: " + player.getName() + " for " + reason);
            }
        }
    }

    private boolean isImpossibleMotion(PlayerEntity player) {
        double x = player.getMotion().x;
        double y = player.getMotion().y;
        double z = player.getMotion().z;
        return x > IMPOSSIBLE_MOTION_THRESHOLD || x < -IMPOSSIBLE_MOTION_THRESHOLD ||
                y > IMPOSSIBLE_MOTION_Y_THRESHOLD || y < -IMPOSSIBLE_MOTION_Y_THRESHOLD ||
                z > IMPOSSIBLE_MOTION_THRESHOLD || z < -IMPOSSIBLE_MOTION_THRESHOLD;
    }

    private boolean isHealthCheck(PlayerEntity player) {
        return player.ticksExisted < 1 && player.getHealth() <= 19;
    }

    private boolean isImmuneCheck(PlayerEntity player) {
        return player.isInvisible() || player.isImmuneToFire() || player.isImmuneToExplosions();
    }

    private boolean isImpossibleMotionY(PlayerEntity player) {
        return player.isAirBorne && player.fallDistance > 1 && player.getMotion().y > 0;
    }

    private boolean isInGroundCheck(PlayerEntity player) {
        BlockPos leg = new BlockPos(player.getPosX(), player.getPosY(), player.getPosZ());
        BlockPos head = new BlockPos(player.getPosX(), player.getPosY() + 1, player.getPosZ());
        return !mc.world.getBlockState(leg).getBlock().equals(Blocks.AIR) ||
                !mc.world.getBlockState(head).getBlock().equals(Blocks.AIR);
    }
}
