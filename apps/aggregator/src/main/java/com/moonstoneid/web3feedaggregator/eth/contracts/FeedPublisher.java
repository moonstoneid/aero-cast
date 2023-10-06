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
    public static final String BINARY = "608060405234801561001057600080fd5b50600080546001600160a01b03191633179055604080516060810190915260288082526100459190610b80602083013961004a565b610104565b6100918160405160240161005e91906100b6565b60408051601f198184030181529190526020810180516001600160e01b0390811663104c13eb60e21b1790915261009416565b50565b80516a636f6e736f6c652e6c6f6790602083016000808383865afa5050505050565b600060208083528351808285015260005b818110156100e3578581018301518582016040015282016100c7565b506000604082860101526040601f19601f8301168501019250505092915050565b610a6d806101136000396000f3fe608060405234801561001057600080fd5b506004361061007d5760003560e01c80637b7636a11161005b5780637b7636a1146100c05780637c83510f146100e057806384b439c0146100f55780638da5cb5b1461010857600080fd5b806313af4035146100825780631c4bdaa314610097578063243e280b146100ad575b600080fd5b61009561009036600461064b565b610123565b005b6002546040519081526020015b60405180910390f35b6100956100bb366004610691565b610178565b6100d36100ce366004610742565b6102aa565b6040516100a491906107a1565b6100e86103f8565b6040516100a491906107d8565b610095610103366004610691565b61048a565b6000546040516001600160a01b0390911681526020016100a4565b6000546001600160a01b031633146101565760405162461bcd60e51b815260040161014d906107eb565b60405180910390fd5b600080546001600160a01b0319166001600160a01b0392909216919091179055565b6000546001600160a01b031633146101a25760405162461bcd60e51b815260040161014d906107eb565b600280546040805160608101825282815242602082019081529181018581526001840185556000949094528051600384027f405787fa12a823e0f2b7631cc41b3ba8828b3321ca811111fa75cd3aa3bb5ace810191825592517f405787fa12a823e0f2b7631cc41b3ba8828b3321ca811111fa75cd3aa3bb5acf84015593519293909290917f405787fa12a823e0f2b7631cc41b3ba8828b3321ca811111fa75cd3aa3bb5ad0019061025490826108bb565b50505061027b604051806060016040528060228152602001610a166022913933838561058e565b60405181907f6dda240fc875f1ee65b95abd125377f81bf199395a55b7f2ef46c713cae8c29890600090a25050565b6102ce60405180606001604052806000815260200160008152602001606081525090565b600254821061031f5760405162461bcd60e51b815260206004820152601860248201527f507562206974656d20646f6573206e6f74206578697374210000000000000000604482015260640161014d565b600282815481106103325761033261097b565b9060005260206000209060030201604051806060016040529081600082015481526020016001820154815260200160028201805461036f90610833565b80601f016020809104026020016040519081016040528092919081815260200182805461039b90610833565b80156103e85780601f106103bd576101008083540402835291602001916103e8565b820191906000526020600020905b8154815290600101906020018083116103cb57829003601f168201915b5050505050815250509050919050565b60606001805461040790610833565b80601f016020809104026020016040519081016040528092919081815260200182805461043390610833565b80156104805780601f1061045557610100808354040283529160200191610480565b820191906000526020600020905b81548152906001019060200180831161046357829003601f168201915b5050505050905090565b6000546001600160a01b031633146104b45760405162461bcd60e51b815260040161014d906107eb565b60016104c082826108bb565b5061058b6040518060400160405280601d81526020017f4163636f756e742025732073657420666565642055524c3a2027257327000000815250336001805461050890610833565b80601f016020809104026020016040519081016040528092919081815260200182805461053490610833565b80156105815780601f1061055657610100808354040283529160200191610581565b820191906000526020600020905b81548152906001019060200180831161056457829003601f168201915b50505050506105dd565b50565b6105d7848484846040516024016105a89493929190610991565b60408051601f198184030181529190526020810180516001600160e01b0316632d23bb1960e11b179052610629565b50505050565b6106248383836040516024016105f5939291906109d7565b60408051601f198184030181529190526020810180516001600160e01b031663e0e9ad4f60e01b179052610629565b505050565b80516a636f6e736f6c652e6c6f6790602083016000808383865afa5050505050565b60006020828403121561065d57600080fd5b81356001600160a01b038116811461067457600080fd5b9392505050565b634e487b7160e01b600052604160045260246000fd5b6000602082840312156106a357600080fd5b813567ffffffffffffffff808211156106bb57600080fd5b818401915084601f8301126106cf57600080fd5b8135818111156106e1576106e161067b565b604051601f8201601f19908116603f011681019083821181831017156107095761070961067b565b8160405282815287602084870101111561072257600080fd5b826020860160208301376000928101602001929092525095945050505050565b60006020828403121561075457600080fd5b5035919050565b6000815180845260005b8181101561078157602081850181015186830182015201610765565b506000602082860101526020601f19601f83011685010191505092915050565b602081528151602082015260208201516040820152600060408301516060808401526107d0608084018261075b565b949350505050565b602081526000610674602083018461075b565b60208082526028908201527f43616c6c6572206f66207468652066756e6374696f6e206973206e6f7420746860408201526765206f776e65722160c01b606082015260800190565b600181811c9082168061084757607f821691505b60208210810361086757634e487b7160e01b600052602260045260246000fd5b50919050565b601f82111561062457600081815260208120601f850160051c810160208610156108945750805b601f850160051c820191505b818110156108b3578281556001016108a0565b505050505050565b815167ffffffffffffffff8111156108d5576108d561067b565b6108e9816108e38454610833565b8461086d565b602080601f83116001811461091e57600084156109065750858301515b600019600386901b1c1916600185901b1785556108b3565b600085815260208120601f198616915b8281101561094d5788860151825594840194600190910190840161092e565b508582101561096b5787850151600019600388901b60f8161c191681555b5050505050600190811b01905550565b634e487b7160e01b600052603260045260246000fd5b6080815260006109a4608083018761075b565b6001600160a01b03861660208401526040830185905282810360608401526109cc818561075b565b979650505050505050565b6060815260006109ea606083018661075b565b6001600160a01b03851660208401528281036040840152610a0b818561075b565b969550505050505056fe4163636f756e74202573207075626c6973686564206974656d2025643a2027257327a264697066735822122015025348b2070e170e07b005bee318202c0997e07c9b4a7af027615656f8950964736f6c634300081300335075626c697368657220636f6e747261637420686173206265656e20636f6e737472756374656421";

    public static final String FUNC_GETFEEDURL = "getFeedUrl";

    public static final String FUNC_GETPUBITEM = "getPubItem";

    public static final String FUNC_GETTOTALPUBITEMCOUNT = "getTotalPubItemCount";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_PUBLISH = "publish";

    public static final String FUNC_SETFEEDURL = "setFeedUrl";

    public static final String FUNC_SETOWNER = "setOwner";

    public static final Event NEWPUBITEM_EVENT = new Event("NewPubItem",
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}));

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
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(NEWPUBITEM_EVENT, transactionReceipt);
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

    public RemoteFunctionCall<String> getFeedUrl() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETFEEDURL,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
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

    public RemoteFunctionCall<TransactionReceipt> setFeedUrl(String feedUrl) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_SETFEEDURL, 
                Arrays.<Type>asList(new Utf8String(feedUrl)),
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
            super(new Uint256(num), new Uint256(timestamp), new Utf8String(data));
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
