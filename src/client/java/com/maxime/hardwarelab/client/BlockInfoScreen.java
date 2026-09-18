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
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class BlockInfoScreen extends Screen {
    private final BlockPos targetPos;
    private final Direction targetFace;
    private final String path;

    public BlockInfoScreen(BlockPos targetPos, Direction targetFace, String path) {
        super(HardwareLabLanguage.component("gui.info.title"));
        this.targetPos = targetPos.immutable();
        this.targetFace = targetFace;
        this.path = path;
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        if (input.key() == InputConstants.KEY_H) {
            minecraft.gui.setScreen(new HardwareGuideScreen());
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        graphics.fill(0, 0, width, height, 0xB8090C10);

        int panelWidth = Math.min(700, width - 40);
        int left = (width - panelWidth) / 2;
        int top = 28;
        int right = left + panelWidth;
        int bottom = Math.min(height - 28, top + 330);

        graphics.fill(left, top, right, bottom, 0xF013171C);
        graphics.fill(left, top, right, top + 3, 0xFF39D5FF);

        graphics.text(font, HardwareLabLanguage.blockName(path), left + 18, top + 18, 0xFFFFFFFF, true);
        graphics.text(font, HardwareLabLanguage.text("gui.info.face"), left + 18, top + 42, 0xFF7F8B96, false);
        graphics.text(font, targetFace.getName().toUpperCase(), left + 130, top + 42, 0xFFB9C2CC, true);

        int signal = readSignal();
        graphics.text(font, HardwareLabLanguage.text("gui.info.live"), left + 18, top + 74, 0xFF4DE38B, true);
        graphics.text(font, signal > 0 ? "HIGH (" + signal + "/15)" : "LOW (0/15)",
                left + 128, top + 74, signal > 0 ? 0xFF4DE38B : 0xFF8A949F, true);

        graphics.text(font, HardwareLabLanguage.text("gui.info.control"), left + 18, top + 112, 0xFF7F8B96, false);
        graphics.text(font, HardwareLabLanguage.control(path), left + 18, top + 128, 0xFFB9C2CC, false);

        graphics.text(font, HardwareLabLanguage.text("gui.info.readout"), left + 18, top + 159, 0xFF4DE38B, true);
        graphics.text(font, liveReadout(), left + 18, top + 177, 0xFFB9C2CC, false);

        graphics.fill(left + 18, top + 198, right - 18, top + 200, 0xFF2C343D);
        graphics.text(font, HardwareLabLanguage.text("gui.info.hint"), left + 18, top + 216, 0xFFB9C2CC, false);


        graphics.text(font, HardwareLabLanguage.text("gui.info.guide"), left + 18, bottom - 18, 0xFF7F8B96, false);
        graphics.text(font, HardwareLabLanguage.text("gui.info.close"), right - 86, bottom - 18, 0xFF7F8B96, false);
    }

    private String liveReadout() {
        Level level = minecraft.level;
        if (level == null) return "-";

        BlockState state = level.getBlockState(targetPos);
        if (state.getBlock() instanceof DigitalWireBlock wire) {
            StringBuilder links = new StringBuilder();
            addConnection(links, state, "N", DigitalWireBlock.NORTH);
            addConnection(links, state, "E", DigitalWireBlock.EAST);
            addConnection(links, state, "S", DigitalWireBlock.SOUTH);
            addConnection(links, state, "W", DigitalWireBlock.WEST);
            addConnection(links, state, "U", DigitalWireBlock.UP);
            addConnection(links, state, "D", DigitalWireBlock.DOWN);
            return "WIRE=" + (state.getValue(DigitalWireBlock.POWERED) ? "HIGH" : "LOW")
                    + "  LINKS=" + (links.isEmpty() ? "-" : links);
        }

        if (state.getBlock() instanceof UniversalLogicGateBlock) {
            var inputs = UniversalLogicGateBlock.getInputs(state, level, targetPos);
            return "A=" + inputs[0].value() + "  B=" + inputs[1].value()
                    + "  OUT=" + UniversalLogicGateBlock.evaluate(state, level, targetPos).value();
        }

        if (state.getBlock() instanceof BusOutputBlock output) {
            BusSignal signal = output.getBusOutput(level, targetPos, state);
            return signal == null
                    ? "BUS=DISCONNECTED"
                    : "WIDTH=" + signal.width().bits() + "  VALUE=0x" + signal.hex();
        }

        return "SIGNAL=" + readSignal();
    }

    private int readSignal() {
        Level level = minecraft.level;
        if (level == null) return 0;

        BlockState state = level.getBlockState(targetPos);
        if (state.getBlock() instanceof DigitalWireBlock) {
            return state.getValue(DigitalWireBlock.POWERED) ? 15 : 0;
        }
        return state.getSignal(level, targetPos, targetFace);
    }

    private static void addConnection(StringBuilder out, BlockState state, String label,
                                      net.minecraft.world.level.block.state.properties.BooleanProperty property) {
        if (!state.hasProperty(property) || !state.getValue(property)) return;
        if (!out.isEmpty()) out.append(' ');
        out.append(label);
    }
}
