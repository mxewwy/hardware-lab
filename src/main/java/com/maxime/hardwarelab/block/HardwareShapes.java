package com.maxime.hardwarelab.block;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class HardwareShapes {
    public static final VoxelShape COMPONENT = Shapes.box(1.0/16.0, 1.0/16.0, 1.0/16.0, 15.0/16.0, 15.0/16.0, 15.0/16.0);
    private HardwareShapes() {}
}
