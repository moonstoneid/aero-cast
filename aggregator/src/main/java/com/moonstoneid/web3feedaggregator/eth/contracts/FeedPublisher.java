package com.moonstoneid.web3feedaggregator.eth.contracts;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
import org.web3j.protocol.core.RemoteCall;
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
public class FeedPublisher extends Contract {
    public static final String BINARY = "608060405234801561001057600080fd5b50600080546001600160a01b0319163317905561081d806100326000396000f3fe608060405234801561001057600080fd5b506004361061007d5760003560e01c80637b7636a11161005b5780637b7636a1146100c05780637c83510f146100e057806384b439c0146100f55780638da5cb5b1461010857600080fd5b806313af4035146100825780631c4bdaa314610097578063243e280b146100ad575b600080fd5b6100956100903660046104a0565b610123565b005b6002546040519081526020015b60405180910390f35b6100956100bb3660046104e6565b610178565b6100d36100ce366004610597565b610286565b6040516100a491906105f6565b6100e86103d4565b6040516100a4919061062d565b6100956101033660046104e6565b610466565b6000546040516001600160a01b0390911681526020016100a4565b6000546001600160a01b031633146101565760405162461bcd60e51b815260040161014d90610640565b60405180910390fd5b600080546001600160a01b0319166001600160a01b0392909216919091179055565b6000546001600160a01b031633146101a25760405162461bcd60e51b815260040161014d90610640565b600280546040805160608101825282815242602082019081529181018581526001840185556000949094528051600384027f405787fa12a823e0f2b7631cc41b3ba8828b3321ca811111fa75cd3aa3bb5ace810191825592517f405787fa12a823e0f2b7631cc41b3ba8828b3321ca811111fa75cd3aa3bb5acf84015593519293909290917f405787fa12a823e0f2b7631cc41b3ba8828b3321ca811111fa75cd3aa3bb5ad001906102549082610711565b50506040518291507f6dda240fc875f1ee65b95abd125377f81bf199395a55b7f2ef46c713cae8c29890600090a25050565b6102aa60405180606001604052806000815260200160008152602001606081525090565b60025482106102fb5760405162461bcd60e51b815260206004820152601860248201527f507562206974656d20646f6573206e6f74206578697374210000000000000000604482015260640161014d565b6002828154811061030e5761030e6107d1565b9060005260206000209060030201604051806060016040529081600082015481526020016001820154815260200160028201805461034b90610688565b80601f016020809104026020016040519081016040528092919081815260200182805461037790610688565b80156103c45780601f10610399576101008083540402835291602001916103c4565b820191906000526020600020905b8154815290600101906020018083116103a757829003601f168201915b5050505050815250509050919050565b6060600180546103e390610688565b80601f016020809104026020016040519081016040528092919081815260200182805461040f90610688565b801561045c5780601f106104315761010080835404028352916020019161045c565b820191906000526020600020905b81548152906001019060200180831161043f57829003601f168201915b5050505050905090565b6000546001600160a01b031633146104905760405162461bcd60e51b815260040161014d90610640565b600161049c8282610711565b5050565b6000602082840312156104b257600080fd5b81356001600160a01b03811681146104c957600080fd5b9392505050565b634e487b7160e01b600052604160045260246000fd5b6000602082840312156104f857600080fd5b813567ffffffffffffffff8082111561051057600080fd5b818401915084601f83011261052457600080fd5b813581811115610536576105366104d0565b604051601f8201601f19908116603f0116810190838211818310171561055e5761055e6104d0565b8160405282815287602084870101111561057757600080fd5b826020860160208301376000928101602001929092525095945050505050565b6000602082840312156105a957600080fd5b5035919050565b6000815180845260005b818110156105d6576020818501810151868301820152016105ba565b506000602082860101526020601f19601f83011685010191505092915050565b6020815281516020820152602082015160408201526000604083015160608084015261062560808401826105b0565b949350505050565b6020815260006104c960208301846105b0565b60208082526028908201527f43616c6c6572206f66207468652066756e6374696f6e206973206e6f7420746860408201526765206f776e65722160c01b606082015260800190565b600181811c9082168061069c57607f821691505b6020821081036106bc57634e487b7160e01b600052602260045260246000fd5b50919050565b601f82111561070c57600081815260208120601f850160051c810160208610156106e95750805b601f850160051c820191505b81811015610708578281556001016106f5565b5050505b505050565b815167ffffffffffffffff81111561072b5761072b6104d0565b61073f816107398454610688565b846106c2565b602080601f831160018114610774576000841561075c5750858301515b600019600386901b1c1916600185901b178555610708565b600085815260208120601f198616915b828110156107a357888601518255948401946001909101908401610784565b50858210156107c15787850151600019600388901b60f8161c191681555b5050505050600190811b01905550565b634e487b7160e01b600052603260045260246000fdfea26469706673582212208f2c2a332c88fe287b7a52c1380773455ce87bbd690abf83a13b5340eef963b464736f6c63430008130033";

    public static final String FUNC_GETFEEDURL = "getFeedUrl";

    public static final String FUNC_GETPUBITEM = "getPubItem";

    public static final String FUNC_GETTOTALPUBITEMCOUNT = "getTotalPubItemCount";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_PUBLISH = "publish";

    public static final String FUNC_SETFEEDURL = "setFeedUrl";

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

    public List<NewPubItemEventResponse> getNewPubItemEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(NEWPUBITEM_EVENT, transactionReceipt);
        ArrayList<NewPubItemEventResponse> responses = new ArrayList<NewPubItemEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
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
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(NEWPUBITEM_EVENT, log);
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

    public RemoteFunctionCall<String> getFeedUrl() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETFEEDURL, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<PubItem> getPubItem(BigInteger num) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETPUBITEM, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(num)), 
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
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(data)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setFeedUrl(String feedUrl) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_SETFEEDURL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(feedUrl)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setOwner(String _newOwner) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_SETOWNER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _newOwner)), 
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

    public static RemoteCall<FeedPublisher> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(FeedPublisher.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<FeedPublisher> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(FeedPublisher.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<FeedPublisher> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(FeedPublisher.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<FeedPublisher> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(FeedPublisher.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static class PubItem extends DynamicStruct {
        public BigInteger num;

        public BigInteger timestamp;

        public String data;

        public PubItem(BigInteger num, BigInteger timestamp, String data) {
            super(new org.web3j.abi.datatypes.generated.Uint256(num),new org.web3j.abi.datatypes.generated.Uint256(timestamp),new org.web3j.abi.datatypes.Utf8String(data));
            this.num = num;
            this.timestamp = timestamp;
            this.data = data;
        }

        public PubItem(Uint256 num, Uint256 timestamp, Utf8String data) {
            super(num,timestamp,data);
            this.num = num.getValue();
            this.timestamp = timestamp.getValue();
            this.data = data.getValue();
        }
    }

    public static class NewPubItemEventResponse extends BaseEventResponse {
        public BigInteger num;
    }
}
