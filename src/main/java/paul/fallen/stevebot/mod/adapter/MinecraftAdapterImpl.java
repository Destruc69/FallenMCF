package paul.fallen.stevebot.mod.adapter;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import stevebot.core.data.blockpos.BaseBlockPos;
import stevebot.core.data.blocks.BlockWrapper;
import stevebot.core.data.items.ItemUtils;
import stevebot.core.data.items.wrapper.ItemBlockWrapper;
import stevebot.core.data.items.wrapper.ItemStackWrapper;
import stevebot.core.data.items.wrapper.ItemWrapper;
import stevebot.core.minecraft.InputBinding;
import stevebot.core.minecraft.MinecraftAdapter;
import stevebot.core.minecraft.MouseChangeInterceptor;
import stevebot.core.player.PlayerInputConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MinecraftAdapterImpl implements MinecraftAdapter {

    private MouseChangeInterceptor mouseChangeInterceptor = null;
    private final Map<String, Item> items = new HashMap<>();
    private final Map<String, Block> blocks = new HashMap<>();

    public MinecraftAdapterImpl() {
    }

    private static float getBreakDuration(ItemStack itemStack, BlockState state) {
        float blockHardness = state.getBlockHardness(null, null);
        if (blockHardness < 0) {
            return Float.MAX_VALUE;
        }
        float playerBreakSpeed = getDigSpeed(itemStack, state);
        int canHarvestMod = (itemStack != null && itemStack.canHarvestBlock(state)) ? 30 : 100;
        float dmgPerTick = ((playerBreakSpeed / blockHardness) * (1f / canHarvestMod));
        return 1f / dmgPerTick;
    }

    private Minecraft getMinecraft() {
        return Minecraft.getInstance();
    }

    private World getWorld() {
        return getMinecraft().world;
    }

    private PlayerEntity getPlayer() {
        return getMinecraft().player;
    }

    @Override
    public boolean hasPlayer() {
        return getPlayer() != null;
    }

    @Override
    public boolean isPlayerCreativeMode() {
        return getPlayer().isCreative();
    }

    @Override
    public Vector3d getPlayerHeadPosition() {
        return getPlayer().getEyePosition(1.0F);
    }

    @Override
    public Vector3d getPlayerHeadPositionXZ() {
        Vector3d posEyes = getPlayer().getEyePosition(1.0F);
        return new Vector3d(posEyes.x, posEyes.z, posEyes.z);
    }

    @SubscribeEvent
    public void onMouseChange(InputEvent.RawMouseEvent event) {
        if (mouseChangeInterceptor == null || mouseChangeInterceptor.onChange()) {
            return;
        }

        event.setCanceled(true);
    }

    @Override
    public Vector3d getPlayerPosition() {
        PlayerEntity player = getPlayer();
        if (player != null) {
            return new Vector3d(player.getPosX(), player.getPosY(), player.getPosZ());
        } else {
            return null;
        }
    }

    @Override
    public Vector3d getPlayerMotion() {
        PlayerEntity player = getPlayer();
        if (player != null) {
            return new Vector3d(player.getMotion().x, player.getMotion().y, player.getMotion().z);
        } else {
            return null;
        }
    }

    @Override
    public float getPlayerRotationYaw() {
        return getPlayer().rotationYaw;
    }

    @Override
    public float getPlayerRotationPitch() {
        return getPlayer().rotationPitch;
    }

    @Override
    public void setPlayerRotation(float yaw, float pitch) {
        PlayerEntity player = getPlayer();
        if (player != null) {
            player.rotationPitch = pitch;
            player.rotationYaw = yaw;
        }
    }

    @Override
    public void setCameraRotation(float yaw, float pitch) {
        Entity camera = getMinecraft().getRenderViewEntity();
        if (camera != null) {
            camera.rotationYaw = yaw;
            camera.rotationPitch = pitch;
            camera.prevRotationYaw = yaw;
            camera.prevRotationPitch = pitch;
        }
    }

    @Override
    public Vector3d getLookDir() {
        PlayerEntity player = getPlayer();
        if (player != null) {
            Vector3d lookDir = player.getLookVec();
            return new Vector3d(lookDir.x, lookDir.y, lookDir.z);
        } else {
            return null;
        }
    }

    @Override
    public void setMouseChangeInterceptor(MouseChangeInterceptor interceptor) {
        this.mouseChangeInterceptor = interceptor;
    }

    @Override
    public BaseBlockPos getPlayerBlockPosition() {
        PlayerEntity player = getPlayer();
        if (player != null) {
            return new BaseBlockPos(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());
        } else {
            return null;
        }
    }

    @Override
    public float getMouseSensitivity() {
        return (float) getMinecraft().gameSettings.mouseSensitivity;
    }

    @Override
    public double getMouseDX() {
        return Minecraft.getInstance().mouseHelper.getXVelocity();
    }

    @Override
    public double getMouseDY() {
        return Minecraft.getInstance().mouseHelper.getYVelocity();
    }

    @Override
    public void setPlayerSprinting(final boolean sprint) {
        PlayerEntity player = getPlayer();
        if (player != null) {
            player.setSprinting(sprint);
        }
    }

    @Override
    public InputBinding getKeyBinding(final PlayerInputConfig.InputType inputType) {
        GameSettings settings = getMinecraft().gameSettings;
        switch (inputType) {
            case WALK_FORWARD:
                return new McInputBinding(settings.keyBindForward);
            case WALK_BACKWARD:
                return new McInputBinding(settings.keyBindBack);
            case WALK_LEFT:
                return new McInputBinding(settings.keyBindLeft);
            case WALK_RIGHT:
                return new McInputBinding(settings.keyBindRight);
            case SPRINT:
                return new McInputBinding(settings.keyBindSprint);
            case SNEAK:
                return new McInputBinding(settings.keyBindSneak);
            case JUMP:
                return new McInputBinding(settings.keyBindJump);
            case PLACE_BLOCK:
                return new McInputBinding(settings.keyBindUseItem);
            case BREAK_BLOCK:
                return new McInputBinding(settings.keyBindAttack);
            case INTERACT:
                return new McInputBinding(settings.keyBindUseItem);
        }
        return null;
    }

    @Override
    public void setInput(final int keyCode, final boolean down) {
        KeyBinding.setKeyBindState(InputMappings.getInputByCode(keyCode, 0), down);
        if (down) {
            KeyBinding.onTick(InputMappings.getInputByCode(keyCode, 0));
        }
    }

    @Override
    public float getPlayerHealth() {
        PlayerEntity player = getPlayer();
        return player.getHealth();
    }

    @Override
    public void sendMessage(final String msg) {
        PlayerEntity player = getPlayer();
        if (player != null) {
            player.sendMessage(new StringTextComponent(msg), player.getUniqueID());
        }
    }

    @Override
    public List<ItemStackWrapper> getHotbarItems() {
        List<ItemStackWrapper> itemList = new ArrayList<>();
        PlayerInventory inventory = getPlayer().inventory;
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = inventory.getStackInSlot(i);
            if (!itemStack.isEmpty()) {
                int itemId = Item.getIdFromItem(itemStack.getItem());
                itemList.add(new ItemStackWrapper(ItemUtils.getItemLibrary().getItemById(itemId), itemStack.getCount(), i));
            }
        }
        return itemList;
    }

    @Override
    public void selectHotbarSlot(int slot) {
        PlayerInventory inventory = getPlayer().inventory;
        inventory.currentItem = slot;
    }

    @Override
    public boolean isPlayerOnGround() {
        PlayerEntity player = getPlayer();
        return player.isOnGround();
    }

    @Override
    public List<ItemWrapper> getItems() {
        this.items.clear();
        List<ItemWrapper> itemList = new ArrayList<>();
        for (Item item : ForgeRegistries.ITEMS) {
            String name = item.getRegistryName().toString();
            int id = Item.getIdFromItem(item);
            itemList.add(new ItemWrapper(id, name));
            this.items.put(name, item);
        }
        return itemList;
    }

    @Override
    public List<BlockWrapper> getBlocks() {
        this.blocks.clear();
        List<BlockWrapper> blockList = new ArrayList<>();
        for (Block block : ForgeRegistries.BLOCKS) {
            String name = block.getRegistryName().toString();
            int id = Integer.parseInt(String.valueOf(ForgeRegistries.BLOCKS.getKey(block)));
            boolean isNormalCube = block.getDefaultState().getMaterial().isSolid();
            blockList.add(new BlockWrapper(id, name, isNormalCube));
            this.blocks.put(name, block);
        }
        return blockList;
    }

    @Override
    public int getBlockId(final BaseBlockPos pos) {
        BlockState blockState = getWorld().getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ()));
        Block block = blockState.getBlock();
        return Integer.parseInt(String.valueOf(ForgeRegistries.BLOCKS.getKey(block)));
    }

    @Override
    public boolean isChunkLoaded(int chunkX, int chunkZ) {
        return getWorld().getChunkProvider().isChunkLoaded(new ChunkPos(chunkX, chunkZ));
    }

    @Override
    public int getItemIdFromBlock(final BlockWrapper block) {
        Item itemFromBlock = Item.getItemFromBlock(blocks.get(block.getName()));
        return Item.getIdFromItem(itemFromBlock);
    }

    @Override
    public int getBlockIdFromItem(final ItemBlockWrapper item) {
        BlockItem itemBlock = (BlockItem) items.get(item.getName());
        return Integer.parseInt(String.valueOf(itemBlock.getBlock()));
    }

    @Override
    public String getBlockFacing(final BaseBlockPos position) {
        BlockState blockState = getWorld().getBlockState(new BlockPos(position.getX(), position.getY(), position.getZ()));
        Direction facing = blockState.get(BlockStateProperties.HORIZONTAL_FACING);
        return facing.getName2();
    }

    @Override
    public boolean isDoorOpen(final BaseBlockPos position) {
        BlockState blockState = getWorld().getBlockState(new BlockPos(position.getX(), position.getY(), position.getZ()));
        return blockState.get(BlockStateProperties.OPEN);
    }

    @Override
    public boolean isBlockPassable(final BlockWrapper block, final BaseBlockPos pos) {
        return blocks.get(block.getName()).getDefaultState().getMaterial().isReplaceable();
    }

    @Override
    public float getBreakDuration(ItemWrapper item, BlockWrapper block) {
        Block mcBlock = blocks.get(block.getName());
        if (item == null) {
            return getBreakDuration(ItemStack.EMPTY, mcBlock.getDefaultState());
        } else {
            Item mcItem = items.get(item.getName());
            return getBreakDuration(new ItemStack(mcItem), mcBlock.getDefaultState());
        }
    }

    private static float getDigSpeed(ItemStack itemStack, BlockState state) {
        return getDigSpeed(itemStack, state, false, 0, false, 0, 0, false, false, true);
    }

    private static float getDigSpeed(
            ItemStack itemStack,
            BlockState state,
            boolean hasEffectHaste,
            int effectHasteAmplifier,
            boolean hasEffectMiningFatigue,
            int effectMiningFatigueAmplifier,
            int efficiencyModifier,
            boolean aquaAffinityModifier,
            boolean isInsideWater,
            boolean isOnGround
    ) {
        float f = getDestroySpeed(itemStack, state);
        if (f > 1.0F) {
            int i = efficiencyModifier;
            if (i > 0 && (itemStack != null && !itemStack.isEmpty())) {
                f += (float) (i * i + 1);
            }
        }
        if (hasEffectHaste) {
            f *= 1.0F + (float) (effectHasteAmplifier + 1) * 0.2F;
        }
        if (hasEffectMiningFatigue) {
            float f1;
            switch (effectMiningFatigueAmplifier) {
                case 0:
                    f1 = 0.3F;
                    break;
                case 1:
                    f1 = 0.09F;
                    break;
                case 2:
                    f1 = 0.0027F;
                    break;
                case 3:
                default:
                    f1 = 8.1E-4F;
            }
            f *= f1;
        }
        if (isInsideWater && !aquaAffinityModifier) {
            f /= 5.0F;
        }
        if (!isOnGround) {
            f /= 5.0F;
        }
        return (f < 0 ? 0 : f);
    }

    private static float getDestroySpeed(ItemStack itemStack, BlockState state) {
        float f = 1.0F;
        if (itemStack != null && !itemStack.isEmpty()) {
            f *= itemStack.getDestroySpeed(state);
        }
        return f;
    }
}