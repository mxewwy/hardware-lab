package com.maxime.hardwarelab.client;

import com.maxime.hardwarelab.item.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;

public final class HardwareLabClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            if (!level.isClientSide() || !player.getItemInHand(hand).is(ModItems.OSCILLOSCOPE)) {
                return InteractionResult.PASS;
            }

            Minecraft.getInstance().gui.setScreen(
                    new OscilloscopeScreen(hitResult.getBlockPos(), hitResult.getDirection())
            );

            return InteractionResult.SUCCESS;
        });
    }
}
