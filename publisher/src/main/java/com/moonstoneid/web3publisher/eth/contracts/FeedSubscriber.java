package com.moonstoneid.web3publisher.eth.contracts;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.StaticStruct;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
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
public class FeedSubscriber extends Contract {
    public static final String BINARY = "Bin file was not provided";

    public static final String FUNC_GETREACTIONS = "getReactions";

    public static final String FUNC_GETSUBSCRIPTIONS = "getSubscriptions";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_REACT = "react";

    public static final String FUNC_SETOWNER = "setOwner";

    public static final String FUNC_SUBSCRIBE = "subscribe";

    public static final String FUNC_UNREACT = "unreact";

    public static final String FUNC_UNSUBSCRIBE = "unsubscribe";

    public static final Event CREATEREACTION_EVENT = new Event("CreateReaction", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event CREATESUBSCRIPTION_EVENT = new Event("CreateSubscription", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}));
    ;

    public static final Event REMOVEREACTION_EVENT = new Event("RemoveReaction", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event REMOVESUBSCRIPTION_EVENT = new Event("RemoveSubscription", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}));
    ;

    @Deprecated
    protected FeedSubscriber(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected FeedSubscriber(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected FeedSubscriber(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected FeedSubscriber(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static List<CreateReactionEventResponse> getCreateReactionEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = staticExtractEventParametersWithLog(CREATEREACTION_EVENT, transactionReceipt);
        ArrayList<CreateReactionEventResponse> responses = new ArrayList<CreateReactionEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            CreateReactionEventResponse typedResponse = new CreateReactionEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.pubAddress = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._pubItemNum = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<CreateReactionEventResponse> createReactionEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, CreateReactionEventResponse>() {
            @Override
            public CreateReactionEventResponse apply(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(CREATEREACTION_EVENT, log);
                CreateReactionEventResponse typedResponse = new CreateReactionEventResponse();
                typedResponse.log = log;
                typedResponse.pubAddress = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._pubItemNum = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<CreateReactionEventResponse> createReactionEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CREATEREACTION_EVENT));
        return createReactionEventFlowable(filter);
    }

    public static List<CreateSubscriptionEventResponse> getCreateSubscriptionEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = staticExtractEventParametersWithLog(CREATESUBSCRIPTION_EVENT, transactionReceipt);
        ArrayList<CreateSubscriptionEventResponse> responses = new ArrayList<CreateSubscriptionEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            CreateSubscriptionEventResponse typedResponse = new CreateSubscriptionEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.pubAddress = (String) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<CreateSubscriptionEventResponse> createSubscriptionEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, CreateSubscriptionEventResponse>() {
            @Override
            public CreateSubscriptionEventResponse apply(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(CREATESUBSCRIPTION_EVENT, log);
                CreateSubscriptionEventResponse typedResponse = new CreateSubscriptionEventResponse();
                typedResponse.log = log;
                typedResponse.pubAddress = (String) eventValues.getIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<CreateSubscriptionEventResponse> createSubscriptionEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CREATESUBSCRIPTION_EVENT));
        return createSubscriptionEventFlowable(filter);
    }

    public static List<RemoveReactionEventResponse> getRemoveReactionEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = staticExtractEventParametersWithLog(REMOVEREACTION_EVENT, transactionReceipt);
        ArrayList<RemoveReactionEventResponse> responses = new ArrayList<RemoveReactionEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            RemoveReactionEventResponse typedResponse = new RemoveReactionEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.pubAddress = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._pubItemNum = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<RemoveReactionEventResponse> removeReactionEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, RemoveReactionEventResponse>() {
            @Override
            public RemoveReactionEventResponse apply(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(REMOVEREACTION_EVENT, log);
                RemoveReactionEventResponse typedResponse = new RemoveReactionEventResponse();
                typedResponse.log = log;
                typedResponse.pubAddress = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._pubItemNum = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<RemoveReactionEventResponse> removeReactionEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(REMOVEREACTION_EVENT));
        return removeReactionEventFlowable(filter);
    }

    public static List<RemoveSubscriptionEventResponse> getRemoveSubscriptionEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = staticExtractEventParametersWithLog(REMOVESUBSCRIPTION_EVENT, transactionReceipt);
        ArrayList<RemoveSubscriptionEventResponse> responses = new ArrayList<RemoveSubscriptionEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            RemoveSubscriptionEventResponse typedResponse = new RemoveSubscriptionEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.pubAddress = (String) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<RemoveSubscriptionEventResponse> removeSubscriptionEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, RemoveSubscriptionEventResponse>() {
            @Override
            public RemoveSubscriptionEventResponse apply(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(REMOVESUBSCRIPTION_EVENT, log);
                RemoveSubscriptionEventResponse typedResponse = new RemoveSubscriptionEventResponse();
                typedResponse.log = log;
                typedResponse.pubAddress = (String) eventValues.getIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<RemoveSubscriptionEventResponse> removeSubscriptionEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(REMOVESUBSCRIPTION_EVENT));
        return removeSubscriptionEventFlowable(filter);
    }

    public RemoteFunctionCall<List> getReactions() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETREACTIONS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Reaction>>() {}));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<List> getSubscriptions() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETSUBSCRIPTIONS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Subscription>>() {}));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<String> owner() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> react(String pubAddr, BigInteger pubItemNum, BigInteger rating) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_REACT, 
                Arrays.<Type>asList(new Address(160, pubAddr),
                new Uint256(pubItemNum),
                new Uint8(rating)),
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

    public RemoteFunctionCall<TransactionReceipt> subscribe(String pubAddr) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_SUBSCRIBE, 
                Arrays.<Type>asList(new Address(160, pubAddr)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> unreact(String pubAddr, BigInteger pubItemNum) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_UNREACT, 
                Arrays.<Type>asList(new Address(160, pubAddr),
                new Uint256(pubItemNum)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> unsubscribe(String pubAddr) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_UNSUBSCRIBE, 
                Arrays.<Type>asList(new Address(160, pubAddr)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static FeedSubscriber load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new FeedSubscriber(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static FeedSubscriber load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new FeedSubscriber(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static FeedSubscriber load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new FeedSubscriber(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static FeedSubscriber load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new FeedSubscriber(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class Reaction extends StaticStruct {
        public String pubAddress;

        public BigInteger pubItemNum;

        public BigInteger rating;

        public BigInteger timestamp;

        public Reaction(String pubAddress, BigInteger pubItemNum, BigInteger rating, BigInteger timestamp) {
            super(new Address(160, pubAddress),
                    new Uint256(pubItemNum),
                    new Uint8(rating),
                    new Uint256(timestamp));
            this.pubAddress = pubAddress;
            this.pubItemNum = pubItemNum;
            this.rating = rating;
            this.timestamp = timestamp;
        }

        public Reaction(Address pubAddress, Uint256 pubItemNum, Uint8 rating, Uint256 timestamp) {
            super(pubAddress, pubItemNum, rating, timestamp);
            this.pubAddress = pubAddress.getValue();
            this.pubItemNum = pubItemNum.getValue();
            this.rating = rating.getValue();
            this.timestamp = timestamp.getValue();
        }
    }

    public static class Subscription extends StaticStruct {
        public String pubAddress;

        public BigInteger timestamp;

        public Subscription(String pubAddress, BigInteger timestamp) {
            super(new Address(160, pubAddress),
                    new Uint256(timestamp));
            this.pubAddress = pubAddress;
            this.timestamp = timestamp;
        }

        public Subscription(Address pubAddress, Uint256 timestamp) {
            super(pubAddress, timestamp);
            this.pubAddress = pubAddress.getValue();
            this.timestamp = timestamp.getValue();
        }
    }

    public static class CreateReactionEventResponse extends BaseEventResponse {
        public String pubAddress;

        public BigInteger _pubItemNum;
    }

    public static class CreateSubscriptionEventResponse extends BaseEventResponse {
        public String pubAddress;
    }

    public static class RemoveReactionEventResponse extends BaseEventResponse {
        public String pubAddress;

        public BigInteger _pubItemNum;
    }

    public static class RemoveSubscriptionEventResponse extends BaseEventResponse {
        public String pubAddress;
    }
}
