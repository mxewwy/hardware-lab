package com.maxime.hardwarelab.block;

import com.maxime.hardwarelab.logic.BusSignal;
import com.maxime.hardwarelab.logic.BusWidth;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public final class BusDriverBlock extends HorizontalDirectionalBlock implements BusOutputBlock {
    public static final MapCodec<BusDriverBlock> CODEC = simpleCodec(BusDriverBlock::new);

    public BusDriverBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BusSignal getBusOutput(BlockGetter level, BlockPos pos, BlockState state) {
        Direction facing = state.getValue(FACING);
        BusSignal input = BusNetwork.readOutput(level, pos.relative(facing.getOpposite()), facing);
        return input == null ? BusSignal.zero(BusWidth.BITS_8) : input.resized(BusWidth.BITS_8);
    }

    @Override
    public Direction getBusOutputDirection(BlockState state) {
        return state.getValue(FACING);
    }

    @Override
    public boolean isBusDriving(BlockGetter level, BlockPos pos, BlockState state) {
        Direction facing = state.getValue(FACING);
        BlockPos enablePos = pos.relative(facing.getClockWise());
        BlockState enableState = level.getBlockState(enablePos);
        return enableState.getSignal(level, enablePos, facing.getCounterClockWise()) > 0;
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return direction == state.getValue(FACING)
                && getBusOutput(level, pos, state).value() != 0
                && isBusDriving(level, pos, state) ? 15 : 0;
    }

    @Override
    protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return getSignal(state, level, pos, direction);
    }
    @Override
    protected net.minecraft.world.phys.shapes.VoxelShape getShape(
            net.minecraft.world.level.block.state.BlockState state,
            net.minecraft.world.level.BlockGetter level,
            net.minecraft.core.BlockPos pos,
            net.minecraft.world.phys.shapes.CollisionContext context) {
        return HardwareShapes.COMPONENT;
    }

    @Override
    protected net.minecraft.world.phys.shapes.VoxelShape getCollisionShape(
            net.minecraft.world.level.block.state.BlockState state,
            net.minecraft.world.level.BlockGetter level,
            net.minecraft.core.BlockPos pos,
            net.minecraft.world.phys.shapes.CollisionContext context) {
        return HardwareShapes.COMPONENT;
    }

}
