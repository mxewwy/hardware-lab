package com.maxime.hardwarelab.block.entity;

import com.maxime.hardwarelab.logic.BusSignal;
import com.maxime.hardwarelab.logic.BusWidth;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public final class SevenSegmentDisplayBlockEntity extends BlockEntity {
    private int value;

    public SevenSegmentDisplayBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SEVEN_SEGMENT_DISPLAY, pos, state);
    }

    public boolean update(BusSignal input) {
        int next = input == null ? 0 : input.resized(BusWidth.BITS_4).value();
        if (next == value) {
            return false;
        }
        value = next;
        setChanged();
        return true;
    }

    public int value() {
        return value;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("value", value);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        value = input.getIntOr("value", 0) & 0xF;
    }
}
