package com.maxime.hardwarelab.item;

import com.maxime.hardwarelab.block.DigitalWireBlock;
import com.maxime.hardwarelab.block.UniversalLogicGateBlock;
import com.maxime.hardwarelab.logic.GateType;
import com.maxime.hardwarelab.logic.Signal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
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

        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (state.getBlock() instanceof UniversalLogicGateBlock) {
            showGate(player, level, pos, state);
            return InteractionResult.SUCCESS;
        }

        if (state.getBlock() instanceof DigitalWireBlock) {
            showWire(player, state);
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
