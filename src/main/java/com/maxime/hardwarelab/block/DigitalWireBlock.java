package com.maxime.hardwarelab.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.redstone.Orientation;
import org.jspecify.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DigitalWireBlock extends Block {
    public static final MapCodec<DigitalWireBlock> CODEC = simpleCodec(DigitalWireBlock::new);

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty UP = PipeBlock.UP;
    public static final BooleanProperty DOWN = PipeBlock.DOWN;
    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;

    private static final Map<Direction, BooleanProperty> CONNECTIONS = Map.of(
            Direction.UP, UP,
            Direction.DOWN, DOWN,
            Direction.NORTH, NORTH,
            Direction.EAST, EAST,
            Direction.SOUTH, SOUTH,
            Direction.WEST, WEST
    );

    public DigitalWireBlock(BlockBehaviour.Properties properties) {
        super(properties);

        registerDefaultState(defaultBlockState()
                .setValue(POWERED, false)
                .setValue(UP, false)
                .setValue(DOWN, false)
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false));
    }

    @Override
    protected MapCodec<? extends DigitalWireBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED, UP, DOWN, NORTH, EAST, SOUTH, WEST);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return updateConnections(context.getLevel(), defaultBlockState(), context.getClickedPos());
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess ticks,
            BlockPos pos,
            Direction directionToNeighbour,
            BlockPos neighbourPos,
            BlockState neighbourState,
            RandomSource random
    ) {
        BooleanProperty property = CONNECTIONS.get(directionToNeighbour);
        if (property == null) {
            return state;
        }

        return state.setValue(property, canConnect(neighbourState));
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return true;
    }

    @Override
    protected void onPlace(
            BlockState state,
            Level level,
            BlockPos pos,
            BlockState oldState,
            boolean movedByPiston
    ) {
        if (oldState.is(this) || level.isClientSide()) {
            return;
        }

        propagateNetwork(level, pos);
    }

    @Override
    protected void affectNeighborsAfterRemoval(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            boolean movedByPiston
    ) {
        if (movedByPiston) {
            return;
        }

        for (Direction direction : Direction.values()) {
            if (level.getBlockState(pos.relative(direction)).getBlock() instanceof DigitalWireBlock) {
                propagateNetwork(level, pos.relative(direction));
            }
        }
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
        if (!level.isClientSide()) {
            BlockState connected = updateConnections(level, state, pos);
            if (!connected.equals(state)) {
                level.setBlock(pos, connected, Block.UPDATE_ALL);
                state = connected;
            }
            updatePower(level, pos, state);
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
        BooleanProperty connection = CONNECTIONS.get(direction);
        if (!state.getValue(POWERED) || (connection != null && !state.getValue(connection))) {
            return 0;
        }

        return 15;
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
    protected VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return shapeForState(state);
    }

    @Override
    protected VoxelShape getCollisionShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return shapeForState(state);
    }

    private static VoxelShape shapeForState(BlockState state) {
        VoxelShape shape = Shapes.box(6.0 / 16.0, 0.0, 6.0 / 16.0, 10.0 / 16.0, 2.0 / 16.0, 10.0 / 16.0);

        if (state.getValue(NORTH)) shape = Shapes.or(shape, Shapes.box(7.0 / 16.0, 0.0, 0.0, 9.0 / 16.0, 2.0 / 16.0, 10.0 / 16.0));
        if (state.getValue(SOUTH)) shape = Shapes.or(shape, Shapes.box(7.0 / 16.0, 0.0, 6.0 / 16.0, 9.0 / 16.0, 2.0 / 16.0, 1.0));
        if (state.getValue(EAST))  shape = Shapes.or(shape, Shapes.box(6.0 / 16.0, 0.0, 7.0 / 16.0, 1.0, 2.0 / 16.0, 9.0 / 16.0));
        if (state.getValue(WEST))  shape = Shapes.or(shape, Shapes.box(0.0, 0.0, 7.0 / 16.0, 10.0 / 16.0, 2.0 / 16.0, 9.0 / 16.0));
        if (state.getValue(UP))    shape = Shapes.or(shape, Shapes.box(6.0 / 16.0, 2.0 / 16.0, 6.0 / 16.0, 10.0 / 16.0, 1.0, 10.0 / 16.0));
        if (state.getValue(DOWN))  shape = Shapes.or(shape, Shapes.box(6.0 / 16.0, 0.0, 6.0 / 16.0, 10.0 / 16.0, 14.0 / 16.0, 10.0 / 16.0));

        return shape;
    }

    private static boolean propagating;

    private static void propagateNetwork(Level level, BlockPos start) {
        if (propagating) {
            return;
        }

        propagating = true;
        try {
            Set<BlockPos> wires = new HashSet<>();
            ArrayDeque<BlockPos> queue = new ArrayDeque<>();
            queue.add(start);

            while (!queue.isEmpty()) {
                BlockPos pos = queue.removeFirst();
                if (!wires.add(pos)) {
                    continue;
                }

                if (!(level.getBlockState(pos).getBlock() instanceof DigitalWireBlock)) {
                    wires.remove(pos);
                    continue;
                }

                for (Direction direction : Direction.values()) {
                    BlockPos next = pos.relative(direction);
                    if (level.getBlockState(next).getBlock() instanceof DigitalWireBlock && !wires.contains(next)) {
                        queue.addLast(next);
                    }
                }
            }

            for (BlockPos pos : wires) {
                BlockState state = level.getBlockState(pos);
                BlockState connected = updateConnections(level, state, pos);
                if (!connected.equals(state)) {
                    level.setBlock(pos, connected, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
                }
            }

            for (BlockPos pos : wires) {
                BlockState state = level.getBlockState(pos);
                boolean powered = false;

                for (Direction direction : Direction.values()) {
                    BlockPos neighbourPos = pos.relative(direction);
                    if (level.getBlockState(neighbourPos).getBlock() instanceof DigitalWireBlock) {
                        continue;
                    }

                    if (level.getSignal(neighbourPos, direction.getOpposite()) > 0) {
                        powered = true;
                        break;
                    }
                }

                BlockState current = level.getBlockState(pos);
                if (current.getValue(POWERED) != powered) {
                    level.setBlock(pos, current.setValue(POWERED, powered), Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
                }
            }

            for (BlockPos pos : wires) {
                level.updateNeighborsAt(pos, ModBlocks.DIGITAL_WIRE);
            }
        } finally {
            propagating = false;
        }
    }

    private static BlockState updateConnections(BlockGetter level, BlockState state, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            state = state.setValue(
                    CONNECTIONS.get(direction),
                    canConnect(level.getBlockState(pos.relative(direction)))
            );
        }

        return state;
    }

    private static boolean canConnect(BlockState state) {
        return state.getBlock() instanceof DigitalWireBlock || state.isSignalSource();
    }


}
