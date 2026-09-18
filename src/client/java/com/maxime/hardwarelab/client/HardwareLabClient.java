package com.maxime.hardwarelab.client;

import com.maxime.hardwarelab.HardwareLab;
import com.maxime.hardwarelab.HardwareLabLanguage;
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

    private static final Map<String, Boolean> MOD_ITEMS = Map.ofEntries(
            Map.entry("logic_probe", true), Map.entry("oscilloscope", true),
            Map.entry("universal_logic_gate", true), Map.entry("digital_wire", true),
            Map.entry("redstone_input", true), Map.entry("redstone_output", true),
            Map.entry("clock_generator", true), Map.entry("clock_divider", true),
            Map.entry("d_flip_flop", true), Map.entry("digital_bus", true),
            Map.entry("bus_mux", true), Map.entry("bus_splitter", true),
            Map.entry("bus_merger", true), Map.entry("bus_driver", true),
            Map.entry("adc", true), Map.entry("dac", true),
            Map.entry("eight_bit_register", true), Map.entry("ram_256", true),
            Map.entry("rom_256", true), Map.entry("seven_segment_display", true),
            Map.entry("led_matrix", true), Map.entry("cpu", true), Map.entry("fpga", true)
    );

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (GUIDE_KEY.consumeClick()) {
                if (client.player != null) client.gui.setScreen(new HardwareGuideScreen());
            }
        });

        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            if (!level.isClientSide()) return InteractionResult.PASS;

            if (player.getItemInHand(hand).is(ModItems.OSCILLOSCOPE)) {
                Minecraft.getInstance().gui.setScreen(
                        new OscilloscopeScreen(hitResult.getBlockPos(), hitResult.getDirection()));
                return InteractionResult.SUCCESS;
            }

            if (player.getItemInHand(hand).is(ModItems.LOGIC_PROBE)) {
                String path = BuiltInRegistries.BLOCK.getKey(
                        level.getBlockState(hitResult.getBlockPos()).getBlock()).getPath();
                Minecraft.getInstance().gui.setScreen(
                        new BlockInfoScreen(hitResult.getBlockPos(), hitResult.getDirection(), path));
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
        });

        ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipFlag, lines) -> {
            Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
            if (!HardwareLab.MOD_ID.equals(id.getNamespace()) || !MOD_ITEMS.containsKey(id.getPath())) return;

            lines.add(Component.literal(" "));
            lines.add(HardwareLabLanguage.component("gui.tooltip.header"));
            lines.add(Component.literal(HardwareLabLanguage.control(id.getPath())));
            lines.add(HardwareLabLanguage.component("gui.tooltip.guide"));
        });
    }
}
