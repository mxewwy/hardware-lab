package com.maxime.hardwarelab.logic;

public enum DivisionFactor {
    DIVIDE_BY_2("DIV2", 2),
    DIVIDE_BY_4("DIV4", 4),
    DIVIDE_BY_8("DIV8", 8),
    DIVIDE_BY_16("DIV16", 16);

    private final String displayName;
    private final int factor;

    DivisionFactor(String displayName, int factor) {
        this.displayName = displayName;
        this.factor = factor;
    }

    public String displayName() {
        return displayName;
    }

    public int factor() {
        return factor;
    }

    public DivisionFactor next() {
        DivisionFactor[] values = values();
        return values[(ordinal() + 1) % values.length];
    }
}
