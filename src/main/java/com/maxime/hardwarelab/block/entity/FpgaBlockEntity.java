package com.maxime.hardwarelab.block.entity;

import com.maxime.hardwarelab.logic.FpgaLut;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public final class FpgaBlockEntity extends BlockEntity {
    private FpgaLut.Mode mode = FpgaLut.Mode.AND4;
    private boolean registered;
    private boolean previousClockHigh;
    private boolean registeredOutput;

    public FpgaBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FPGA, pos, state);
    }

    public FpgaLut.Mode mode() {
        return mode;
    }

    public void cycleMode() {
        mode = mode.next();
        setChanged();
    }

    public boolean registered() {
        return registered;
    }

    public void toggleRegistered() {
        registered = !registered;
        setChanged();
    }

    public boolean registeredOutput() {
        return registeredOutput;
    }

    public void updateClock(boolean high, boolean combinationalOutput) {
        if (high && !previousClockHigh) {
            registeredOutput = combinationalOutput;
        }
        previousClockHigh = high;
        setChanged();
    }

    public boolean evaluate(int address) {
        return FpgaLut.evaluate(FpgaLut.truthTable(mode), address);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("mode", mode.ordinal());
        output.putBoolean("registered", registered);
        output.putBoolean("previousClockHigh", previousClockHigh);
        output.putBoolean("registeredOutput", registeredOutput);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        int modeIndex = Math.floorMod(input.getIntOr("mode", 0), FpgaLut.Mode.values().length);
        mode = FpgaLut.Mode.values()[modeIndex];
        registered = input.getBooleanOr("registered", false);
        previousClockHigh = input.getBooleanOr("previousClockHigh", false);
        registeredOutput = input.getBooleanOr("registeredOutput", false);
    }
}
