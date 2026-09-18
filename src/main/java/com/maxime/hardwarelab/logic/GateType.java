package com.maxime.hardwarelab.logic;

public enum GateType {
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
}
