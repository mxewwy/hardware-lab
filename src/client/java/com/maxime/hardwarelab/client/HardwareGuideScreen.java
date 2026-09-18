package com.maxime.hardwarelab.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;

public final class HardwareGuideScreen extends Screen {
    private record Entry(String name, String purpose, String control) {}

    private static final Entry[][] PAGES = {
            {
                    new Entry("Universal Logic Gate", "AND / OR / XOR / NAND / NOR / XNOR / NOT / BUFFER", "Right-click: next logic function"),
                    new Entry("Digital Wire", "Binary HIGH / LOW carrier", "Connect to signal sources"),
                    new Entry("Redstone Input", "Vanilla redstone -> hardware signal", "Input is the marked face"),
                    new Entry("Redstone Output", "Hardware signal -> vanilla redstone", "Output is the marked face"),
                    new Entry("Clock Generator", "Periodic digital clock source", "Right-click: change rate"),
                    new Entry("Clock Divider", "Divides an incoming clock", "Right-click: change division"),
                    new Entry("D Flip-Flop", "Stores one bit on a clock edge", "D + CLK in, Q out")
            },
            {
                    new Entry("Digital Bus", "Manual 4 / 8 / 16 / 32-bit source", "Right-click: pattern | Shift + right-click: width"),
                    new Entry("Bus MUX", "Selects one of two bus inputs", "Right-click: width | SELECT = redstone"),
                    new Entry("Bus Splitter", "Extracts one 4-bit bank", "Right-click: bank | Shift + right-click: width"),
                    new Entry("Bus Merger", "Inserts one 4-bit bank", "Right-click: bank | Shift + right-click: width"),
                    new Entry("Tri-State Bus Driver", "Drives a shared bus when enabled", "Side redstone input = enable"),
                    new Entry("4-bit ADC", "Redstone power -> 4-bit bus", "Input from the back"),
                    new Entry("4-bit DAC", "4-bit bus -> redstone", "Bus in, redstone out"),
                    new Entry("8-bit Register", "Holds a byte", "Clock/load controls the latch"),
                    new Entry("RAM-256", "256 bytes read/write memory", "Address selects byte | Shift + right-click: clear"),
                    new Entry("ROM-256", "256-byte read-only demo source", "Right-click: cycle demo pattern")
            },
            {
                    new Entry("7-Segment Display", "Hexadecimal display endpoint", "Feed a 4-bit value"),
                    new Entry("8x8 LED Matrix", "Byte-sized visual endpoint", "Feed row + 8-bit data"),
                    new Entry("8-bit CPU", "Runs the built-in CPU ISA", "Right-click: program | Shift + right-click: speed"),
                    new Entry("FPGA 4-LUT", "Four-input programmable truth table", "Right-click: LUT mode | Shift + right-click: register"),
                    new Entry("Logic Probe", "Inspect a component and read live values", "Right-click a component"),
                    new Entry("Oscilloscope", "Watch a live redstone waveform", "Right-click source | R: reset"),
                    new Entry("Hardware Guide", "This panel", "H: open | Left/Right: page | Esc: close")
            }
    };

    private int page;

    public HardwareGuideScreen() {
        super(Component.literal("Hardware Lab Guide"));
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        int keyCode = input.key();

        if (keyCode == InputConstants.KEY_LEFT) {
            this.page = (this.page + PAGES.length - 1) % PAGES.length;
            return true;
        }
        if (keyCode == InputConstants.KEY_RIGHT) {
            this.page = (this.page + 1) % PAGES.length;
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public void extractRenderState(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float delta
    ) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        graphics.fill(0, 0, this.width, this.height, 0xC9090C10);

        int panelWidth = Math.min(760, this.width - 40);
        int left = (this.width - panelWidth) / 2;
        int top = 22;
        int right = left + panelWidth;
        int bottom = Math.min(this.height - 22, top + 320);

        graphics.fill(left, top, right, bottom, 0xF013171C);
        graphics.fill(left, top, right, top + 3, 0xFF4DE38B);

        graphics.text(this.font, "HARDWARE LAB // GUIDE", left + 18, top + 15, 0xFFFFFFFF, true);
        graphics.text(this.font, "PAGE " + (this.page + 1) + " / " + PAGES.length, right - 72, top + 15, 0xFF7F8B96, false);

        Entry[] entries = PAGES[this.page];
        int columns = 2;
        int rows = (entries.length + columns - 1) / columns;
        int columnWidth = (panelWidth - 44) / columns;

        for (int i = 0; i < entries.length; i++) {
            Entry entry = entries[i];
            int column = i / rows;
            int row = i % rows;
            int x = left + 18 + column * columnWidth;
            int y = top + 45 + row * 43;

            graphics.text(this.font, entry.name, x, y, 0xFFFFFFFF, true);
            graphics.text(this.font, entry.purpose, x, y + 12, 0xFFB9C2CC, false);
            graphics.text(this.font, entry.control, x, y + 24, 0xFF7F8B96, false);
        }

        graphics.fill(left + 16, bottom - 30, right - 16, bottom - 29, 0xFF2C343D);
        graphics.text(this.font, "<", left + 18, bottom - 20, 0xFFB9C2CC, true);
        graphics.text(this.font, "LEFT / RIGHT", left + 31, bottom - 20, 0xFF7F8B96, false);
        graphics.text(this.font, "PAGE", left + 108, bottom - 20, 0xFF7F8B96, false);
        graphics.text(this.font, "ESC", right - 45, bottom - 20, 0xFF7F8B96, false);
    }
}
