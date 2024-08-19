package paul.fallen.module.modules.render;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class XRay extends Module {

    private final Set<BlockPos> currentPoses = ConcurrentHashMap.newKeySet();
    private final ConcurrentHashMap<BlockPos, BlockState> originalBlockStates = new ConcurrentHashMap<>();

    public XRay(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @SubscribeEvent
    public void tick(TickEvent.PlayerTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.world == null || mc.player == null) return; // Check for null values

        // Get all loaded ore blocks
        Set<BlockPos> oreBlocks = getAllLoadedOreBlockPos(mc);

        // Add new ore blocks to currentPoses
        for (BlockPos orePos : oreBlocks) {
            if (!currentPoses.contains(orePos)) {
                createTunnel(mc, orePos);
            }
        }

        // Remove blocks outside render distance or non-ore blocks
        Iterator<BlockPos> iterator = currentPoses.iterator();
        while (iterator.hasNext()) {
            BlockPos pos = iterator.next();
            if (mc.player.getDistanceSq(pos.getX(), pos.getY(), pos.getZ()) > mc.gameSettings.renderDistanceChunks * 16 * 16) {
                iterator.remove();
            } else {
                BlockState blockState = mc.world.getBlockState(pos);
                if (!isOre(blockState.getBlock())) {
                    iterator.remove();
                }
            }
        }

        // Update block states for the tunnels
        for (BlockPos blockPos : currentPoses) {
            BlockState blockState = mc.world.getBlockState(blockPos);

            if (blockState.getBlock() != Blocks.AIR) {
                boolean isFacing = isPlayerFacingBlock(mc, blockPos);
                boolean isStanding = mc.player.getPosition().equals(blockPos);

                if (!isFacing && !isStanding) {
                    if (blockState.getBlock() != Blocks.BARRIER) {
                        originalBlockStates.putIfAbsent(blockPos, blockState);
                        mc.world.setBlockState(blockPos, Blocks.BARRIER.getDefaultState());
                    }
                } else {
                    if (blockState.getBlock() == Blocks.BARRIER) {
                        BlockState originalState = originalBlockStates.remove(blockPos);
                        if (originalState != null) {
                            mc.world.setBlockState(blockPos, originalState);
                        }
                    }
                }
            }
        }
    }

    private Set<BlockPos> getAllLoadedOreBlockPos(Minecraft mc) {
        Set<BlockPos> positions = new HashSet<>();
        int renderDistance = mc.gameSettings.renderDistanceChunks;
        BlockPos playerPos = mc.player.getPosition();
        int playerChunkX = playerPos.getX() >> 4;
        int playerChunkZ = playerPos.getZ() >> 4;

        for (int dx = -renderDistance; dx <= renderDistance; dx++) {
            for (int dz = -renderDistance; dz <= renderDistance; dz++) {
                ChunkPos chunkPos = new ChunkPos(playerChunkX + dx, playerChunkZ + dz);
                Chunk chunk = mc.world.getChunk(chunkPos.x, chunkPos.z);
                if (chunk != null) {
                    int minX = chunkPos.getXStart();
                    int maxX = chunkPos.getXEnd();
                    int minZ = chunkPos.getZStart();
                    int maxZ = chunkPos.getZEnd();
                    int maxY = mc.world.getHeight() - 1;

                    for (int x = minX; x <= maxX; x++) {
                        for (int z = minZ; z <= maxZ; z++) {
                            for (int y = 0; y <= maxY; y++) {
                                BlockPos pos = new BlockPos(x, y, z);
                                Block block = chunk.getBlockState(pos).getBlock();
                                if (isOre(block)) {
                                    positions.add(pos);
                                }
                            }
                        }
                    }
                }
            }
        }
        return positions;
    }

    private boolean isOre(Block block) {
        return block == Blocks.COAL_ORE ||
                block == Blocks.IRON_ORE ||
                block == Blocks.GOLD_ORE ||
                block == Blocks.DIAMOND_ORE ||
                block == Blocks.LAPIS_ORE ||
                block == Blocks.REDSTONE_ORE ||
                block == Blocks.EMERALD_ORE ||
                block == Blocks.NETHER_QUARTZ_ORE ||
                block == Blocks.NETHER_GOLD_ORE;
    }

    private void createTunnel(Minecraft mc, BlockPos orePos) {
        BlockPos playerPos = mc.player.getPosition();

        int dx = Integer.compare(orePos.getX(), playerPos.getX());
        int dy = Integer.compare(orePos.getY(), playerPos.getY());
        int dz = Integer.compare(orePos.getZ(), playerPos.getZ());

        BlockPos currentPos = playerPos;
        while (!currentPos.equals(orePos)) {
            currentPoses.add(currentPos);
            currentPos = currentPos.add(dx, dy, dz);
        }
        currentPoses.add(orePos); // Ensure the ore block itself is also added
    }

    private boolean isPlayerFacingBlock(Minecraft mc, BlockPos blockPos) {
        Vector3d playerLook = mc.player.getLook(1.0F).normalize();
        Vector3d blockCenter = new Vector3d(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
        Vector3d playerEyes = mc.player.getEyePosition(1.0F);
        Vector3d vecToBlock = blockCenter.subtract(playerEyes).normalize();
        double dotProduct = playerLook.dotProduct(vecToBlock);

        return dotProduct > 0.99;
    }
}