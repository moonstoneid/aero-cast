package com.moonstoneid.aerocast.common.eth;

import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;
import org.web3j.tx.exceptions.ContractCallException;

final class Web3jFix {

    private Web3jFix() {}

    @SuppressWarnings("unchecked")
    static <T extends Type, R> R convertResult(T result, Class<R> returnType) {
        if (result == null) {
            return null;
        }

        Object value = result.getValue();
        if (returnType.isAssignableFrom(result.getClass())) {
            return (R) result;
        } else if (returnType.isAssignableFrom(value.getClass())) {
            return (R) value;
        } else if (result.getClass().equals(Address.class) && returnType.equals(String.class)) {
            return (R) result.toString();
        } else {
            throw new ContractCallException(
                    "Unable to convert response: "
                            + value
                            + " to expected type: "
                            + returnType.getSimpleName());
        }
    }

}
