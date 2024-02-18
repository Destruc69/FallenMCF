package paul.fallen.events;

import net.minecraft.block.Block;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.eventbus.api.Event;

public class BoundingBoxEvent extends Event
{
    private Block block;
    private BlockPos blockPos;
    private AxisAlignedBB boundingBox;

    public BoundingBoxEvent(final Block block, final BlockPos pos, final AxisAlignedBB boundingBox) {
        this.block = block;
        this.blockPos = pos;
        this.boundingBox = boundingBox;
    }

    public Block getBlock() {
        return block;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public AxisAlignedBB getBoundingBox() {
        return boundingBox;
    }

    public void setBlock(final Block block) {
        this.block = block;
    }

    public void setBlockPos(final BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public void setBoundingBox(final AxisAlignedBB boundingBox) {
        this.boundingBox = boundingBox;
    }
}