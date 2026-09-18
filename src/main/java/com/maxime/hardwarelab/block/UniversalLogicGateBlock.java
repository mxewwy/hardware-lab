package com.maxime.hardwarelab.block;

import com.maxime.hardwarelab.HardwareLabLanguage;
import com.maxime.hardwarelab.logic.GateType;
import com.maxime.hardwarelab.logic.Signal;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class UniversalLogicGateBlock extends HorizontalDirectionalBlock {
    public static final EnumProperty<GateType> GATE_TYPE =
            EnumProperty.create("gate_type", GateType.class);

    public static final MapCodec<UniversalLogicGateBlock> CODEC =
            simpleCodec(UniversalLogicGateBlock::new);

    public UniversalLogicGateBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(GATE_TYPE, GateType.AND));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, GATE_TYPE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hit
    ) {
        GateType next = state.getValue(GATE_TYPE).next();

        if (!level.isClientSide()) {
            level.setBlockAndUpdate(pos, state.setValue(GATE_TYPE, next));
            notifyOutputNeighbors(level, pos, state.getValue(FACING));
        }

        if (level.isClientSide()) {
            player.sendOverlayMessage(Component.literal(HardwareLabLanguage.blockName("universal_logic_gate") + ": " + next.displayName()));
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    protected int getSignal(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            Direction direction
    ) {
        if (direction != state.getValue(FACING)) {
            return 0;
        }

        return evaluate(state, level, pos).isHigh() ? 15 : 0;
    }

    @Override
    protected int getDirectSignal(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            Direction direction
    ) {
        return getSignal(state, level, pos, direction);
    }

    @Override
    protected void neighborChanged(
            BlockState state,
            Level level,
            BlockPos pos,
            Block block,
            @Nullable Orientation orientation,
            boolean movedByPiston
    ) {
        super.neighborChanged(state, level, pos, block, orientation, movedByPiston);
        notifyOutputNeighbors(level, pos, state.getValue(FACING));
    }

    public static Signal evaluate(BlockState state, BlockGetter level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        Direction inputA = facing.getClockWise();
        Direction inputB = facing.getCounterClockWise();

        Signal a = readSignal(level, pos.relative(inputA), inputA.getOpposite());
        Signal b = readSignal(level, pos.relative(inputB), inputB.getOpposite());

        return state.getValue(GATE_TYPE).function().evaluate(new Signal[]{a, b});
    }

    public static Signal[] getInputs(BlockState state, BlockGetter level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        Direction inputA = facing.getClockWise();
        Direction inputB = facing.getCounterClockWise();

        return new Signal[]{
                readSignal(level, pos.relative(inputA), inputA.getOpposite()),
                readSignal(level, pos.relative(inputB), inputB.getOpposite())
        };
    }

    private static Signal readSignal(BlockGetter level, BlockPos sourcePos, Direction towardGate) {
        BlockState sourceState = level.getBlockState(sourcePos);
        return Signal.of(sourceState.getSignal(level, sourcePos, towardGate) > 0);
    }

    private static void notifyOutputNeighbors(Level level, BlockPos pos, Direction facing) {
        level.updateNeighborsAt(pos, ModBlocks.UNIVERSAL_LOGIC_GATE);
        level.updateNeighborsAt(pos.relative(facing), ModBlocks.UNIVERSAL_LOGIC_GATE);
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
