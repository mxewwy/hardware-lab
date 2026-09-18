package com.maxime.hardwarelab.logic;

public final class ClockDividerLogic {
    private int division = 2;
    private int risingEdges;
    private boolean previousInputHigh;
    private boolean outputHigh;

    public boolean update(Signal input) {
        boolean inputHigh = input.isHigh();
        boolean outputChanged = false;

        if (inputHigh && !previousInputHigh) {
            risingEdges++;

            if (risingEdges >= division) {
                risingEdges = 0;
                outputHigh = !outputHigh;
                outputChanged = true;
            }
        }

        previousInputHigh = inputHigh;
        return outputChanged;
    }

    public void setDivision(int division) {
        if (division != 2 && division != 4 && division != 8 && division != 16) {
            throw new IllegalArgumentException("Unsupported clock division: " + division);
        }

        this.division = division;
        this.risingEdges = 0;
        this.previousInputHigh = false;
        this.outputHigh = false;
    }

    public void restore(int division, int risingEdges, boolean previousInputHigh, boolean outputHigh) {
        if (division != 2 && division != 4 && division != 8 && division != 16) {
            division = 2;
        }

        this.division = division;
        this.risingEdges = Math.max(0, Math.min(risingEdges, division - 1));
        this.previousInputHigh = previousInputHigh;
        this.outputHigh = outputHigh;
    }

    public int division() {
        return division;
    }

    public int risingEdges() {
        return risingEdges;
    }

    public boolean previousInputHigh() {
        return previousInputHigh;
    }

    public boolean outputHigh() {
        return outputHigh;
    }
}
