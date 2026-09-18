package com.maxime.hardwarelab.block;

import com.maxime.hardwarelab.logic.GateType;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

public class UniversalLogicGateBlock extends HorizontalDirectionalBlock {
    public static final EnumProperty<GateType> GATE_TYPE =
            EnumProperty.create("gate_type", GateType.class);

    public static final MapCodec<UniversalLogicGateBlock> CODEC =
            simpleCodec(UniversalLogicGateBlock::new);

    public UniversalLogicGateBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(FACING, net.minecraft.core.Direction.NORTH)
                .setValue(GATE_TYPE, GateType.AND));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(GATE_TYPE);
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hit
    ) {
        GateType next = state.getValue(GATE_TYPE).next();

        if (!level.isClientSide()) {
            level.setBlockAndUpdate(pos, state.setValue(GATE_TYPE, next));
        }

        if (level.isClientSide()) {
            player.sendOverlayMessage(Component.literal("Logic Gate: " + next.displayName()));
        }

        return InteractionResult.SUCCESS;
    }
}
