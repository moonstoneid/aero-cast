package com.moonstoneid.web3feed.aggregator.controller.model;

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