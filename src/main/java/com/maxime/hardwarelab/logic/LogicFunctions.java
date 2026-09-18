package com.maxime.hardwarelab.logic;

import java.util.Arrays;

public final class LogicFunctions {
    private LogicFunctions() {
    }

    public static final LogicFunction AND = inputs ->
            Signal.of(Arrays.stream(inputs).allMatch(Signal::isHigh));

    public static final LogicFunction OR = inputs ->
            Signal.of(Arrays.stream(inputs).anyMatch(Signal::isHigh));

    public static final LogicFunction XOR = inputs ->
            Signal.of(Arrays.stream(inputs).filter(Signal::isHigh).count() % 2 == 1);

    public static final LogicFunction NAND = inputs ->
            Signal.of(!AND.evaluate(inputs).isHigh());

    public static final LogicFunction NOR = inputs ->
            Signal.of(!OR.evaluate(inputs).isHigh());

    public static final LogicFunction XNOR = inputs ->
            Signal.of(!XOR.evaluate(inputs).isHigh());

    public static final LogicFunction NOT = inputs ->
            Signal.of(!inputs[0].isHigh());

    public static final LogicFunction BUFFER = inputs -> inputs[0];
}
