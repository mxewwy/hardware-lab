package com.maxime.hardwarelab.client;

import com.maxime.hardwarelab.HardwareLabLanguage;
import com.maxime.hardwarelab.block.BusOutputBlock;
import com.maxime.hardwarelab.block.DigitalWireBlock;
import com.maxime.hardwarelab.block.UniversalLogicGateBlock;
import com.maxime.hardwarelab.logic.BusSignal;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
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
        super(HardwareLabLanguage.component("gui.scope.title"));
        this.targetPos = targetPos.immutable();
        this.targetFace = targetFace;
    }

    @Override
    protected void init() {
        clearSamples();
    }

    @Override
    public void tick() {
        super.tick();
        Level level = minecraft.level;
        if (level == null) return;

        BlockState state = level.getBlockState(targetPos);
        currentSignal = readSignal(level, state);
        samples[sampleIndex] = currentSignal > 0;
        sampleIndex = (sampleIndex + 1) % SAMPLE_COUNT;
        sampleCount = Math.min(sampleCount + 1, SAMPLE_COUNT);
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        if (input.key() == InputConstants.KEY_R) {
            clearSamples();
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        int left = 22, top = 20, right = width - 22, bottom = height - 20;
        graphics.fill(0, 0, width, height, 0xB80B0E12);
        graphics.fill(left, top, right, bottom, 0xF013171C);
        graphics.fill(left, top, right, top + 2, 0xFF4DE38B);

        graphics.text(font, HardwareLabLanguage.text("gui.scope.title"), left + 16, top + 14, 0xFFFFFFFF, true);
        graphics.text(font, HardwareLabLanguage.text("gui.scope.source") + "  " + getTargetName(),
                left + 16, top + 31, 0xFFB9C2CC, false);
        graphics.text(font, HardwareLabLanguage.text("gui.scope.face") + "  " + targetFace.getName().toUpperCase(),
                left + 16, top + 43, 0xFF7F8B96, false);

        int graphLeft = left + 16, graphTop = top + 62, graphRight = right - 16, graphBottom = bottom - 65;
        int mid = (graphTop + graphBottom) / 2;
        graphics.fill(graphLeft, graphTop, graphRight, graphBottom, 0xFF090B0E);

        for (int y = graphTop + 20; y < graphBottom; y += 20)
            graphics.fill(graphLeft, y, graphRight, y + 1, 0xFF171C21);
        for (int x = graphLeft + 12; x < graphRight; x += 12)
            graphics.fill(x, graphTop, x + 1, graphBottom, 0xFF171C21);
        graphics.fill(graphLeft, mid, graphRight, mid + 1, 0xFF36424E);

        if (sampleCount > 1) {
            int availableWidth = graphRight - graphLeft - 4;
            int start = (sampleIndex - sampleCount + SAMPLE_COUNT) % SAMPLE_COUNT;
            for (int i = 0; i < sampleCount; i++) {
                int sourceIndex = (start + i) % SAMPLE_COUNT;
                int x = graphLeft + 2 + (i * availableWidth / Math.max(1, SAMPLE_COUNT - 1));
                if (samples[sourceIndex])
                    graphics.fill(x, graphTop + 8, x + 2, mid, 0xFF4DE38B);
                else
                    graphics.fill(x, mid, x + 2, graphBottom - 8, 0xFF33404C);
            }
        }

        graphics.text(font, HardwareLabLanguage.text("gui.scope.live"), left + 16, bottom - 47, 0xFF7F8B96, false);
        graphics.text(font, currentSignal > 0
                ? HardwareLabLanguage.text("gui.scope.high")
                : HardwareLabLanguage.text("gui.scope.low"),
                left + 114, bottom - 47,
                currentSignal > 0 ? 0xFF4DE38B : 0xFF88929D, true);
        graphics.text(font, HardwareLabLanguage.text("gui.scope.power") + " " + currentSignal + "/15",
                left + 16, bottom - 30, 0xFFB9C2CC, false);
        graphics.text(font, HardwareLabLanguage.text("gui.scope.reset") + "   "
                + HardwareLabLanguage.text("gui.scope.close"),
                right - 160, bottom - 30, 0xFF7F8B96, false);
    }

    private int readSignal(Level level, BlockState state) {
        if (state.getBlock() instanceof DigitalWireBlock) {
            return state.getValue(DigitalWireBlock.POWERED) ? 15 : 0;
        }
        if (state.getBlock() instanceof UniversalLogicGateBlock gate) {
            return gate.evaluate(state, level, targetPos).isHigh() ? 15 : 0;
        }
        if (state.getBlock() instanceof BusOutputBlock output) {
            BusSignal signal = output.getBusOutput(level, targetPos, state);
            return signal == null ? 0 : (signal.value() == 0 ? 0 : 15);
        }
        return state.getSignal(level, targetPos, targetFace);
    }

    private String getTargetName() {
        Level level = minecraft.level;
        if (level == null) return "UNKNOWN";
        Identifier id = BuiltInRegistries.BLOCK.getKey(level.getBlockState(targetPos).getBlock());
        return HardwareLabLanguage.blockName(id.getPath());
    }

    private void clearSamples() {
        java.util.Arrays.fill(samples, false);
        sampleIndex = 0;
        sampleCount = 0;
        currentSignal = 0;
    }
}
