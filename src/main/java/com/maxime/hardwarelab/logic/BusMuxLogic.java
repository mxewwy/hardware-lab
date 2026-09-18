package com.maxime.hardwarelab.logic;

public final class BusMuxLogic {
    private BusMuxLogic() {
    }

    public static BusSignal select(
            BusSignal inputA,
            BusSignal inputB,
            Signal select,
            BusWidth width
    ) {
        BusSignal selected = select.value() ? inputB : inputA;
        return selected == null
                ? BusSignal.zero(width)
                : selected.resized(width);
    }
}
