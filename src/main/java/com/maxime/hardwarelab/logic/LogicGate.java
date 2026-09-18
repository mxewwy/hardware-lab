package com.maxime.hardwarelab.logic;

import java.util.Arrays;

public final class LogicGate {
    private final LogicFunction function;
    private final Signal[] inputs;

    public LogicGate(LogicFunction function, int inputCount) {
        if (inputCount < 1) {
            throw new IllegalArgumentException("inputCount must be positive");
        }

        this.function = function;
        this.inputs = new Signal[inputCount];
        Arrays.fill(this.inputs, Signal.LOW);
    }

    public Signal evaluate() {
        return function.evaluate(inputs);
    }

    public Signal[] inputs() {
        return inputs.clone();
    }

    public void setInput(int index, Signal signal) {
        inputs[index] = signal;
    }
}
