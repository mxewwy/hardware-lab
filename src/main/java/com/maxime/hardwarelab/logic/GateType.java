package com.maxime.hardwarelab.logic;

import net.minecraft.util.StringRepresentable;

public enum GateType implements StringRepresentable {
    AND("AND", LogicFunctions.AND),
    OR("OR", LogicFunctions.OR),
    XOR("XOR", LogicFunctions.XOR),
    NAND("NAND", LogicFunctions.NAND),
    NOR("NOR", LogicFunctions.NOR),
    XNOR("XNOR", LogicFunctions.XNOR),
    NOT("NOT", LogicFunctions.NOT),
    BUFFER("BUFFER", LogicFunctions.BUFFER);

    private final String displayName;
    private final LogicFunction function;

    GateType(String displayName, LogicFunction function) {
        this.displayName = displayName;
        this.function = function;
    }

    public String displayName() {
        return displayName;
    }

    public LogicFunction function() {
        return function;
    }

    public GateType next() {
        GateType[] values = values();
        return values[(ordinal() + 1) % values.length];
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase();
    }
}
