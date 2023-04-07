package com.moonstoneid.web3feedaggregator.eth;

import java.math.BigInteger;
import java.util.Optional;

import com.moonstoneid.web3feedaggregator.eth.contracts.FeedPublisher;
import com.moonstoneid.web3feedaggregator.service.EntryService;
import com.moonstoneid.web3feedaggregator.service.PublisherService;
import io.reactivex.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Uint;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;

@Slf4j
public class EthPublisherEventListener {

    private final PublisherService publisherService;
    private final EntryService entryService;
    private final EthService ethService;
    private final Web3j web3j;

    private final MultiValueMap<String, Disposable> listeners = new LinkedMultiValueMap<>();

    public EthPublisherEventListener(PublisherService publisherService, EntryService entryService,
            EthService ethService, Web3j web3j) {
        this.publisherService = publisherService;
        this.entryService = entryService;
        this.ethService = ethService;
        this.web3j = web3j;
    }

    public void registerPublisherEventListeners() {
        publisherService.getPublishers().forEach(p -> registerPublisherEventListener(
                p.getContractAddress()));
    }

    public void registerPublisherEventListener(String contractAddr) {
        log.debug("Adding event listener on publisher contract '{}'.",
                EthUtil.shortenAddress(contractAddr));

        String blockNumber = ethService.getCurrentBlockNumber();

        EthFilter subFilter = EthUtil.createFilter(contractAddr, blockNumber, FeedPublisher.NEWPUBITEM_EVENT);
        Disposable sub = web3j.ethLogFlowable(subFilter).subscribe(l -> onNewPubItemEvent(contractAddr, l));
        listeners.add(contractAddr, sub);
    }

    private void onNewPubItemEvent(String contractAddr, Log log) {
        Optional<BigInteger> pubItemId = getPubItemIdFromLog(log);
        if (pubItemId.isEmpty()) {
            return;
        }

        // Get pubItem from contract
        FeedPublisher.PubItem pubItem = ethService.getPubItem(contractAddr, pubItemId.get());
        if (pubItem == null || pubItem.data == null) {
            return;
        }

        // Fetch entry
        String guid = pubItem.data;
        entryService.fetchEntry(contractAddr, guid);
    }

    public void unregisterPublisherEventListener(String contractAddr) {
        log.debug("Removing event listener on publisher contract '{}'.",
                EthUtil.shortenAddress(contractAddr));

        for (Disposable d : listeners.get(contractAddr)) {
            d.dispose();
        }
    }

    private static Optional<BigInteger> getPubItemIdFromLog(Log log) {
        if (log.getTopics().size() <= 1) {
            return Optional.empty();
        }
        String topic = log.getTopics().get(1);
        // Topic 0: Event signature, topic 1: Item
        // See  https://medium.com/mycrypto/understanding-event-logs-on-the-ethereum-blockchain-f4ae7ba50378
        Uint putItemNumber =  (Uint) FunctionReturnDecoder.decodeIndexedValue(topic, new TypeReference<Uint>(){});
        return Optional.ofNullable(putItemNumber.getValue());
    }

}
