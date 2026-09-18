package com.maxime.hardwarelab.block;

import com.maxime.hardwarelab.block.entity.BusMuxBlockEntity;
import com.maxime.hardwarelab.block.entity.ModBlockEntities;
import com.maxime.hardwarelab.logic.BusMuxLogic;
import com.maxime.hardwarelab.logic.BusSignal;
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
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

public class BusMuxBlock extends BaseEntityBlock implements BusOutputBlock {
    public static final MapCodec<BusMuxBlock> CODEC = simpleCodec(BusMuxBlock::new);
    public static final net.minecraft.world.level.block.state.properties.EnumProperty<Direction> FACING =
            HorizontalDirectionalBlock.FACING;

    public BusMuxBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(
                FACING,
                context.getHorizontalDirection().getOpposite()
        );
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BusMuxBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hitResult
    ) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BusMuxBlockEntity entity = getEntity(level, pos);
        if (entity == null) {
            return InteractionResult.PASS;
        }

        entity.cycleWidth();
        player.sendOverlayMessage(Component.literal(
                "Bus MUX | WIDTH=" + entity.width().bits()
                        + " | SELECT=REDSTONE"
        ));
        level.updateNeighborsAt(pos, this);

        return InteractionResult.SUCCESS;
    }

    @Override
    protected void neighborChanged(
            BlockState state,
            Level level,
            BlockPos pos,
            Block block,
            net.minecraft.world.level.redstone.Orientation orientation,
            boolean movedByPiston
    ) {
        if (!level.isClientSide()) {
            level.updateNeighborsAt(pos, this);
        }
    }

    @Override
    public BusSignal getBusOutput(BlockGetter level, BlockPos pos, BlockState state) {
        BusMuxBlockEntity entity = getEntity(level, pos);
        if (entity == null) {
            return BusSignal.zero(com.maxime.hardwarelab.logic.BusWidth.BITS_8);
        }

        Direction facing = state.getValue(FACING);
        Direction right = facing.getClockWise();

        BusSignal inputA = BusNetwork.readOutput(
                level,
                pos.relative(facing.getOpposite()),
                facing
        );
        BusSignal inputB = BusNetwork.readOutput(
                level,
                pos.relative(facing.getCounterClockWise()),
                right
        );

        boolean selectHigh = level.getSignal(
                pos.relative(right),
                right.getOpposite()
        ) > 0;

        return BusMuxLogic.select(
                inputA,
                inputB,
                Signal.of(selectHigh),
                entity.width()
        );
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

        return getBusOutput(level, pos, state).value() == 0 ? 0 : 15;
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

    private static BusMuxBlockEntity getEntity(BlockGetter level, BlockPos pos) {
        BlockEntity entity = level.getBlockEntity(pos);
        return entity instanceof BusMuxBlockEntity mux ? mux : null;
    }
}
