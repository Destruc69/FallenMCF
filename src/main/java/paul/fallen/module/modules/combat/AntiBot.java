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
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.setting.Setting;
import paul.fallen.utils.client.ClientUtils;

import java.util.ArrayList;
import java.util.List;

public final class AntiBot extends Module {

    public static List<PlayerEntity> list;
    private final Setting impMotionCheck;
    private final Setting healthCheck;
    private final Setting immuneCheck;
    private final Setting impMotionY;
    private final Setting inGroundCheck;
    ArrayList<PlayerEntity> bots = new ArrayList<>();

    public AntiBot(int bind, String name, String displayName, Module.Category category) {
        super(bind, name, displayName, category);

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
        try {
            for (PlayerEntity theBots : bots) {
                assert mc.world != null;
                mc.world.removeEntityFromWorld(theBots.getEntityId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            assert mc.world != null;
            for (Entity e : mc.world.getAllEntities()) {
                if (e instanceof PlayerEntity) {
                    if (e != mc.player) {
                        if (!(bots.contains(e))) {
                            if (impMotionCheck.bval) {
                                if (e.getMotion().x > 2.035 || e.getMotion().x < -2.035 || e.getMotion().y > 0.407 || e.getMotion().y < -0.407 || e.getMotion().z > 2.035 || e.getMotion().z < -2.035) {
                                    if (((PlayerEntity) e).isPotionActive(Effects.SPEED) || ((PlayerEntity) e).isPotionActive(Effects.JUMP_BOOST))
                                        return;
                                    bots.add((PlayerEntity) e);
                                    ClientUtils.addChatMessage("[AB] We removed a bot:" + " " + e.getName() + " " + "for ImpossibleMotion");
                                }
                            }
                            if (healthCheck.bval) {
                                if (e.ticksExisted < 1 && ((PlayerEntity) e).getHealth() <= 19) {
                                    bots.add((PlayerEntity) e);
                                    ClientUtils.addChatMessage("[AB] We removed a bot:" + " " + e.getName() + " " + "for HeathCheck");
                                }
                            }
                            if (immuneCheck.bval) {
                                if (e.isInvisible() || e.isImmuneToFire() || e.isImmuneToExplosions()) {
                                    bots.add((PlayerEntity) e);
                                    ClientUtils.addChatMessage("[AB] We removed a bot:" + " " + e.getName());
                                }
                            }
                            if (impMotionY.bval) {
                                if (e.isAirBorne && e.fallDistance > 1 && e.getMotion().y > 0) {
                                    bots.add((PlayerEntity) e);
                                    ClientUtils.addChatMessage("[AB] We removed a bot:" + " " + e.getName());
                                }
                            }
                            if (inGroundCheck.bval) {
                                BlockPos leg = new BlockPos(e.getPosX(), e.getPosY(), e.getPosZ());
                                BlockPos head = new BlockPos(e.getPosX(), e.getPosY() + 1, e.getPosZ());
                                if (!mc.world.getBlockState(leg).getBlock().equals(Blocks.AIR)) {
                                    bots.add((PlayerEntity) e);
                                    ClientUtils.addChatMessage("[AB] We removed a bot:" + " " + e.getName());
                                }
                                if (!mc.world.getBlockState(head).getBlock().equals(Blocks.AIR)) {
                                    bots.add((PlayerEntity) e);
                                    ClientUtils.addChatMessage("[AB] We removed a bot:" + " " + e.getName());
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
    }
}
