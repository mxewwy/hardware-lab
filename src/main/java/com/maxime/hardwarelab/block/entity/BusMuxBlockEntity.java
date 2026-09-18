package com.maxime.hardwarelab.block.entity;

import com.maxime.hardwarelab.logic.BusWidth;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public final class BusMuxBlockEntity extends BlockEntity {
    private BusWidth width = BusWidth.BITS_8;

    public BusMuxBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BUS_MUX, pos, state);
    }

    public BusWidth width() {
        return width;
    }

    public void cycleWidth() {
        width = width.next();
        setChanged();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("width", width.ordinal());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        width = BusWidth.fromIndex(input.getIntOr("width", BusWidth.BITS_8.ordinal()));
    }
}
