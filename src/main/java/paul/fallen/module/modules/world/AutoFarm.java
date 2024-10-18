package paul.fallen.module.modules.world;

import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;
import paul.fallen.utils.entity.InventoryUtils;
import paul.fallen.utils.render.RenderUtils;
import paul.fallen.utils.world.BlockUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AutoFarm extends Module {

    private final Setting range;
    private final Setting harvest;
    private final Setting plant;
    private final Setting hoe;
    private final Setting boneMeal;
    private final Setting wheat;
    private final Setting carrot;
    private final Setting potato;
    private final Setting beetroot;
    private final Setting pumpkin;
    private final Setting melon;
    private final Set<Block> selectedCropBlocks;
    private List<BlockPos> farmBlocks;
    private List<BlockPos> cropBlocks;
    private List<BlockPos> toFeedBlocks;
    private List<BlockPos> toHoeBlocks;
    private List<BlockPos> melonAndPumpkinBlocks;
    private Iterator<BlockPos> farmIterator;
    private Iterator<BlockPos> cropIterator;
    private Iterator<BlockPos> hoeIterator;
    private Iterator<BlockPos> feedIterator;
    private Iterator<BlockPos> melonPumpkinIterator;

    public AutoFarm(int bind, String name, String displayName, Category category, String description) {
        super(bind, name, displayName, category, description);

        range = new Setting("Range", this, 4, 1, 4, true);

        harvest = new Setting("AutoHarvest", this, false);
        plant = new Setting("AutoPlant", this, false);
        hoe = new Setting("AutoHoe", this, false);
        boneMeal = new Setting("AutoBoneMeal", this, false);

        wheat = new Setting("Wheat", this, false);
        carrot = new Setting("Carrot", this, false);
        potato = new Setting("Potato", this, false);
        beetroot = new Setting("Beetroot", this, false);
        pumpkin = new Setting("Pumpkin", this, false);
        melon = new Setting("Melon", this, false);

        addSetting(range);

        addSetting(harvest);
        addSetting(plant);
        addSetting(hoe);
        addSetting(boneMeal);

        addSetting(wheat);
        addSetting(carrot);
        addSetting(potato);
        addSetting(beetroot);
        addSetting(pumpkin);
        addSetting(melon);

        selectedCropBlocks = new HashSet<>();
    }

    private void updateSelectedCrops() {
        selectedCropBlocks.clear();
        if (wheat.getValBoolean()) selectedCropBlocks.add(Blocks.WHEAT);
        if (carrot.getValBoolean()) selectedCropBlocks.add(Blocks.CARROTS);
        if (potato.getValBoolean()) selectedCropBlocks.add(Blocks.POTATOES);
        if (beetroot.getValBoolean()) selectedCropBlocks.add(Blocks.BEETROOTS);
        if (pumpkin.getValBoolean()) selectedCropBlocks.add(Blocks.PUMPKIN);
        if (melon.getValBoolean()) selectedCropBlocks.add(Blocks.MELON);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END)
            return;

        try {
            updateSelectedCrops();

            BlockPos playerPos = mc.player.getPosition();

            if (hoe.getValBoolean()) {
                if (hoeIterator == null || !hoeIterator.hasNext()) {
                    toHoeBlocks = getGrassBlocks().stream()
                            .filter(this::hasSurroundingWater)
                            .sorted(Comparator.comparingDouble(blockPos -> blockPos.distanceSq(playerPos)))
                            .collect(Collectors.toList());
                    hoeIterator = toHoeBlocks.iterator();
                }

                if (hoeIterator.hasNext()) {
                    int hoeSlot = getHoeSlot();
                    if (hoeSlot != -1) {
                        InventoryUtils.setSlot(hoeSlot);
                        if (mc.player.ticksExisted % 10 == 0) {
                            BlockUtils.placeBlock(hoeIterator.next(), mc.player.inventory.currentItem, true, true);
                        }
                    }
                    return;
                }
            }

            if (plant.getValBoolean()) {
                if (farmIterator == null || !farmIterator.hasNext()) {
                    farmBlocks = getFarmBlocks().stream()
                            .filter(blockPos -> !(mc.world.getBlockState(blockPos.up()).getBlock() instanceof CropsBlock))
                            .sorted(Comparator.comparingDouble(blockPos -> blockPos.distanceSq(playerPos)))
                            .collect(Collectors.toList());
                    farmIterator = farmBlocks.iterator();
                }

                if (farmIterator.hasNext()) {
                    int seedSlot = getSeedSlot();
                    if (seedSlot != -1) {
                        InventoryUtils.setSlot(seedSlot);
                        if (mc.player.ticksExisted % 10 == 0) {
                            BlockUtils.placeBlock(farmIterator.next(), mc.player.inventory.currentItem, true, true);
                        }
                    }
                    return;
                }
            }

            if (harvest.getValBoolean()) {
                if (cropIterator == null || !cropIterator.hasNext()) {
                    cropBlocks = getCropBlocks().stream()
                            .filter(blockPos -> {
                                Block block = mc.world.getBlockState(blockPos).getBlock();
                                return block instanceof CropsBlock &&
                                        selectedCropBlocks.contains(block) &&
                                        ((CropsBlock) block).isMaxAge(mc.world.getBlockState(blockPos));
                            })
                            .sorted(Comparator.comparingDouble(blockPos -> blockPos.distanceSq(playerPos)))
                            .collect(Collectors.toList());
                    cropIterator = cropBlocks.iterator();
                }

                if (cropIterator.hasNext()) {
                    if (mc.player.ticksExisted % 10 == 0) {
                        BlockUtils.breakBlock(cropIterator.next(), mc.player.inventory.currentItem, true, true);
                    }
                    return;
                }

                if (melonPumpkinIterator == null || !melonPumpkinIterator.hasNext()) {
                    melonAndPumpkinBlocks = getMelonAndPumpkinBlocks().stream()
                            .sorted(Comparator.comparingDouble(blockPos -> blockPos.distanceSq(playerPos)))
                            .collect(Collectors.toList());
                    melonPumpkinIterator = melonAndPumpkinBlocks.iterator();
                }

                if (melonPumpkinIterator.hasNext()) {
                    if (mc.player.ticksExisted % 10 == 0) {
                        BlockUtils.breakBlock(melonPumpkinIterator.next(), mc.player.inventory.currentItem, true, true);
                    }
                    return;
                }

                if (boneMeal.getValBoolean()) {
                    if (feedIterator == null || !feedIterator.hasNext()) {
                        toFeedBlocks = getCropBlocks().stream()
                                .filter(blockPos -> {
                                    Block block = mc.world.getBlockState(blockPos).getBlock();
                                    return block instanceof CropsBlock &&
                                            selectedCropBlocks.contains(block) &&
                                            !((CropsBlock) block).isMaxAge(mc.world.getBlockState(blockPos));
                                })
                                .sorted(Comparator.comparingDouble(blockPos -> blockPos.distanceSq(playerPos)))
                                .collect(Collectors.toList());
                        feedIterator = toFeedBlocks.iterator();
                    }

                    if (feedIterator.hasNext()) {
                        int boneMealSlot = getBoneMealSlot();
                        if (boneMealSlot != -1) {
                            InventoryUtils.setSlot(boneMealSlot);
                            if (mc.player.ticksExisted % 10 == 0) {
                                BlockUtils.placeBlock(feedIterator.next(), mc.player.inventory.currentItem, true, true);
                            }
                        }
                    }
                }
            }

        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        try {
            for (BlockPos blockPos : farmBlocks) {
                RenderUtils.drawOutlinedBox(blockPos, 0, 1, 0, event);
            }
            for (BlockPos blockPos : cropBlocks) {
                RenderUtils.drawOutlinedBox(blockPos, 1, 0, 0, event);
            }
            for (BlockPos blockPos : toHoeBlocks) {
                RenderUtils.drawOutlinedBox(blockPos, 1, 1, 0, event);
            }
            for (BlockPos blockPos : melonAndPumpkinBlocks) {
                RenderUtils.drawOutlinedBox(blockPos, 1, 1, 1, event);
            }
        } catch (Exception ignored) {
        }
    }

    public List<BlockPos> getFarmBlocks() {
        int r = (int) range.getValDouble();
        return IntStream.rangeClosed(-r, r).boxed()
                .flatMap(x -> IntStream.rangeClosed(-1, 1).boxed()
                        .flatMap(y -> IntStream.rangeClosed(-r, r).mapToObj(z -> mc.player.getPosition().add(x, y, z))))
                .filter(blockPos -> mc.world.getBlockState(blockPos).getBlock() instanceof FarmlandBlock)
                .collect(Collectors.toList());
    }

    public List<BlockPos> getCropBlocks() {
        int r = (int) range.getValDouble();
        return IntStream.rangeClosed(-r, r).boxed()
                .flatMap(x -> IntStream.rangeClosed(-1, 1).boxed()
                        .flatMap(y -> IntStream.rangeClosed(-r, r).mapToObj(z -> mc.player.getPosition().add(x, y, z))))
                .filter(blockPos -> selectedCropBlocks.contains(mc.world.getBlockState(blockPos).getBlock()))
                .collect(Collectors.toList());
    }

    public List<BlockPos> getGrassBlocks() {
        int r = (int) range.getValDouble();
        return IntStream.rangeClosed(-r, r).boxed()
                .flatMap(x -> IntStream.rangeClosed(-1, 1).boxed()
                        .flatMap(y -> IntStream.rangeClosed(-r, r).mapToObj(z -> mc.player.getPosition().add(x, y, z))))
                .filter(blockPos -> mc.world.getBlockState(blockPos).getBlock() instanceof GrassBlock && mc.world.getBlockState(blockPos.up()).isAir())
                .collect(Collectors.toList());
    }

    private List<BlockPos> getMelonAndPumpkinBlocks() {
        int r = (int) range.getValDouble();
        return IntStream.rangeClosed(-r, r).boxed()
                .flatMap(x -> IntStream.rangeClosed(-1, 1).boxed()
                        .flatMap(y -> IntStream.rangeClosed(-r, r).mapToObj(z -> mc.player.getPosition().add(x, y, z))))
                .filter(blockPos -> {
                    Block block = mc.world.getBlockState(blockPos).getBlock();
                    return block == Blocks.MELON || block == Blocks.PUMPKIN;
                })
                .collect(Collectors.toList());
    }

    private int getSeedSlot() {
        return IntStream.range(0, 9)
                .filter(i -> {
                    ItemStack stack = mc.player.inventory.getStackInSlot(i);
                    return stack.getItem() instanceof BlockItem &&
                            selectedCropBlocks.contains(((BlockItem) stack.getItem()).getBlock());
                })
                .findFirst()
                .orElse(-1);
    }

    private int getBoneMealSlot() {
        return IntStream.range(0, 9)
                .filter(i -> {
                    ItemStack stack = mc.player.inventory.getStackInSlot(i);
                    return stack.getItem().equals(Items.BONE_MEAL);
                })
                .findFirst()
                .orElse(-1);
    }

    private int getHoeSlot() {
        return IntStream.range(0, 9)
                .filter(i -> {
                    ItemStack stack = mc.player.inventory.getStackInSlot(i);
                    return stack.getItem() instanceof HoeItem;
                })
                .findFirst()
                .orElse(-1);
    }

    private boolean hasSurroundingWater(BlockPos blockPos) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x == 0 && z == 0) {
                    continue;
                }
                if (isWater(blockPos.add(x, 0, z))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isWater(BlockPos blockPos) {
        return mc.world.getBlockState(blockPos).getBlock() == Blocks.WATER;
    }
}