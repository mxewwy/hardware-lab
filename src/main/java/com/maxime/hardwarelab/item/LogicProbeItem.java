package com.maxime.hardwarelab.item;

import com.maxime.hardwarelab.block.BusMuxBlock;
import com.maxime.hardwarelab.block.BusOutputBlock;
import com.maxime.hardwarelab.block.BusSplitterBlock;
import com.maxime.hardwarelab.block.DigitalBusBlock;
import com.maxime.hardwarelab.block.DigitalWireBlock;
import com.maxime.hardwarelab.block.UniversalLogicGateBlock;
import com.maxime.hardwarelab.block.entity.BusMuxBlockEntity;
import com.maxime.hardwarelab.block.entity.BusSplitterBlockEntity;
import com.maxime.hardwarelab.block.entity.DigitalBusBlockEntity;
import com.maxime.hardwarelab.logic.BusSignal;
import com.maxime.hardwarelab.logic.GateType;
import com.maxime.hardwarelab.logic.Signal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public final class LogicProbeItem extends Item {
    public LogicProbeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();

        if (player == null) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof UniversalLogicGateBlock) {
            showGate(player, level, pos, state);
            return InteractionResult.SUCCESS;
        }

        if (state.getBlock() instanceof DigitalWireBlock) {
            showWire(player, state);
            return InteractionResult.SUCCESS;
        }

        if (state.getBlock() instanceof DigitalBusBlock
                || state.getBlock() instanceof BusMuxBlock) {
            showBus(player, level, pos, state);
            return InteractionResult.SUCCESS;
        }

        if (state.getBlock() instanceof BusSplitterBlock) {
            showSplitter(player, level, pos);
            return InteractionResult.SUCCESS;
        }

        int redstone = state.getSignal(level, pos, context.getClickedFace());
        player.sendOverlayMessage(Component.literal(
                "PROBE | " + state.getBlock().getName().getString()
                        + " | SIGNAL=" + redstone
        ));

        return InteractionResult.SUCCESS;
    }

    private static void showGate(Player player, Level level, BlockPos pos, BlockState state) {
        GateType type = state.getValue(UniversalLogicGateBlock.GATE_TYPE);
        Direction facing = state.getValue(UniversalLogicGateBlock.FACING);

        Signal[] inputs = UniversalLogicGateBlock.getInputs(state, level, pos);
        Signal output = UniversalLogicGateBlock.evaluate(state, level, pos);

        player.sendOverlayMessage(Component.literal(
                "PROBE | " + type.displayName()
                        + " | A=" + inputs[0].value()
                        + " B=" + inputs[1].value()
                        + " OUT=" + output.value()
                        + " | FACING=" + facing.getName()
        ));
    }

    private static void showWire(Player player, BlockState state) {
        StringBuilder connections = new StringBuilder();

        appendConnection(connections, state, Direction.NORTH, DigitalWireBlock.NORTH);
        appendConnection(connections, state, Direction.EAST, DigitalWireBlock.EAST);
        appendConnection(connections, state, Direction.SOUTH, DigitalWireBlock.SOUTH);
        appendConnection(connections, state, Direction.WEST, DigitalWireBlock.WEST);
        appendConnection(connections, state, Direction.UP, DigitalWireBlock.UP);
        appendConnection(connections, state, Direction.DOWN, DigitalWireBlock.DOWN);

        player.sendOverlayMessage(Component.literal(
                "PROBE | DIGITAL WIRE"
                        + " | STATE=" + (state.getValue(DigitalWireBlock.POWERED) ? "HIGH" : "LOW")
                        + " | CONNECTIONS=" + (connections.length() == 0 ? "-" : connections)
        ));
    }

    private static void showBus(
            Player player,
            Level level,
            BlockPos pos,
            BlockState state
    ) {
        BusOutputBlock output = (BusOutputBlock) state.getBlock();
        BusSignal signal = output.getBusOutput(level, pos, state);

        player.sendOverlayMessage(Component.literal(
                "PROBE | BUS"
                        + " | WIDTH=" + signal.width().bits()
                        + " | VALUE=0x" + signal.hex()
                        + " | BIN=" + signal.binary()
        ));
    }

    private static void showSplitter(Player player, Level level, BlockPos pos) {
        BlockEntity raw = level.getBlockEntity(pos);
        if (!(raw instanceof BusSplitterBlockEntity entity)) {
            return;
        }

        BusSignal input = getSplitterInput(level, pos);
        String value = input == null ? "NO BUS" : "0x" + input.hex();

        player.sendOverlayMessage(Component.literal(
                "PROBE | BUS SPLITTER"
                        + " | WIDTH=" + entity.width().bits()
                        + " | BANK=" + entity.bank()
                        + " | BITS=" + entity.firstBit() + "-" + (entity.firstBit() + 3)
                        + " | INPUT=" + value
        ));
    }

    private static BusSignal getSplitterInput(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        Direction facing = state.getValue(BusSplitterBlock.FACING);
        return com.maxime.hardwarelab.block.BusNetwork.readOutput(
                level,
                pos.relative(facing.getOpposite()),
                facing
        );
    }

    private static void appendConnection(
            StringBuilder out,
            BlockState state,
            Direction direction,
            BooleanProperty property
    ) {
        if (!state.getValue(property)) {
            return;
        }

        if (out.length() > 0) {
            out.append(",");
        }

        out.append(direction.getName());
    }
}
