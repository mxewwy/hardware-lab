package com.maxime.hardwarelab.logic;

@FunctionalInterface
public interface LogicFunction {
    Signal evaluate(Signal[] inputs);
}
