package com.maxime.hardwarelab.block.entity;

import com.maxime.hardwarelab.logic.DFlipFlopLogic;
import com.maxime.hardwarelab.logic.Signal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public final class DFlipFlopBlockEntity extends BlockEntity {
    private final DFlipFlopLogic logic = new DFlipFlopLogic();

    public DFlipFlopBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.D_FLIP_FLOP, pos, state);
    }

    public boolean updateInputs(boolean dataHigh, boolean clockHigh) {
        boolean changed = logic.update(
                Signal.of(dataHigh),
                Signal.of(clockHigh)
        );
        setChanged();
        return changed;
    }

    public boolean previousClockHigh() {
        return logic.previousClockHigh();
    }

    public boolean q() {
        return logic.q();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("previousClockHigh", logic.previousClockHigh());
        output.putBoolean("q", logic.q());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        logic.restore(
                input.getBooleanOr("previousClockHigh", false),
                input.getBooleanOr("q", false)
        );
    }
}
