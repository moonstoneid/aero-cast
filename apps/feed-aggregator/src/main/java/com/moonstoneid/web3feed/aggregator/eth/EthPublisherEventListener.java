package com.moonstoneid.web3feed.aggregator.eth;

import java.math.BigInteger;
import java.util.Optional;

import com.moonstoneid.web3feed.common.eth.EthUtil;
import com.moonstoneid.web3feed.common.eth.BaseEthEventListener;
import com.moonstoneid.web3feed.common.eth.contracts.FeedPublisher;
import com.moonstoneid.web3feed.aggregator.model.Publisher;
import com.moonstoneid.web3feed.aggregator.service.EntryService;
import com.moonstoneid.web3feed.aggregator.service.PublisherService;
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
public class EthPublisherEventListener extends BaseEthEventListener {

    private final PublisherService pubService;
    private final EntryService entryService;

    private final EthPublisherService ethPubService;

    private final MultiValueMap<String, Disposable> listeners = new LinkedMultiValueMap<>();

    public EthPublisherEventListener(Web3j web3j, PublisherService pubService,
            EntryService entryService, EthPublisherService ethService) {
        super(web3j);
        this.pubService = pubService;
        this.entryService = entryService;
        this.ethPubService = ethService;
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

    public void registerPubEventListener(String pubContractAddr) {
        log.debug("Adding event listener on publisher contract '{}'.",
                EthUtil.shortenAddress(pubContractAddr));

        Optional<Publisher> pub = pubService.findPublisher(pubContractAddr);
        if (pub.isEmpty()) {
            return;
        }
        BigInteger blockNumber = Numeric.toBigInt(pub.get().getBlockNumber());

        EthFilter subFilter = createFilter(pubContractAddr, blockNumber,
                FeedPublisher.NEWPUBITEM_EVENT);
        Disposable sub = web3j.ethLogFlowable(subFilter).subscribe(l ->
                onNewPubItemEvent(pubContractAddr, l));
        listeners.add(pubContractAddr, sub);
    }

    public void unregisterPubEventListener(String pubContractAddr) {
        log.debug("Removing event listener on publisher contract '{}'.",
                EthUtil.shortenAddress(pubContractAddr));

        for (Disposable d : listeners.get(pubContractAddr)) {
            d.dispose();
        }
    }

    private void onNewPubItemEvent(String pubContractAddr, Log log) {
        Optional<BigInteger> pubItemId = getPubItemIdFromLog(log);
        if (pubItemId.isEmpty()) {
            return;
        }

        // Get pubItem from contract
        FeedPublisher.PubItem pubItem = ethPubService.getPubItem(pubContractAddr, pubItemId.get());
        if (pubItem == null || pubItem.data == null) {
            return;
        }

        // Update block number in publisher db
        String blockNumber = Numeric.toHexStringWithPrefix(log.getBlockNumber());
        pubService.updateBlockNumber(pubContractAddr, blockNumber);

        // Fetch entry
        String guid = pubItem.data;
        entryService.createEntry(pubContractAddr, guid);
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
