package com.maxime.hardwarelab.block.entity;

import com.maxime.hardwarelab.logic.BusSignal;
import com.maxime.hardwarelab.logic.BusWidth;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public final class EightBitRegisterBlockEntity extends BlockEntity {
    private int value;
    private boolean previousClockHigh;

    public EightBitRegisterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EIGHT_BIT_REGISTER, pos, state);
    }

    public boolean capture(boolean clockHigh, BusSignal input) {
        boolean rising = clockHigh && !previousClockHigh;
        previousClockHigh = clockHigh;

        if (!rising || input == null) {
            setChanged();
            return false;
        }

        int next = input.resized(BusWidth.BITS_8).value();
        if (next == value) {
            setChanged();
            return false;
        }

        value = next;
        setChanged();
        return true;
    }

    public int value() {
        return value;
    }

    public boolean previousClockHigh() {
        return previousClockHigh;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("value", value);
        output.putBoolean("previousClockHigh", previousClockHigh);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        value = input.getIntOr("value", 0) & 0xFF;
        previousClockHigh = input.getBooleanOr("previousClockHigh", false);
    }
}
