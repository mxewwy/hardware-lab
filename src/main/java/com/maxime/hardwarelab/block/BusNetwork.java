package com.maxime.hardwarelab.block;

import com.maxime.hardwarelab.logic.BusSignal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public final class BusNetwork {
    private BusNetwork() {
    }

    public static BusSignal readOutput(
            BlockGetter level,
            BlockPos pos,
            Direction expectedDirection
    ) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof BusOutputBlock output)) {
            return null;
        }

        Direction facing = outputDirection(state.getBlock(), state);
        if (facing != expectedDirection) {
            return null;
        }

        return output.getBusOutput(level, pos, state);
    }

    private static Direction outputDirection(
            net.minecraft.world.level.block.Block block,
            BlockState state
    ) {
        if (block instanceof DigitalBusBlock) {
            return state.getValue(DigitalBusBlock.FACING);
        }

        if (block instanceof BusMuxBlock) {
            return state.getValue(BusMuxBlock.FACING);
        }

        return Direction.NORTH;
    }
}
