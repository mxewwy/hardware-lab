package com.maxime.hardwarelab.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class OscilloscopeScreen extends Screen {
    private static final int SAMPLE_COUNT = 96;
    private final BlockPos targetPos;
    private final Direction targetFace;
    private final boolean[] samples = new boolean[SAMPLE_COUNT];

    private int sampleIndex;
    private int sampleCount;
    private int currentSignal;

    public OscilloscopeScreen(BlockPos targetPos, Direction targetFace) {
        super(Component.literal("Hardware Lab Oscilloscope"));
        this.targetPos = targetPos.immutable();
        this.targetFace = targetFace;
    }

    @Override
    protected void init() {
        this.clearSamples();
    }

    @Override
    public void tick() {
        super.tick();

        Level level = this.minecraft.level;
        if (level == null) {
            return;
        }

        BlockState state = level.getBlockState(this.targetPos);
        this.currentSignal = state.getSignal(level, this.targetPos, this.targetFace);

        this.samples[this.sampleIndex] = this.currentSignal > 0;
        this.sampleIndex = (this.sampleIndex + 1) % SAMPLE_COUNT;
        this.sampleCount = Math.min(this.sampleCount + 1, SAMPLE_COUNT);
    }

    @Override
    public void extractRenderState(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float delta
    ) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        int left = 28;
        int top = 28;
        int right = this.width - 28;
        int bottom = this.height - 28;

        graphics.fill(left, top, right, bottom, 0xE6101317);
        graphics.fill(left + 10, top + 10, right - 10, top + 11, 0xFF2C343D);
        graphics.fill(left + 10, bottom - 11, right - 10, bottom - 10, 0xFF2C343D);

        graphics.text(
                this.font,
                "HARDWARE LAB // OSCILLOSCOPE",
                left + 16,
                top + 16,
                0xFFFFFFFF,
                true
        );

        String targetName = this.getTargetName();
        graphics.text(
                this.font,
                "SOURCE  " + targetName,
                left + 16,
                top + 32,
                0xFFB9C2CC,
                false
        );

        graphics.text(
                this.font,
                "FACE  " + this.targetFace.getName().toUpperCase(),
                left + 16,
                top + 44,
                0xFF7F8B96,
                false
        );

        int graphLeft = left + 18;
        int graphTop = top + 70;
        int graphRight = right - 18;
        int graphBottom = bottom - 58;
        int mid = (graphTop + graphBottom) / 2;

        graphics.fill(graphLeft, graphTop, graphRight, graphBottom, 0xFF0A0C0F);

        for (int y = graphTop + 20; y < graphBottom; y += 20) {
            graphics.fill(graphLeft, y, graphRight, y + 1, 0xFF171C21);
        }

        for (int x = graphLeft + 12; x < graphRight; x += 12) {
            graphics.fill(x, graphTop, x + 1, graphBottom, 0xFF171C21);
        }

        graphics.fill(graphLeft, mid, graphRight, mid + 1, 0xFF2E3943);

        if (this.sampleCount > 1) {
            int availableWidth = graphRight - graphLeft - 4;
            int start = (this.sampleIndex - this.sampleCount + SAMPLE_COUNT) % SAMPLE_COUNT;

            for (int i = 0; i < this.sampleCount; i++) {
                int sourceIndex = (start + i) % SAMPLE_COUNT;
                int x = graphLeft + 2 + (i * availableWidth / Math.max(1, SAMPLE_COUNT - 1));

                if (this.samples[sourceIndex]) {
                    graphics.fill(x, graphTop + 8, x + 2, mid, 0xFF4DE38B);
                } else {
                    graphics.fill(x, mid, x + 2, graphBottom - 8, 0xFF33404C);
                }
            }
        }

        graphics.text(
                this.font,
                "SIGNAL",
                left + 16,
                bottom - 40,
                0xFF7F8B96,
                false
        );

        graphics.text(
                this.font,
                this.currentSignal > 0 ? "HIGH" : "LOW",
                left + 66,
                bottom - 40,
                this.currentSignal > 0 ? 0xFF4DE38B : 0xFF88929D,
                true
        );

        graphics.text(
                this.font,
                "POWER " + this.currentSignal + "/15",
                left + 16,
                bottom - 25,
                0xFF7F8B96,
                false
        );

        graphics.text(
                this.font,
                "ESC  CLOSE",
                right - 92,
                bottom - 25,
                0xFF7F8B96,
                false
        );
    }

    private String getTargetName() {
        Level level = this.minecraft.level;
        if (level == null) {
            return "UNKNOWN";
        }

        return level.getBlockState(this.targetPos).getBlock().getName().getString();
    }

    private void clearSamples() {
        for (int i = 0; i < this.samples.length; i++) {
            this.samples[i] = false;
        }

        this.sampleIndex = 0;
        this.sampleCount = 0;
        this.currentSignal = 0;
    }
}
