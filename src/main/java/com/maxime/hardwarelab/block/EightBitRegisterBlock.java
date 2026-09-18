package com.maxime.hardwarelab.block;

import com.maxime.hardwarelab.block.entity.EightBitRegisterBlockEntity;
import com.maxime.hardwarelab.block.entity.ModBlockEntities;
import com.maxime.hardwarelab.logic.BusSignal;
import com.maxime.hardwarelab.logic.BusWidth;
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

public final class EightBitRegisterBlock extends BaseEntityBlock implements BusOutputBlock {
    public static final MapCodec<EightBitRegisterBlock> CODEC = simpleCodec(EightBitRegisterBlock::new);
    public static final net.minecraft.world.level.block.state.properties.EnumProperty<Direction> FACING =
            HorizontalDirectionalBlock.FACING;

    public EightBitRegisterBlock(Properties properties) {
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
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EightBitRegisterBlockEntity(pos, state);
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

        EightBitRegisterBlockEntity entity = getEntity(level, pos);
        if (entity == null) {
            return InteractionResult.PASS;
        }

        player.sendOverlayMessage(Component.literal(
                "8-bit Register | VALUE=0x" + String.format("%02X", entity.value())
        ));
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
        if (level.isClientSide()) {
            return;
        }

        EightBitRegisterBlockEntity entity = getEntity(level, pos);
        if (entity == null) {
            return;
        }

        Direction facing = state.getValue(FACING);
        Direction clockDirection = facing.getClockWise();

        BlockState clockState = level.getBlockState(pos.relative(clockDirection));
        boolean clockHigh = clockState.getSignal(level, pos.relative(clockDirection), clockDirection.getOpposite()) > 0;

        BusSignal input = BusNetwork.readOutput(
                level,
                pos.relative(facing.getOpposite()),
                facing
        );

        if (entity.capture(clockHigh, input)) {
            level.updateNeighborsAt(pos, this);
        }
    }

    @Override
    public BusSignal getBusOutput(BlockGetter level, BlockPos pos, BlockState state) {
        EightBitRegisterBlockEntity entity = getEntity(level, pos);
        return entity == null ? BusSignal.zero(BusWidth.BITS_8) : BusSignal.of(BusWidth.BITS_8, entity.value());
    }

    @Override
    public Direction getBusOutputDirection(BlockState state) {
        return state.getValue(FACING);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return direction == state.getValue(FACING) && getBusOutput(level, pos, state).value() != 0 ? 15 : 0;
    }

    @Override
    protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return getSignal(state, level, pos, direction);
    }

    private static EightBitRegisterBlockEntity getEntity(BlockGetter level, BlockPos pos) {
        BlockEntity entity = level.getBlockEntity(pos);
        return entity instanceof EightBitRegisterBlockEntity register ? register : null;
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
