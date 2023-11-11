package com.moonstoneid.aerocast.aggregator.controller.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SSEEventVM<T> {

    public enum Command {
        REFRESH,
        INSERT
    }

    public Command cmd;
    public T data;

}