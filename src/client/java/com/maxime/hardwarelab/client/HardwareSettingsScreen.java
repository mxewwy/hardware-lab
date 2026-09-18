package com.maxime.hardwarelab.client;

import com.maxime.hardwarelab.HardwareLabLanguage;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class HardwareSettingsScreen extends Screen {
    private final Screen parent;
    private Button english;
    private Button russian;

    public HardwareSettingsScreen(Screen parent) {
        super(HardwareLabLanguage.component("gui.settings.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int panelWidth = 420;
        int left = (width - panelWidth) / 2;
        int top = height / 2 - 58;

        english = addRenderableWidget(Button.builder(Component.literal("English"), b -> {
            HardwareLabLanguage.setLanguage(HardwareLabLanguage.Language.ENGLISH);
            updateButtons();
        }).bounds(left, top, panelWidth, 20).build());

        russian = addRenderableWidget(Button.builder(Component.literal("Русский"), b -> {
            HardwareLabLanguage.setLanguage(HardwareLabLanguage.Language.RUSSIAN);
            updateButtons();
        }).bounds(left, top + 28, panelWidth, 20).build());

        addRenderableWidget(Button.builder(HardwareLabLanguage.component("gui.settings.back"),
                b -> minecraft.gui.setScreen(parent))
                .bounds(left, top + 70, panelWidth, 20).build());

        updateButtons();
    }

    @Override
    public void onClose() {
        minecraft.gui.setScreen(parent);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        int panelWidth = 460;
        int left = (width - panelWidth) / 2;
        int top = height / 2 - 118;

        graphics.fill(0, 0, width, height, 0xC9090C10);
        graphics.fill(left, top, left + panelWidth, top + 205, 0xF013171C);
        graphics.fill(left, top, left + panelWidth, top + 3, 0xFF4DE38B);

        graphics.text(font, HardwareLabLanguage.text("gui.settings.title"),
                left + 20, top + 18, 0xFFFFFFFF, true);
        graphics.text(font, HardwareLabLanguage.text("gui.settings.language"),
                left + 20, top + 48, 0xFF4DE38B, true);
        graphics.text(font, HardwareLabLanguage.text("gui.settings.language_help"),
                left + 20, top + 64, 0xFFB9C2CC, false);
    }

    private void updateButtons() {
        String selected = HardwareLabLanguage.text("gui.settings.selected");
        english.setMessage(Component.literal(HardwareLabLanguage.text("gui.settings.english")
                + (HardwareLabLanguage.isRussian() ? "" : "  [" + selected + "]")));
        russian.setMessage(Component.literal(HardwareLabLanguage.text("gui.settings.russian")
                + (HardwareLabLanguage.isRussian() ? "  [" + selected + "]" : "")));
        setTitle(HardwareLabLanguage.component("gui.settings.title"));
    }

    private void setTitle(Component title) {
        this.title = title;
    }
}
