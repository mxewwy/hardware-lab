package com.maxime.hardwarelab.item;

import com.maxime.hardwarelab.block.BusMuxBlock;
import com.maxime.hardwarelab.block.BusOutputBlock;
import com.maxime.hardwarelab.block.BusSplitterBlock;
import com.maxime.hardwarelab.block.CpuBlock;
import com.maxime.hardwarelab.block.DigitalBusBlock;
import com.maxime.hardwarelab.block.DigitalWireBlock;
import com.maxime.hardwarelab.block.EightBitRegisterBlock;
import com.maxime.hardwarelab.block.FpgaBlock;
import com.maxime.hardwarelab.block.Ram256Block;
import com.maxime.hardwarelab.block.Rom256Block;
import com.maxime.hardwarelab.block.SevenSegmentDisplayBlock;
import com.maxime.hardwarelab.block.LedMatrixBlock;
import com.maxime.hardwarelab.block.UniversalLogicGateBlock;
import com.maxime.hardwarelab.block.entity.BusMuxBlockEntity;
import com.maxime.hardwarelab.block.entity.BusSplitterBlockEntity;
import com.maxime.hardwarelab.block.entity.CpuBlockEntity;
import com.maxime.hardwarelab.block.entity.EightBitRegisterBlockEntity;
import com.maxime.hardwarelab.block.entity.FpgaBlockEntity;
import com.maxime.hardwarelab.block.entity.Ram256BlockEntity;
import com.maxime.hardwarelab.block.entity.Rom256BlockEntity;
import com.maxime.hardwarelab.block.entity.SevenSegmentDisplayBlockEntity;
import com.maxime.hardwarelab.block.entity.LedMatrixBlockEntity;
import com.maxime.hardwarelab.logic.BusSignal;
import com.maxime.hardwarelab.logic.FpgaLut;
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
    public Component getName(net.minecraft.world.item.ItemStack stack) {
        return com.maxime.hardwarelab.HardwareLabLanguage.component("item.logic_probe");
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
                || state.getBlock() instanceof BusMuxBlock
                || state.getBlock() instanceof EightBitRegisterBlock
                || state.getBlock() instanceof CpuBlock
                || state.getBlock() instanceof FpgaBlock) {
            showBus(player, level, pos, state);
            return InteractionResult.SUCCESS;
        }

        if (state.getBlock() instanceof BusSplitterBlock) {
            showSplitter(player, level, pos);
            return InteractionResult.SUCCESS;
        }

        if (state.getBlock() instanceof Ram256Block) {
            showRam(player, level, pos, state);
            return InteractionResult.SUCCESS;
        }

        if (state.getBlock() instanceof Rom256Block) {
            showRom(player, level, pos, state);
            return InteractionResult.SUCCESS;
        }

        if (state.getBlock() instanceof SevenSegmentDisplayBlock) {
            showSevenSegment(player, level, pos);
            return InteractionResult.SUCCESS;
        }

        if (state.getBlock() instanceof LedMatrixBlock) {
            showMatrix(player, level, pos);
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

    private static void showBus(Player player, Level level, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof BusOutputBlock output)) {
            return;
        }

        BusSignal signal = output.getBusOutput(level, pos, state);
        String detail = signal.width().bits() == 8 && state.getBlock() instanceof FpgaBlock
                ? " FPGA=" + ((FpgaBlockEntity) level.getBlockEntity(pos)).mode().displayName()
                : "";

        if (state.getBlock() instanceof CpuBlock
                && level.getBlockEntity(pos) instanceof CpuBlockEntity cpu) {
            detail = " CPU=PC:" + String.format("%02X", cpu.cpu().pc())
                    + " A:" + String.format("%02X", cpu.cpu().a())
                    + " B:" + String.format("%02X", cpu.cpu().b());
        }

        player.sendOverlayMessage(Component.literal(
                "PROBE | BUS"
                        + " | WIDTH=" + signal.width().bits()
                        + " | VALUE=0x" + signal.hex()
                        + " | BIN=" + signal.binary()
                        + detail
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

    private static void showRam(Player player, Level level, BlockPos pos, BlockState state) {
        if (!(level.getBlockEntity(pos) instanceof Ram256BlockEntity ram)) {
            return;
        }

        int address = readAddress(level, pos, state.getValue(Ram256Block.FACING));
        player.sendOverlayMessage(Component.literal(
                "PROBE | RAM-256 | ADDR=0x" + String.format("%02X", address)
                        + " | DATA=0x" + String.format("%02X", ram.read(address))
        ));
    }

    private static void showRom(Player player, Level level, BlockPos pos, BlockState state) {
        if (!(level.getBlockEntity(pos) instanceof Rom256BlockEntity rom)) {
            return;
        }

        int address = readAddress(level, pos, state.getValue(Rom256Block.FACING));
        player.sendOverlayMessage(Component.literal(
                "PROBE | ROM-256 | PATTERN=" + rom.pattern()
                        + " | ADDR=0x" + String.format("%02X", address)
                        + " | DATA=0x" + String.format("%02X", rom.read(address))
        ));
    }

    private static void showSevenSegment(Player player, Level level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof SevenSegmentDisplayBlockEntity display)) {
            return;
        }

        player.sendOverlayMessage(Component.literal(
                "PROBE | 7-SEG | DIGIT=0x" + String.format("%X", display.value())
        ));
    }

    private static void showMatrix(Player player, Level level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof LedMatrixBlockEntity matrix)) {
            return;
        }

        player.sendOverlayMessage(Component.literal(
                "PROBE | LED MATRIX | ROW=" + matrix.row()
                        + " | DATA=" + String.format("%8s", Integer.toBinaryString(matrix.value())).replace(' ', '0')
        ));
    }

    private static int readAddress(Level level, BlockPos pos, Direction facing) {
        BusSignal address = com.maxime.hardwarelab.block.BusNetwork.readOutput(
                level,
                pos.relative(facing.getOpposite()),
                facing
        );
        return address == null ? 0 : address.resized(com.maxime.hardwarelab.logic.BusWidth.BITS_8).value();
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
