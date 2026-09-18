package com.maxime.hardwarelab.block.entity;

import com.maxime.hardwarelab.logic.BusWidth;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public final class BusSplitterBlockEntity extends BlockEntity {
    private BusWidth width = BusWidth.BITS_8;
    private int bank;

    public BusSplitterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BUS_SPLITTER, pos, state);
    }

    public BusWidth width() {
        return width;
    }

    public int bank() {
        return bank;
    }

    public void cycleWidth() {
        width = width.next();
        bank %= width.nibbleCount();
        setChanged();
    }

    public void cycleBank() {
        bank = (bank + 1) % width.nibbleCount();
        setChanged();
    }

    public int firstBit() {
        return bank * 4;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("width", width.ordinal());
        output.putInt("bank", bank);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        width = BusWidth.fromIndex(input.getIntOr("width", BusWidth.BITS_8.ordinal()));
        bank = Math.floorMod(input.getIntOr("bank", 0), width.nibbleCount());
    }
}
