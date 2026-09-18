package com.maxime.hardwarelab.block.entity;

import com.maxime.hardwarelab.logic.BusSignal;
import com.maxime.hardwarelab.logic.BusWidth;
import com.maxime.hardwarelab.logic.TinyCpu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public final class CpuBlockEntity extends BlockEntity {
    private final TinyCpu cpu = new TinyCpu();
    private int ticksPerInstruction = 2;
    private int tickCounter;

    public CpuBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CPU, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CpuBlockEntity entity) {
        if (level.isClientSide() || entity.cpu.halted()) {
            return;
        }

        entity.tickCounter++;
        if (entity.tickCounter < entity.ticksPerInstruction) {
            return;
        }

        entity.tickCounter = 0;
        int before = entity.cpu.output();
        entity.cpu.step();

        if (before != entity.cpu.output() || entity.cpu.halted()) {
            level.updateNeighborsAt(pos, state.getBlock());
        }

        entity.setChanged();
    }

    public TinyCpu cpu() {
        return cpu;
    }

    public int ticksPerInstruction() {
        return ticksPerInstruction;
    }

    public void cycleSpeed() {
        ticksPerInstruction = switch (ticksPerInstruction) {
            case 1 -> 2;
            case 2 -> 4;
            default -> 1;
        };
        setChanged();
    }

    public void reset() {
        cpu.reset();
        tickCounter = 0;
        setChanged();
    }

    public void cycleProgram() {
        cpu.loadProgram((cpu.programId() + 1) & 3);
        tickCounter = 0;
        setChanged();
    }

    public BusSignal outputSignal() {
        return BusSignal.of(BusWidth.BITS_8, cpu.output());
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("ticksPerInstruction", ticksPerInstruction);
        output.putInt("tickCounter", tickCounter);
        output.putInt("programId", cpu.programId());
        output.putInt("a", cpu.a());
        output.putInt("b", cpu.b());
        output.putInt("pc", cpu.pc());
        output.putBoolean("zero", cpu.zero());
        output.putBoolean("carry", cpu.carry());
        output.putBoolean("halted", cpu.halted());
        output.putInt("outputValue", cpu.output());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ticksPerInstruction = switch (input.getIntOr("ticksPerInstruction", 2)) {
            case 1, 2, 4 -> input.getIntOr("ticksPerInstruction", 2);
            default -> 2;
        };
        tickCounter = input.getIntOr("tickCounter", 0);
        cpu.loadProgram(Math.floorMod(input.getIntOr("programId", 0), 4));
        cpu.restore(
                input.getIntOr("a", 0),
                input.getIntOr("b", 0),
                input.getIntOr("pc", 0),
                input.getBooleanOr("zero", false),
                input.getBooleanOr("carry", false),
                input.getBooleanOr("halted", false),
                input.getIntOr("outputValue", 0)
        );
    }
}
