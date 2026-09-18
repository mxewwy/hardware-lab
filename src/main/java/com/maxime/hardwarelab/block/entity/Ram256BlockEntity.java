package com.maxime.hardwarelab.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Arrays;

public final class Ram256BlockEntity extends BlockEntity {
    private final byte[] memory = new byte[256];

    public Ram256BlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RAM_256, pos, state);
    }

    public int read(int address) {
        return memory[address & 0xFF] & 0xFF;
    }

    public void write(int address, int value) {
        memory[address & 0xFF] = (byte) value;
        setChanged();
    }

    public void clear() {
        Arrays.fill(memory, (byte) 0);
        setChanged();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        for (int i = 0; i < memory.length; i++) {
            output.putInt("m" + i, memory[i] & 0xFF);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        for (int i = 0; i < memory.length; i++) {
            memory[i] = (byte) input.getIntOr("m" + i, 0);
        }
    }
}
