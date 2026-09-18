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

        if (output.getBusOutputDirection(state) != expectedDirection) {
            return null;
        }

        if (!output.isBusDriving(level, pos, state)) {
            return null;
        }

        return output.getBusOutput(level, pos, state);
    }
}
