package com.maxime.hardwarelab.block;

import com.maxime.hardwarelab.HardwareLabLanguage;
import com.maxime.hardwarelab.block.entity.FpgaBlockEntity;
import com.maxime.hardwarelab.block.entity.ModBlockEntities;
import com.maxime.hardwarelab.logic.BusSignal;
import com.maxime.hardwarelab.logic.BusWidth;
import com.maxime.hardwarelab.logic.FpgaLut;
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

public final class FpgaBlock extends BaseEntityBlock implements BusOutputBlock {
    public static final MapCodec<FpgaBlock> CODEC = simpleCodec(FpgaBlock::new);
    public static final net.minecraft.world.level.block.state.properties.EnumProperty<Direction> FACING =
            HorizontalDirectionalBlock.FACING;

    public FpgaBlock(Properties properties) {
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
        return new FpgaBlockEntity(pos, state);
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

        FpgaBlockEntity entity = getEntity(level, pos);
        if (entity == null) {
            return;
        }

        Direction facing = state.getValue(FACING);
        boolean i0 = readRedstone(level, pos.relative(facing.getOpposite()), facing);
        boolean i1 = readRedstone(level, pos.relative(facing.getCounterClockWise()), facing.getCounterClockWise().getOpposite());
        boolean i2 = readRedstone(level, pos.relative(facing.getClockWise()), facing.getClockWise().getOpposite());
        boolean i3 = readRedstone(level, pos.above(), Direction.DOWN);
        boolean clock = readRedstone(level, pos.below(), Direction.UP);

        if (entity.registered()) {
            entity.updateClock(clock, entity.evaluate(pack(i0, i1, i2, i3)));
        }
        level.updateNeighborsAt(pos, this);
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

        FpgaBlockEntity entity = getEntity(level, pos);
        if (entity == null) {
            return InteractionResult.PASS;
        }

        if (player.isShiftKeyDown()) {
            entity.toggleRegistered();
        } else {
            entity.cycleMode();
        }

        player.sendOverlayMessage(Component.literal(
                HardwareLabLanguage.blockName("fpga") + " | MODE=" + entity.mode().displayName()
                        + " | " + (entity.registered() ? "REGISTERED" : "COMBINATIONAL")
                        + " | LUT=0x" + String.format("%04X", FpgaLut.truthTable(entity.mode()))
        ));
        level.updateNeighborsAt(pos, this);
        return InteractionResult.SUCCESS;
    }

    @Override
    public BusSignal getBusOutput(BlockGetter level, BlockPos pos, BlockState state) {
        FpgaBlockEntity entity = getEntity(level, pos);
        if (entity == null) {
            return BusSignal.zero(BusWidth.BITS_8);
        }

        Direction facing = state.getValue(FACING);
        boolean i0 = readRedstone(level, pos.relative(facing.getOpposite()), facing);
        boolean i1 = readRedstone(level, pos.relative(facing.getCounterClockWise()), facing.getCounterClockWise().getOpposite());
        boolean i2 = readRedstone(level, pos.relative(facing.getClockWise()), facing.getClockWise().getOpposite());
        boolean i3 = readRedstone(level, pos.above(), Direction.DOWN);

        boolean result = entity.registered()
                ? entity.registeredOutput()
                : entity.evaluate(pack(i0, i1, i2, i3));

        return BusSignal.of(BusWidth.BITS_8, result ? 1 : 0);
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

    private static boolean readRedstone(BlockGetter level, BlockPos pos, Direction side) {
        BlockState state = level.getBlockState(pos);
        return state.getSignal(level, pos, side) > 0;
    }

    private static int pack(boolean i0, boolean i1, boolean i2, boolean i3) {
        return (i0 ? 1 : 0)
                | (i1 ? 2 : 0)
                | (i2 ? 4 : 0)
                | (i3 ? 8 : 0);
    }

    private static FpgaBlockEntity getEntity(BlockGetter level, BlockPos pos) {
        BlockEntity entity = level.getBlockEntity(pos);
        return entity instanceof FpgaBlockEntity fpga ? fpga : null;
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
