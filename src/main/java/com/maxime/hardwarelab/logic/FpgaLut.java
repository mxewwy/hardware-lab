package com.maxime.hardwarelab.logic;

public final class FpgaLut {
    public enum Mode {
        AND4("AND4"),
        OR4("OR4"),
        XOR4("XOR4"),
        MUX("MUX");

        private final String displayName;

        Mode(String displayName) {
            this.displayName = displayName;
        }

        public String displayName() {
            return displayName;
        }

        public Mode next() {
            Mode[] values = values();
            return values[(ordinal() + 1) % values.length];
        }
    }

    private FpgaLut() {
    }

    public static int truthTable(Mode mode) {
        return switch (mode) {
            case AND4 -> 1 << 15;
            case OR4 -> 0xFFFE;
            case XOR4 -> parityTable();
            case MUX -> {
                int table = 0;
                for (int address = 0; address < 16; address++) {
                    boolean select = (address & 8) != 0;
                    boolean left = (address & 2) != 0;
                    boolean right = (address & 1) != 0;
                    if (select ? left : right) {
                        table |= 1 << address;
                    }
                }
                yield table;
            }
        };
    }

    public static boolean evaluate(int truthTable, int inputAddress) {
        return ((truthTable >>> (inputAddress & 15)) & 1) != 0;
    }

    private static int parityTable() {
        int table = 0;
        for (int address = 0; address < 16; address++) {
            if ((Integer.bitCount(address) & 1) != 0) {
                table |= 1 << address;
            }
        }
        return table;
    }
}
