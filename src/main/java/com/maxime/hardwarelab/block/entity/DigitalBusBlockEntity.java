package com.maxime.hardwarelab.block.entity;

import com.maxime.hardwarelab.logic.BusSignal;
import com.maxime.hardwarelab.logic.BusWidth;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public final class DigitalBusBlockEntity extends BlockEntity {
    private BusWidth width = BusWidth.BITS_8;
    private int manualValue;

    public DigitalBusBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DIGITAL_BUS, pos, state);
    }

    public BusWidth width() {
        return width;
    }

    public int manualValue() {
        return manualValue;
    }

    public BusSignal manualSignal() {
        return BusSignal.of(width, manualValue);
    }

    public void cycleWidth() {
        width = width.next();
        manualValue &= width.mask();
        setChanged();
    }

    public void setManualValue(int value) {
        manualValue = value & width.mask();
        setChanged();
    }

    public void cycleTestPattern() {
        int mask = width.mask();
        int[] patterns = {
                0,
                1,
                0xF & mask,
                0x55 & mask,
                0xAA & mask,
                mask
        };

        int current = manualValue & mask;
        int nextIndex = 0;

        for (int i = 0; i < patterns.length; i++) {
            if (patterns[i] == current) {
                nextIndex = (i + 1) % patterns.length;
                break;
            }
        }

        setManualValue(patterns[nextIndex]);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("width", width.ordinal());
        output.putInt("manualValue", manualValue);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        width = BusWidth.fromIndex(input.getIntOr("width", BusWidth.BITS_8.ordinal()));
        manualValue = input.getIntOr("manualValue", 0) & width.mask();
    }
}
