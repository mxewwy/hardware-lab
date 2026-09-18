package com.maxime.hardwarelab.client;

import com.maxime.hardwarelab.HardwareLab;
import com.maxime.hardwarelab.item.ModItems;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionResult;

import java.util.Map;

public final class HardwareLabClient implements ClientModInitializer {
    private static final KeyMapping GUIDE_KEY = KeyMappingHelper.registerKeyMapping(
            new KeyMapping(
                    "key.hardware_lab.open_guide",
                    InputConstants.Type.KEYSYM,
                    InputConstants.KEY_H,
                    KeyMapping.Category.register(
                            Identifier.fromNamespaceAndPath(HardwareLab.MOD_ID, "controls")
                    )
            )
    );

    private static final Map<String, String> CONTROLS = Map.ofEntries(
            Map.entry("universal_logic_gate", "Right-click: next logic function"),
            Map.entry("digital_wire", "Automatic HIGH / LOW carrier"),
            Map.entry("redstone_input", "Reads redstone on the input face"),
            Map.entry("redstone_output", "Drives redstone on the output face"),
            Map.entry("clock_generator", "Right-click: change clock rate"),
            Map.entry("clock_divider", "Right-click: change division"),
            Map.entry("d_flip_flop", "D + CLK in, Q out"),
            Map.entry("digital_bus", "Right-click: test pattern | Shift + right-click: width"),
            Map.entry("bus_mux", "Right-click: width | SELECT is redstone"),
            Map.entry("bus_splitter", "Right-click: bank | Shift + right-click: width"),
            Map.entry("bus_merger", "Right-click: bank | Shift + right-click: width"),
            Map.entry("bus_driver", "Side redstone input = enable"),
            Map.entry("adc", "Redstone power -> 4-bit bus"),
            Map.entry("dac", "4-bit bus -> redstone power"),
            Map.entry("eight_bit_register", "Latches an 8-bit value on load/clock"),
            Map.entry("ram_256", "Address selects byte | Shift + right-click: clear"),
            Map.entry("rom_256", "Right-click: cycle demo pattern"),
            Map.entry("seven_segment_display", "Displays a 4-bit hexadecimal digit"),
            Map.entry("led_matrix", "Displays an 8-bit matrix row"),
            Map.entry("cpu", "Right-click: program | Shift + right-click: speed"),
            Map.entry("fpga", "Right-click: LUT mode | Shift + right-click: register"),
            Map.entry("logic_probe", "Right-click a component: info + live readout"),
            Map.entry("oscilloscope", "Right-click a signal source: waveform viewer")
    );

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (GUIDE_KEY.consumeClick()) {
                if (client.player != null) {
                    client.gui.setScreen(new HardwareGuideScreen());
                }
            }
        });

        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            if (!level.isClientSide()) {
                return InteractionResult.PASS;
            }

            if (player.getItemInHand(hand).is(ModItems.OSCILLOSCOPE)) {
                Minecraft.getInstance().gui.setScreen(
                        new OscilloscopeScreen(hitResult.getBlockPos(), hitResult.getDirection())
                );
                return InteractionResult.SUCCESS;
            }

            if (player.getItemInHand(hand).is(ModItems.LOGIC_PROBE)) {
                String path = BuiltInRegistries.BLOCK.getKey(
                        level.getBlockState(hitResult.getBlockPos()).getBlock()
                ).getPath();

                Minecraft.getInstance().gui.setScreen(
                        new BlockInfoScreen(hitResult.getBlockPos(), hitResult.getDirection(), path)
                );
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
        });

        ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipFlag, lines) -> {
            Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
            if (!HardwareLab.MOD_ID.equals(id.getNamespace())) {
                return;
            }

            String control = CONTROLS.get(id.getPath());
            if (control == null) {
                return;
            }

            lines.add(Component.literal(" "));
            lines.add(Component.literal("Hardware Lab"));
            lines.add(Component.literal(control));
            lines.add(Component.literal("H = Hardware Guide"));
        });
    }
}
