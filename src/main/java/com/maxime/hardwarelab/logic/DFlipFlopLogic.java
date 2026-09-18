package com.maxime.hardwarelab.logic;

public final class DFlipFlopLogic {
    private boolean previousClockHigh;
    private boolean q;

    public boolean update(Signal data, Signal clock) {
        boolean clockHigh = clock.isHigh();
        boolean changed = false;

        if (clockHigh && !previousClockHigh) {
            boolean nextQ = data.isHigh();
            changed = nextQ != q;
            q = nextQ;
        }

        previousClockHigh = clockHigh;
        return changed;
    }

    public void restore(boolean previousClockHigh, boolean q) {
        this.previousClockHigh = previousClockHigh;
        this.q = q;
    }

    public boolean previousClockHigh() {
        return previousClockHigh;
    }

    public boolean q() {
        return q;
    }
}
