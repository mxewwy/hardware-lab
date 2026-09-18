package com.maxime.hardwarelab.block;

import com.maxime.hardwarelab.HardwareLabLanguage;
import com.maxime.hardwarelab.block.entity.DigitalBusBlockEntity;
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

public class DigitalBusBlock extends BaseEntityBlock implements BusOutputBlock {
    public static final MapCodec<DigitalBusBlock> CODEC = simpleCodec(DigitalBusBlock::new);
    public static final net.minecraft.world.level.block.state.properties.EnumProperty<Direction> FACING =
            HorizontalDirectionalBlock.FACING;

    public DigitalBusBlock(Properties properties) {
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
        return new DigitalBusBlockEntity(pos, state);
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

        DigitalBusBlockEntity entity = getEntity(level, pos);
        if (entity == null) {
            return InteractionResult.PASS;
        }

        if (player.isShiftKeyDown()) {
            entity.cycleWidth();
        } else {
            entity.cycleTestPattern();
        }

        player.sendOverlayMessage(Component.literal(
                HardwareLabLanguage.blockName("digital_bus") + " | WIDTH=" + entity.width().bits()
                        + " | VALUE=0x" + entity.manualSignal().hex()
        ));
        level.updateNeighborsAt(pos, this);

        return InteractionResult.SUCCESS;
    }

    @Override
    public BusSignal getBusOutput(BlockGetter level, BlockPos pos, BlockState state) {
        DigitalBusBlockEntity entity = getEntity(level, pos);
        if (entity == null) {
            return BusSignal.zero(BusWidth.BITS_8);
        }

        Direction facing = state.getValue(FACING);
        BusSignal input = BusNetwork.readOutput(
                level,
                pos.relative(facing.getOpposite()),
                facing
        );

        if (input != null) {
            return input.resized(entity.width());
        }

        return entity.manualSignal();
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
    protected int getSignal(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            Direction direction
    ) {
        if (direction != state.getValue(FACING)) {
            return 0;
        }

        BusSignal output = getBusOutput(level, pos, state);
        return output.value() == 0 ? 0 : 15;
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

    private static DigitalBusBlockEntity getEntity(BlockGetter level, BlockPos pos) {
        BlockEntity entity = level.getBlockEntity(pos);
        return entity instanceof DigitalBusBlockEntity bus ? bus : null;
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
