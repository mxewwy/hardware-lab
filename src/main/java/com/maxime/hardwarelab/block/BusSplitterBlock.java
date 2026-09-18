package com.maxime.hardwarelab.block;

import com.maxime.hardwarelab.block.entity.BusSplitterBlockEntity;
import com.maxime.hardwarelab.block.entity.ModBlockEntities;
import com.maxime.hardwarelab.logic.BusSignal;
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

public class BusSplitterBlock extends BaseEntityBlock {
    public static final MapCodec<BusSplitterBlock> CODEC = simpleCodec(BusSplitterBlock::new);
    public static final net.minecraft.world.level.block.state.properties.EnumProperty<Direction> FACING =
            HorizontalDirectionalBlock.FACING;

    public BusSplitterBlock(Properties properties) {
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
        return new BusSplitterBlockEntity(pos, state);
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

        BusSplitterBlockEntity entity = getEntity(level, pos);
        if (entity == null) {
            return InteractionResult.PASS;
        }

        if (player.isShiftKeyDown()) {
            entity.cycleWidth();
        } else {
            entity.cycleBank();
        }

        player.sendOverlayMessage(Component.literal(
                "Bus Splitter | WIDTH=" + entity.width().bits()
                        + " | BANK=" + entity.bank()
                        + " | BITS=" + entity.firstBit() + "-" + (entity.firstBit() + 3)
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
        BusSplitterBlockEntity entity = getEntity(level, pos);
        if (entity == null) {
            return 0;
        }

        Direction facing = state.getValue(FACING);
        int bitOffset = outputBitOffset(facing, direction);
        if (bitOffset < 0) {
            return 0;
        }

        Direction inputDirection = facing.getOpposite();
        BusSignal input = BusNetwork.readOutput(
                level,
                pos.relative(inputDirection),
                facing
        );
        if (input == null) {
            return 0;
        }

        BusSignal selected = input.resized(entity.width());
        return selected.bit(entity.firstBit() + bitOffset) ? 15 : 0;
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

    private static int outputBitOffset(Direction facing, Direction direction) {
        if (direction == facing) {
            return 0;
        }

        if (direction == facing.getCounterClockWise()) {
            return 1;
        }

        if (direction == facing.getClockWise()) {
            return 2;
        }

        if (direction == Direction.UP) {
            return 3;
        }

        return -1;
    }

    private static BusSplitterBlockEntity getEntity(BlockGetter level, BlockPos pos) {
        BlockEntity entity = level.getBlockEntity(pos);
        return entity instanceof BusSplitterBlockEntity splitter ? splitter : null;
    }
}
