package com.maxime.hardwarelab.client;

import com.maxime.hardwarelab.HardwareLabLanguage;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class HardwareGuideScreen extends Screen {
    private int page;

    private static final String[] LOGIC = {
            "universal_logic_gate", "digital_wire", "redstone_input", "redstone_output",
            "clock_generator", "clock_divider", "d_flip_flop", "adc", "dac"
    };

    private static final String[] COMPUTE = {
            "digital_bus", "bus_mux", "bus_splitter", "bus_merger", "bus_driver",
            "eight_bit_register", "ram_256", "rom_256", "seven_segment_display",
            "led_matrix", "cpu", "fpga"
    };

    public HardwareGuideScreen() {
        super(HardwareLabLanguage.component("gui.guide.title"));
    }

    @Override
    protected void init() {
        int panelWidth = Math.min(780, this.width - 40);
        int left = (this.width - panelWidth) / 2;
        int bottom = Math.min(this.height - 18, 378);

        this.addRenderableWidget(Button.builder(
                HardwareLabLanguage.component("gui.guide.settings"),
                button -> this.minecraft.gui.setScreen(new HardwareSettingsScreen(this))
        ).bounds(left + panelWidth - 124, bottom - 28, 108, 20).build());
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        int key = input.key();
        if (key == InputConstants.KEY_LEFT) {
            page = (page + 3) % 4;
            return true;
        }
        if (key == InputConstants.KEY_RIGHT) {
            page = (page + 1) % 4;
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        graphics.fill(0, 0, width, height, 0xC9090C10);

        int panelWidth = Math.min(780, width - 40);
        int left = (width - panelWidth) / 2;
        int top = 18;
        int right = left + panelWidth;
        int bottom = Math.min(height - 18, top + 360);

        graphics.fill(left, top, right, bottom, 0xF013171C);
        graphics.fill(left, top, right, top + 3, 0xFF4DE38B);

        graphics.text(font, HardwareLabLanguage.text("gui.guide.header"), left + 18, top + 15, 0xFFFFFFFF, true);
        graphics.text(font, HardwareLabLanguage.text("gui.guide.page") + " " + (page + 1) + " / 4",
                right - 100, top + 15, 0xFF7F8B96, false);

        switch (page) {
            case 0 -> drawQuickStart(graphics, left, top);
            case 1 -> drawComponents(graphics, left, top, LOGIC, HardwareLabLanguage.text("gui.guide.logic"));
            case 2 -> drawComponents(graphics, left, top, COMPUTE, HardwareLabLanguage.text("gui.guide.buses"));
            case 3 -> drawTools(graphics, left, top);
            default -> {}
        }

        graphics.fill(left + 16, bottom - 39, right - 16, bottom - 38, 0xFF2C343D);
        graphics.text(font, HardwareLabLanguage.text("gui.guide.footer"),
                left + 18, bottom - 27, 0xFF7F8B96, false);
    }

    private void drawQuickStart(GuiGraphicsExtractor graphics, int left, int top) {
        drawStep(graphics, left, top + 52, "gui.guide.step1", "gui.guide.step1b");
        drawStep(graphics, left, top + 101, "gui.guide.step2", "gui.guide.step2b");
        drawStep(graphics, left, top + 150, "gui.guide.step3", "gui.guide.step3b");
        drawStep(graphics, left, top + 199, "gui.guide.step4", "gui.guide.step4b");

        graphics.fill(left + 18, top + 242, left + 746, top + 244, 0xFF2C343D);
        graphics.text(font, HardwareLabLanguage.text("gui.guide.circuit_title"), left + 18, top + 256, 0xFFFFFFFF, true);

        String[] circuit = {"LEVER", "INPUT", "WIRE", "NOT", "WIRE", "OUTPUT", "LAMP"};
        int startX = left + 26;
        int boxY = top + 278;
        for (int i = 0; i < circuit.length; i++) {
            int x = startX + i * 103;
            graphics.fill(x, boxY, x + 78, boxY + 26, 0xFF202930);
            graphics.fill(x, boxY, x + 78, boxY + 2, 0xFF4DE38B);
            graphics.text(font, circuit[i], x + 8, boxY + 9, 0xFFFFFFFF, true);
            if (i < circuit.length - 1) {
                graphics.text(font, ">", x + 83, boxY + 8, 0xFF5A6772, true);
            }
        }

        graphics.text(font, HardwareLabLanguage.text("gui.guide.port_note"),
                left + 18, top + 316, 0xFFB9C2CC, false);
        graphics.text(font, HardwareLabLanguage.text("gui.guide.bus_note"),
                left + 18, top + 332, 0xFF8F9AA5, false);
    }

    private void drawStep(GuiGraphicsExtractor graphics, int left, int y, String titleKey, String bodyKey) {
        graphics.text(font, HardwareLabLanguage.text(titleKey), left + 18, y, 0xFF4DE38B, true);
        graphics.text(font, HardwareLabLanguage.text(bodyKey), left + 18, y + 16, 0xFFB9C2CC, false);
    }

    private void drawComponents(GuiGraphicsExtractor graphics, int left, int top, String[] ids, String section) {
        graphics.text(font, section, left + 18, top + 45, 0xFFFFFFFF, true);

        int columns = 2;
        int rows = (ids.length + columns - 1) / columns;
        int columnWidth = 370;

        for (int i = 0; i < ids.length; i++) {
            int col = i / rows;
            int row = i % rows;
            int x = left + 18 + col * columnWidth;
            int y = top + 66 + row * 28;

            String id = ids[i];
            graphics.text(font, HardwareLabLanguage.blockName(id), x, y, 0xFFFFFFFF, true);
            graphics.text(font, HardwareLabLanguage.control(id), x, y + 12, 0xFF8F9AA5, false);
        }
    }

    private void drawTools(GuiGraphicsExtractor graphics, int left, int top) {
        graphics.text(font, HardwareLabLanguage.text("gui.guide.tools"), left + 18, top + 48, 0xFFFFFFFF, true);
        graphics.text(font, HardwareLabLanguage.itemName("logic_probe"), left + 18, top + 74, 0xFFFFFFFF, true);
        graphics.text(font, HardwareLabLanguage.control("logic_probe"), left + 18, top + 88, 0xFFB9C2CC, false);
        graphics.text(font, HardwareLabLanguage.itemName("oscilloscope"), left + 18, top + 118, 0xFFFFFFFF, true);
        graphics.text(font, HardwareLabLanguage.control("oscilloscope"), left + 18, top + 132, 0xFFB9C2CC, false);

        graphics.text(font, "H", left + 18, top + 168, 0xFF4DE38B, true);
        graphics.text(font, HardwareLabLanguage.text("gui.guide.step4b"), left + 36, top + 168, 0xFFB9C2CC, false);

        graphics.text(font, HardwareLabLanguage.text("gui.guide.settings"), left + 18, top + 212, 0xFFFFFFFF, true);
        graphics.text(font, HardwareLabLanguage.text("gui.settings.language_help"),
                left + 18, top + 228, 0xFFB9C2CC, false);
        graphics.text(font, HardwareLabLanguage.text("gui.settings.english") + " / " +
                HardwareLabLanguage.text("gui.settings.russian"),
                left + 18, top + 247, 0xFF8F9AA5, false);
    }
}
