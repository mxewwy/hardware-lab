package com.maxime.hardwarelab.block.entity;

import com.maxime.hardwarelab.logic.BusSignal;
import com.maxime.hardwarelab.logic.BusWidth;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public final class LedMatrixBlockEntity extends BlockEntity {
    private int row;
    private int value;

    public LedMatrixBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LED_MATRIX, pos, state);
    }

    public boolean update(BusSignal input) {
        int next = input == null ? 0 : input.resized(BusWidth.BITS_8).value();
        if (next == value) {
            return false;
        }
        value = next;
        setChanged();
        return true;
    }

    public void nextRow() {
        row = (row + 1) & 7;
        setChanged();
    }

    public int row() {
        return row;
    }

    public int value() {
        return value;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("row", row);
        output.putInt("value", value);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        row = input.getIntOr("row", 0) & 7;
        value = input.getIntOr("value", 0) & 0xFF;
    }
}
