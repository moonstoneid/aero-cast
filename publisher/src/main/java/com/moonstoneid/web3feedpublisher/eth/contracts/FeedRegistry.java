package com.moonstoneid.web3feedpublisher.eth.contracts;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.4.2.
 */
@SuppressWarnings("rawtypes")
public class FeedRegistry extends Contract {
    public static final String BINARY = "Bin file was not provided";

    public static final String FUNC_GETPUBLISHERCONTRACT = "getPublisherContract";

    public static final String FUNC_GETSUBSCRIBERCONTRACT = "getSubscriberContract";

    public static final String FUNC_PUBLISHERCONTRACTS = "publisherContracts";

    public static final String FUNC_SETUPPUBLISHER = "setupPublisher";

    public static final String FUNC_SETUPSUBSCRIBER = "setupSubscriber";

    public static final String FUNC_SUBSCRIBERCONTRACTS = "subscriberContracts";

    @Deprecated
    protected FeedRegistry(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected FeedRegistry(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected FeedRegistry(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected FeedRegistry(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<String> getPublisherContract() {
        final Function function = new Function(FUNC_GETPUBLISHERCONTRACT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> getSubscriberContract() {
        final Function function = new Function(FUNC_GETSUBSCRIBERCONTRACT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> publisherContracts(String param0) {
        final Function function = new Function(FUNC_PUBLISHERCONTRACTS, 
                Arrays.<Type>asList(new Address(160, param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> setupPublisher() {
        final Function function = new Function(
                FUNC_SETUPPUBLISHER, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setupSubscriber() {
        final Function function = new Function(
                FUNC_SETUPSUBSCRIBER, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> subscriberContracts(String param0) {
        final Function function = new Function(FUNC_SUBSCRIBERCONTRACTS, 
                Arrays.<Type>asList(new Address(160, param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    @Deprecated
    public static FeedRegistry load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new FeedRegistry(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static FeedRegistry load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new FeedRegistry(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static FeedRegistry load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new FeedRegistry(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static FeedRegistry load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new FeedRegistry(contractAddress, web3j, transactionManager, contractGasProvider);
    }
}
