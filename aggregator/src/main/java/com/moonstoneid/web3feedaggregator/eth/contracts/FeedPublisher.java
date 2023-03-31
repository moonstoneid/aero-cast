package com.moonstoneid.web3feedaggregator.eth.contracts;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicStruct;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
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
public class FeedPublisher extends Contract {
    public static final String BINARY = "Bin file was not provided";

    public static final String FUNC_GETPUBITEM = "getPubItem";

    public static final String FUNC_GETTOTALPUBITEMCOUNT = "getTotalPubItemCount";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_PUBLISH = "publish";

    public static final String FUNC_SETOWNER = "setOwner";

    public static final Event NEWPUBITEM_EVENT = new Event("NewPubItem", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}));
    ;

    @Deprecated
    protected FeedPublisher(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected FeedPublisher(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected FeedPublisher(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected FeedPublisher(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static List<NewPubItemEventResponse> getNewPubItemEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = staticExtractEventParametersWithLog(NEWPUBITEM_EVENT, transactionReceipt);
        ArrayList<NewPubItemEventResponse> responses = new ArrayList<NewPubItemEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            NewPubItemEventResponse typedResponse = new NewPubItemEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.num = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<NewPubItemEventResponse> newPubItemEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, NewPubItemEventResponse>() {
            @Override
            public NewPubItemEventResponse apply(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(NEWPUBITEM_EVENT, log);
                NewPubItemEventResponse typedResponse = new NewPubItemEventResponse();
                typedResponse.log = log;
                typedResponse.num = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<NewPubItemEventResponse> newPubItemEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(NEWPUBITEM_EVENT));
        return newPubItemEventFlowable(filter);
    }

    public RemoteFunctionCall<PubItem> getPubItem(BigInteger num) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETPUBITEM, 
                Arrays.<Type>asList(new Uint256(num)),
                Arrays.<TypeReference<?>>asList(new TypeReference<PubItem>() {}));
        return executeRemoteCallSingleValueReturn(function, PubItem.class);
    }

    public RemoteFunctionCall<BigInteger> getTotalPubItemCount() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETTOTALPUBITEMCOUNT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> owner() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> publish(String data) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_PUBLISH, 
                Arrays.<Type>asList(new Utf8String(data)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setOwner(String _newOwner) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_SETOWNER, 
                Arrays.<Type>asList(new Address(160, _newOwner)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static FeedPublisher load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new FeedPublisher(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static FeedPublisher load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new FeedPublisher(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static FeedPublisher load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new FeedPublisher(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static FeedPublisher load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new FeedPublisher(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class PubItem extends DynamicStruct {
        public BigInteger num;

        public BigInteger timestamp;

        public String data;

        public PubItem(BigInteger num, BigInteger timestamp, String data) {
            super(new Uint256(num),
                    new Uint256(timestamp),
                    new Utf8String(data));
            this.num = num;
            this.timestamp = timestamp;
            this.data = data;
        }

        public PubItem(Uint256 num, Uint256 timestamp, Utf8String data) {
            super(num, timestamp, data);
            this.num = num.getValue();
            this.timestamp = timestamp.getValue();
            this.data = data.getValue();
        }
    }

    public static class NewPubItemEventResponse extends BaseEventResponse {
        public BigInteger num;
    }
}
