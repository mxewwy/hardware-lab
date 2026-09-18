package com.maxime.hardwarelab.item;

import com.maxime.hardwarelab.client.OscilloscopeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public final class OscilloscopeItem extends Item {
    public OscilloscopeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide()) {
            Minecraft.getInstance().gui.setScreen(
                    new OscilloscopeScreen(
                            context.getClickedPos(),
                            context.getClickedFace()
                    )
            );
        }

        return InteractionResult.SUCCESS;
    }
}
