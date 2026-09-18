package com.maxime.hardwarelab.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Arrays;

public final class Rom256BlockEntity extends BlockEntity {
    private final byte[] memory = new byte[256];
    private int pattern;

    public Rom256BlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ROM_256, pos, state);
        loadPattern(0);
    }

    public int read(int address) {
        return memory[address & 0xFF] & 0xFF;
    }

    public int pattern() {
        return pattern;
    }

    public void cyclePattern() {
        loadPattern((pattern + 1) % 4);
        setChanged();
    }

    private void loadPattern(int id) {
        pattern = id;
        Arrays.fill(memory, (byte) 0);

        switch (pattern) {
            case 0 -> {
                for (int i = 0; i < 256; i++) {
                    memory[i] = (byte) i;
                }
            }
            case 1 -> {
                for (int i = 0; i < 256; i++) {
                    memory[i] = (byte) (i ^ (i >>> 1));
                }
            }
            case 2 -> {
                for (int i = 0; i < 256; i++) {
                    memory[i] = (byte) Integer.bitCount(i);
                }
            }
            case 3 -> {
                for (int i = 0; i < 256; i++) {
                    memory[i] = (byte) ((i * 37 + 11) & 0xFF);
                }
            }
            default -> throw new AssertionError();
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("pattern", pattern);
        for (int i = 0; i < memory.length; i++) {
            output.putInt("m" + i, memory[i] & 0xFF);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        int loadedPattern = Math.floorMod(input.getIntOr("pattern", 0), 4);
        loadPattern(loadedPattern);
        for (int i = 0; i < memory.length; i++) {
            memory[i] = (byte) input.getIntOr("m" + i, memory[i] & 0xFF);
        }
    }
}
