package com.moonstoneid.web3feedaggregator.eth;

import com.moonstoneid.web3feedaggregator.eth.contracts.FeedPublisher;
import com.moonstoneid.web3feedaggregator.model.Publisher;
import com.moonstoneid.web3feedaggregator.service.EntryService;
import com.moonstoneid.web3feedaggregator.service.PublisherService;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Uint;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;

public class EthPublisherEventListener {

    private final PublisherService publisherService;
    private final EntryService entryService;
    private final EthService ethService;
    private final Web3j web3j;

    public EthPublisherEventListener(PublisherService publisherService, EntryService entryService, EthService ethService,
            Web3j web3j) {
        this.web3j = web3j;
        this.publisherService = publisherService;
        this.entryService = entryService;
        this.ethService = ethService;
    }

    public void registerPublisherEventListeners() {
        publisherService.getPublishers().forEach(this::registerPublisherEventListener);
    }

    public void registerPublisherEventListener(Publisher publisher) {
        String contractAddress = publisher.getContractAddress();

        EthFilter subFilter = createFilter(contractAddress, FeedPublisher.NEWPUBITEM_EVENT);
        web3j.ethLogFlowable(subFilter).subscribe(l -> onNewPubItemEvent(contractAddress, l));
    }

    private void onNewPubItemEvent(String pubAddress, Log log) {
        // Topic 0: Event signature, topic 1: Item
        // See  https://medium.com/mycrypto/understanding-event-logs-on-the-ethereum-blockchain-f4ae7ba50378
        Uint putItemNumber =  (Uint) FunctionReturnDecoder.decodeIndexedValue(log.getTopics().get(1),
                new TypeReference<Uint>(){});

        // Get pubItem from contract
        FeedPublisher.PubItem pubItem = ethService.getPubItem(pubAddress, putItemNumber.getValue());
        String guid = pubItem.data;

        entryService.createEntry(guid, pubAddress);
    }

    public void unregisterPublisherEventListener(Publisher publisher) {
        // TODO!!!
    }

    // TODO: Impl. smarter algorithm that only a processes only events after a specific block timestamp
    private static EthFilter createFilter(String contractAddress, Event event) {
        EthFilter filter = new EthFilter(DefaultBlockParameterName.EARLIEST,
                DefaultBlockParameterName.LATEST, contractAddress);
        filter.addSingleTopic(EventEncoder.encode(event));
        return filter;
    }

}
