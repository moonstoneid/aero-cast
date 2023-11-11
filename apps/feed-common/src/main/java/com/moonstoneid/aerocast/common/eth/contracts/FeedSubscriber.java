package com.moonstoneid.aerocast.common.eth.contracts;

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
 * <p>Generated with web3j version 1.4.1.
 */
@SuppressWarnings("rawtypes")
public class FeedSubscriber extends Contract {
    public static final String BINARY = "608060405234801561001057600080fd5b50600080546001600160a01b0319163317905561104a806100326000396000f3fe608060405234801561001057600080fd5b50600436106100885760003560e01c80637262561c1161005b5780637262561c146100e85780637358152a146100fb5780638b46a2b41461010e5780638da5cb5b1461012157600080fd5b80630472f4181461008d57806313af4035146100ab5780633b47a9ac146100c057806341a7726a146100d5575b600080fd5b61009561013c565b6040516100a29190610c87565b60405180910390f35b6100be6100b9366004610d2e565b6101f5565b005b6100c861024a565b6040516100a29190610d50565b6100be6100e3366004610d2e565b6102b6565b6100be6100f6366004610d2e565b610478565b6100be610109366004610da8565b610660565b6100be61011c366004610dd2565b61090b565b6000546040516001600160a01b0390911681526020016100a2565b60606003805480602002602001604051908101604052809291908181526020016000905b828210156101ec576000848152602090819020604080516080810182526004860290920180546001600160a01b03168352600180820154948401949094526002810154929390929184019160ff16908111156101be576101be610c71565b60018111156101cf576101cf610c71565b815260200160038201548152505081526020019060010190610160565b50505050905090565b6000546001600160a01b031633146102285760405162461bcd60e51b815260040161021f90610e16565b60405180910390fd5b600080546001600160a01b0319166001600160a01b0392909216919091179055565b60606001805480602002602001604051908101604052809291908181526020016000905b828210156101ec576000848152602090819020604080518082019091526002850290910180546001600160a01b0316825260019081015482840152908352909201910161026e565b6000546001600160a01b031633146102e05760405162461bcd60e51b815260040161021f90610e16565b604080518082019091526001600160a01b0382168152426020820152600061030783610baa565b6001600160a01b0381166000908152600260205260409020549091508015610385578260016103368184610e5e565b8154811061034657610346610e7f565b600091825260209182902083516002929092020180546001600160a01b0319166001600160a01b0390921691909117815591015160019091015561041b565b50600180548082018255600082815284517fb10e2d527612073b26eecdfd717e6a320cf44b4afac2b0732d9fcbe2b7fa0cf6600293840290810180546001600160a01b0319166001600160a01b039384161790556020808801517fb10e2d527612073b26eecdfd717e6a320cf44b4afac2b0732d9fcbe2b7fa0cf790920191909155935490851682529190925260409091208190555b61043e604051806060016040528060308152602001610f5b603091393386610bb4565b6040516001600160a01b038516907f6ff6eaed699c246ce4a81c625294e51377e7c325c1c73e5bfa3b44be72806d0290600090a250505050565b6000546001600160a01b031633146104a25760405162461bcd60e51b815260040161021f90610e16565b60006104ad82610baa565b6001600160a01b0381166000908152600260205260408120549192508190036104d557505050565b6001600160a01b0382166000908152600260205260408120556001805411156105c757600180546000919061050b908290610e5e565b8154811061051b5761051b610e7f565b600091825260208083206040805180820190915260029093020180546001600160a01b03168084526001909101549183019190915290925061055c90610baa565b905081600161056b8186610e5e565b8154811061057b5761057b610e7f565b600091825260208083208451600293840290910180546001600160a01b0319166001600160a01b0392831617815594820151600190950194909455939092168152915260409020829055505b60018054806105d8576105d8610e95565b6000828152602080822060026000199094019384020180546001600160a01b03191681556001019190915591556040805160608101909152603080825261062792610fe5908301393385610bb4565b6040516001600160a01b038416907fd4da1c95251a1c543838518bae8531facb525bc74cf1ac0ed8fb308479958c2e90600090a2505050565b6000546001600160a01b0316331461068a5760405162461bcd60e51b815260040161021f90610e16565b6000601083901b62010000600160b01b031682176001600160a01b0381166000908152600460205260408120549192508190036106c75750505050565b6001600160a01b0382166000908152600460205260408120556003546001101561084e5760038054600091906106ff90600190610e5e565b8154811061070f5761070f610e7f565b600091825260209182902060408051608081018252600490930290910180546001600160a01b03168352600180820154948401949094526002810154929390929184019160ff169081111561076657610766610c71565b600181111561077757610777610c71565b815260039182015460209182015282519083015192935060101b62010000600160b01b03169091179082906107ad600186610e5e565b815481106107bd576107bd610e7f565b600091825260209182902083516004929092020180546001600160a01b0319166001600160a01b039092169190911781559082015160018083019190915560408301516002830180549192909160ff191690838181111561082057610820610c71565b0217905550606091909101516003909101556001600160a01b03166000908152600460205260409020829055505b600380548061085f5761085f610e95565b6000828152602080822060046000199094019384020180546001600160a01b03191681556001810183905560028101805460ff191690556003019190915591556040805160608101909152602d8082526108c292610fb890830139338686610c00565b836001600160a01b03167f8f22061a7389d6620c735385174537c8baaf1439735a4a569906d00f95c04581846040516108fd91815260200190565b60405180910390a250505050565b6000546001600160a01b031633146109355760405162461bcd60e51b815260040161021f90610e16565b60006040518060800160405280856001600160a01b0316815260200184815260200183600181111561096957610969610c71565b81524260209091015290506000601085901b62010000600160b01b031684176001600160a01b0381166000908152600460205260409020549091508015610a42578260036109b8600184610e5e565b815481106109c8576109c8610e7f565b600091825260209182902083516004929092020180546001600160a01b0319166001600160a01b039092169190911781559082015160018083019190915560408301516002830180549192909160ff1916908381811115610a2b57610a2b610c71565b021790555060608201518160030155905050610b3b565b6003805460018181018355600092909252845160049091027fc2575a0e9e593c00f959f8c92f12db2869c3395a3b0502d05e2516446f71f85b810180546001600160a01b039093166001600160a01b031990931692909217825560208601517fc2575a0e9e593c00f959f8c92f12db2869c3395a3b0502d05e2516446f71f85c82015560408601517fc2575a0e9e593c00f959f8c92f12db2869c3395a3b0502d05e2516446f71f85d9091018054879460ff19909116908381811115610b0a57610b0a610c71565b021790555060609190910151600391820155546001600160a01b038316600090815260046020526040902081905590505b610b5f6040518060600160405280602d8152602001610f8b602d9139338888610c00565b856001600160a01b03167f66ddc840fe0c0e2d79e178ffe79c6ede8c6fcf1c61cf1edb4adb1d1e40ee327886604051610b9a91815260200190565b60405180910390a2505050505050565b6000815b92915050565b610bfb838383604051602401610bcc93929190610ef1565b60408051601f198184030181529190526020810180516001600160e01b03166307e763af60e51b179052610c4f565b505050565b610c4984848484604051602401610c1a9493929190610f24565b60408051601f198184030181529190526020810180516001600160e01b0316638ef3f39960e01b179052610c4f565b50505050565b80516a636f6e736f6c652e6c6f6790602083016000808383865afa5050505050565b634e487b7160e01b600052602160045260246000fd5b60208082528251828201819052600091906040908185019086840185805b83811015610d0457825180516001600160a01b0316865287810151888701528681015160028110610ce457634e487b7160e01b84526021600452602484fd5b868801526060908101519086015260809094019391860191600101610ca5565b509298975050505050505050565b80356001600160a01b0381168114610d2957600080fd5b919050565b600060208284031215610d4057600080fd5b610d4982610d12565b9392505050565b602080825282518282018190526000919060409081850190868401855b82811015610d9b57815180516001600160a01b03168552860151868501529284019290850190600101610d6d565b5091979650505050505050565b60008060408385031215610dbb57600080fd5b610dc483610d12565b946020939093013593505050565b600080600060608486031215610de757600080fd5b610df084610d12565b925060208401359150604084013560028110610e0b57600080fd5b809150509250925092565b60208082526028908201527f43616c6c6572206f66207468652066756e6374696f6e206973206e6f7420746860408201526765206f776e65722160c01b606082015260800190565b81810381811115610bae57634e487b7160e01b600052601160045260246000fd5b634e487b7160e01b600052603260045260246000fd5b634e487b7160e01b600052603160045260246000fd5b6000815180845260005b81811015610ed157602081850181015186830182015201610eb5565b506000602082860101526020601f19601f83011685010191505092915050565b606081526000610f046060830186610eab565b6001600160a01b0394851660208401529290931660409091015292915050565b608081526000610f376080830187610eab565b6001600160a01b039586166020840152939094166040820152606001529291505056fe4163636f756e74202573206372656174656420737562736372697074696f6e206f6e207075626c69736865722025732e4163636f756e742025732063726561746564207265616374696f6e206f6e20707562206974656d2025732f25644163636f756e742025732072656d6f766564207265616374696f6e206f6e20707562206974656d2025732f25644163636f756e742025732072656d6f76656420737562736372697074696f6e206f6e207075626c69736865722025732ea2646970667358221220f653a3f6306444d45301105417326b7702fe13fe4fc4a37c672bb68055b81dc864736f6c63430008130033";

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

    public List<CreateReactionEventResponse> getCreateReactionEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(CREATEREACTION_EVENT, transactionReceipt);
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

    public List<CreateSubscriptionEventResponse> getCreateSubscriptionEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(CREATESUBSCRIPTION_EVENT, transactionReceipt);
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

    public List<RemoveReactionEventResponse> getRemoveReactionEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(REMOVEREACTION_EVENT, transactionReceipt);
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

    public List<RemoveSubscriptionEventResponse> getRemoveSubscriptionEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(REMOVESUBSCRIPTION_EVENT, transactionReceipt);
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
