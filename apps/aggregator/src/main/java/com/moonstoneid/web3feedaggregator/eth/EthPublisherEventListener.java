package com.moonstoneid.web3feedaggregator.eth;

import java.math.BigInteger;
import java.util.Optional;

import com.moonstoneid.web3feedaggregator.eth.contracts.FeedPublisher;
import com.moonstoneid.web3feedaggregator.model.Publisher;
import com.moonstoneid.web3feedaggregator.service.EntryService;
import com.moonstoneid.web3feedaggregator.service.PublisherService;
import io.reactivex.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Uint;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.utils.Numeric;

@Slf4j
@Component
public class EthPublisherEventListener {

    private final PublisherService pubService;
    private final EntryService entryService;
    private final EthService ethService;
    private final Web3j web3j;

    private final MultiValueMap<String, Disposable> listeners = new LinkedMultiValueMap<>();

    public EthPublisherEventListener(PublisherService pubService, EntryService entryService,
            EthService ethService, Web3j web3j) {
        this.pubService = pubService;
        this.entryService = entryService;
        this.ethService = ethService;
        this.web3j = web3j;
    }

    // Register listeners after Spring Boot has started
    @EventListener(ApplicationReadyEvent.class)
    public void initEventListener() {
        registerPubEventListeners();
    }

    public void registerPubEventListeners() {
        pubService.getPublishers().forEach(p -> registerPubEventListener(
                p.getContractAddress()));
    }

    public void registerPubEventListener(String pubContrAddr) {
        log.debug("Adding event listener on publisher contract '{}'.",
                EthUtil.shortenAddress(pubContrAddr));

        Optional<Publisher> pub = pubService.findPublisher(pubContrAddr);
        if (pub.isEmpty()) {
            return;
        }
        BigInteger blockNumber = Numeric.toBigInt(pub.get().getBlockNumber());

        EthFilter subFilter = EthUtil.createFilter(pubContrAddr, blockNumber,
                FeedPublisher.NEWPUBITEM_EVENT);
        Disposable sub = web3j.ethLogFlowable(subFilter).subscribe(l ->
                onNewPubItemEvent(pubContrAddr, l));
        listeners.add(pubContrAddr, sub);
    }

    public void unregisterPubEventListener(String pubContrAddr) {
        log.debug("Removing event listener on publisher contract '{}'.",
                EthUtil.shortenAddress(pubContrAddr));

        for (Disposable d : listeners.get(pubContrAddr)) {
            d.dispose();
        }
    }

    private void onNewPubItemEvent(String pubContrAddr, Log log) {
        Optional<BigInteger> pubItemId = getPubItemIdFromLog(log);
        if (pubItemId.isEmpty()) {
            return;
        }

        // Get pubItem from contract
        FeedPublisher.PubItem pubItem = ethService.getPubItem(pubContrAddr, pubItemId.get());
        if (pubItem == null || pubItem.data == null) {
            return;
        }

        // Update block number in publisher db
        String blockNumber = Numeric.toHexStringWithPrefix(log.getBlockNumber());
        pubService.updateBlockNumber(pubContrAddr, blockNumber);

        // Fetch entry
        String guid = pubItem.data;
        entryService.createEntry(pubContrAddr, guid);
    }

    private static Optional<BigInteger> getPubItemIdFromLog(Log log) {
        if (log.getTopics().size() <= 1) {
            return Optional.empty();
        }
        String topic = log.getTopics().get(1);
        // Topic 0: Event signature, topic 1: Item
        // https://medium.com/mycrypto/understanding-event-logs-on-the-ethereum-blockchain-f4ae7ba50378
        Uint putItemNumber =  (Uint) FunctionReturnDecoder.decodeIndexedValue(topic,
                new TypeReference<Uint>(){});
        return Optional.ofNullable(putItemNumber.getValue());
    }

}
