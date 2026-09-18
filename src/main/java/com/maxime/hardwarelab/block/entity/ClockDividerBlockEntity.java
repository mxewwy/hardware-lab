package com.maxime.hardwarelab.block.entity;

import com.maxime.hardwarelab.logic.ClockDividerLogic;
import com.maxime.hardwarelab.logic.Signal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class ClockDividerBlockEntity extends BlockEntity {
    private final ClockDividerLogic logic = new ClockDividerLogic();

    public ClockDividerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CLOCK_DIVIDER, pos, state);
    }

    public boolean updateInput(boolean inputHigh) {
        boolean changed = logic.update(Signal.of(inputHigh));
        setChanged();
        return changed;
    }

    public void setDivision(int division) {
        logic.setDivision(division);
        setChanged();
    }

    public int division() {
        return logic.division();
    }

    public int risingEdges() {
        return logic.risingEdges();
    }

    public boolean previousInputHigh() {
        return logic.previousInputHigh();
    }

    public boolean outputHigh() {
        return logic.outputHigh();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("division", logic.division());
        output.putInt("risingEdges", logic.risingEdges());
        output.putBoolean("previousInputHigh", logic.previousInputHigh());
        output.putBoolean("outputHigh", logic.outputHigh());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        logic.restore(
                input.getIntOr("division", 2),
                input.getIntOr("risingEdges", 0),
                input.getBooleanOr("previousInputHigh", false),
                input.getBooleanOr("outputHigh", false)
        );
    }
}
