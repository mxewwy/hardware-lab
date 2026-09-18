package com.maxime.hardwarelab.block;

import com.maxime.hardwarelab.logic.BusSignal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface BusOutputBlock {
    BusSignal getBusOutput(BlockGetter level, BlockPos pos, BlockState state);

    Direction getBusOutputDirection(BlockState state);
}
