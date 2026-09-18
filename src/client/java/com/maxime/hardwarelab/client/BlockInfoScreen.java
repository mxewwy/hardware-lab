package com.maxime.hardwarelab.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class BlockInfoScreen extends Screen {
    private final BlockPos targetPos;
    private final Direction targetFace;
    private final String path;

    public BlockInfoScreen(BlockPos targetPos, Direction targetFace, String path) {
        super(Component.literal("Hardware Lab Component"));
        this.targetPos = targetPos.immutable();
        this.targetFace = targetFace;
        this.path = path;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == InputConstants.KEY_H) {
            this.minecraft.gui.setScreen(new HardwareGuideScreen());
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void extractRenderState(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float delta
    ) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        graphics.fill(0, 0, this.width, this.height, 0xB8090C10);

        int panelWidth = Math.min(640, this.width - 40);
        int left = (this.width - panelWidth) / 2;
        int top = 28;
        int right = left + panelWidth;
        int bottom = Math.min(this.height - 28, top + 330);

        graphics.fill(left, top, right, bottom, 0xF013171C);
        graphics.fill(left, top, right, top + 3, 0xFF39D5FF);

        String title = this.minecraft.level == null
                ? path
                : this.minecraft.level.getBlockState(this.targetPos).getBlock().getName().getString();

        graphics.text(this.font, title, left + 18, top + 18, 0xFFFFFFFF, true);
        graphics.text(this.font, "HARDWARE LAB COMPONENT", left + 18, top + 32, 0xFF7F8B96, false);
        graphics.text(this.font, "CLICKED FACE", left + 18, top + 57, 0xFF7F8B96, false);
        graphics.text(this.font, targetFace.getName().toUpperCase(), left + 98, top + 57, 0xFFB9C2CC, true);

        int y = top + 84;
        for (String line : details(path)) {
            graphics.text(this.font, line, left + 18, y, 0xFFB9C2CC, false);
            y += 17;
        }

        graphics.fill(left + 16, bottom - 42, right - 16, bottom - 41, 0xFF2C343D);
        graphics.text(this.font, "LIVE VALUE", left + 18, bottom - 31, 0xFF4DE38B, true);
        graphics.text(this.font, "The probe also prints the current value/state.", left + 94, bottom - 31, 0xFF7F8B96, false);
        graphics.text(this.font, "H  GUIDE", left + 18, bottom - 14, 0xFF7F8B96, false);
        graphics.text(this.font, "ESC  CLOSE", right - 76, bottom - 14, 0xFF7F8B96, false);
    }

    private static List<String> details(String path) {
        return switch (path) {
            case "universal_logic_gate" -> List.of(
                    "PORTS   A + B = side inputs, OUT = front",
                    "CONTROL Right-click cycles AND / OR / XOR / NAND / NOR / XNOR / NOT / BUFFER",
                    "USE     Build combinational logic from redstone and digital wires"
            );
            case "digital_wire" -> List.of(
                    "SIGNAL  Binary HIGH / LOW carrier",
                    "USE     Connect hardware blocks; connections are automatic",
                    "TIP     Use the Logic Probe to inspect connected state"
            );
            case "redstone_input" -> List.of(
                    "IN      Vanilla redstone",
                    "OUT     Hardware signal on the marked face",
                    "USE     Bridge vanilla redstone into the digital system"
            );
            case "redstone_output" -> List.of(
                    "IN      Hardware signal",
                    "OUT     Vanilla redstone on the marked face",
                    "USE     Bridge digital logic back into Minecraft"
            );
            case "clock_generator" -> List.of(
                    "OUT     Clock on the marked face",
                    "CONTROL Right-click cycles 1 / 2 / 4 / 8 tick rates",
                    "USE     Drive sequential logic"
            );
            case "clock_divider" -> List.of(
                    "IN      Clock from the back",
                    "OUT     Divided clock from the front",
                    "CONTROL Right-click changes the division factor"
            );
            case "d_flip_flop" -> List.of(
                    "INPUT   D + CLK",
                    "OUTPUT  Q",
                    "USE     Capture one bit on a rising clock edge"
            );
            case "digital_bus" -> List.of(
                    "BUS     4 / 8 / 16 / 32 bits",
                    "CONTROL Right-click = test pattern | Shift + right-click = width",
                    "USE     Easy manual bus source for experiments"
            );
            case "bus_mux" -> List.of(
                    "INPUTS  BUS A + BUS B, SELECT = redstone",
                    "OUTPUT  Selected bus",
                    "CONTROL Right-click cycles bus width"
            );
            case "bus_splitter" -> List.of(
                    "INPUT   Wide bus",
                    "OUTPUT  One selected 4-bit bank",
                    "CONTROL Right-click = bank | Shift + right-click = width"
            );
            case "bus_merger" -> List.of(
                    "INPUT   One selected 4-bit bank",
                    "OUTPUT  Wide bus",
                    "CONTROL Right-click = bank | Shift + right-click = width"
            );
            case "bus_driver" -> List.of(
                    "INPUT   Bus source behind the driver",
                    "ENABLE  Redstone on the side input",
                    "OUTPUT  Shared bus only while enabled"
            );
            case "adc" -> List.of(
                    "INPUT   Adjacent redstone strength 0..15",
                    "OUTPUT  4-bit bus",
                    "USE     Convert vanilla analog redstone to digital data"
            );
            case "dac" -> List.of(
                    "INPUT   4-bit bus",
                    "OUTPUT  Redstone strength 0..15",
                    "USE     Convert digital data to vanilla redstone"
            );
            case "eight_bit_register" -> List.of(
                    "BUS     8-bit data",
                    "CONTROL Clock/load latch",
                    "USE     Hold a byte stable"
            );
            case "ram_256" -> List.of(
                    "ADDR    8-bit address selects 0x00..0xFF",
                    "DATA    8-bit read/write data",
                    "CONTROL Shift + right-click = clear"
            );
            case "rom_256" -> List.of(
                    "ADDR    8-bit address",
                    "DATA    8-bit read-only data",
                    "CONTROL Right-click cycles the demo pattern"
            );
            case "seven_segment_display" -> List.of(
                    "INPUT   4-bit hexadecimal digit",
                    "OUTPUT  Visual endpoint",
                    "USE     Connect a bus to display a hex value"
            );
            case "led_matrix" -> List.of(
                    "INPUT   Row select + 8-bit data",
                    "OUTPUT  8x8 visual endpoint",
                    "USE     Render byte-sized patterns"
            );
            case "cpu" -> List.of(
                    "CORE    A, B, PC, flags and 256-byte memory",
                    "CONTROL Right-click = program | Shift + right-click = speed",
                    "USE     Run the built-in 8-bit ISA"
            );
            case "fpga" -> List.of(
                    "LUT     Four inputs -> one programmable truth-table output",
                    "CONTROL Right-click = mode | Shift + right-click = registered",
                    "USE     Experiment with combinational and clocked logic"
            );
            default -> List.of(
                    "Use the Hardware Guide (H) for the full control reference"
            );
        };
    }
}
