package com.moonstoneid.web3feed.aggregator.eth;

import java.math.BigInteger;
import java.util.Optional;

import com.moonstoneid.web3feed.common.eth.EthUtil;
import com.moonstoneid.web3feed.common.eth.BaseEthEventListener;
import com.moonstoneid.web3feed.common.eth.contracts.FeedSubscriber;
import com.moonstoneid.web3feed.aggregator.model.Subscriber;
import com.moonstoneid.web3feed.aggregator.service.SubscriberService;
import io.reactivex.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.utils.Numeric;

@Slf4j
@Component
public class EthSubscriberEventListener extends BaseEthEventListener {

    private final SubscriberService subService;

    private final MultiValueMap<String,Disposable> listeners = new LinkedMultiValueMap<>();

    public EthSubscriberEventListener(Web3j web3j, SubscriberService subService) {
        super(web3j);
        this.subService = subService;
    }

    // Register listeners after Spring Boot has started
    @EventListener(ApplicationReadyEvent.class)
    public void initEventListener() {
        registerSubEventListeners();
    }

    public void registerSubEventListeners() {
        subService.getSubscribers().forEach(this::registerSubEventListener);
    }

    public void registerSubEventListener(Subscriber subscriber) {
        String subAccountAddr = subscriber.getAccountAddress();
        String subContractAddr = subscriber.getContractAddress();
        BigInteger blockNumber = Numeric.toBigInt(subscriber.getBlockNumber());

        log.debug("Adding event listener on subscriber contract '{}'.",
                EthUtil.shortenAddress(subContractAddr));

        EthFilter subFilter = createFilter(subContractAddr, blockNumber,
                FeedSubscriber.CREATESUBSCRIPTION_EVENT);
        Disposable sub = web3j.ethLogFlowable(subFilter).subscribe(l ->
                onCreateSubEvent(subAccountAddr, l));

        EthFilter unsubFilter = createFilter(subContractAddr, blockNumber,
                FeedSubscriber.REMOVESUBSCRIPTION_EVENT);
        Disposable unsub = web3j.ethLogFlowable(unsubFilter).subscribe(l ->
                onRemoveSubEvent(subAccountAddr, l));

        // Store listeners so we can unregister them later
        listeners.add(subContractAddr, sub);
        listeners.add(subContractAddr, unsub);
    }

    public void unregisterSubEventListener(Subscriber subscriber) {
        log.debug("Removing event listener on subscriber contract '{}'.",
                EthUtil.shortenAddress(subscriber.getContractAddress()));

        for (Disposable listener : listeners.get(subscriber.getContractAddress())) {
            listener.dispose();
        }
    }

    private void onCreateSubEvent(String subAccountAddr, Log log) {
        Optional<String> pubContractAddr = getPubAddressFromLog(log);
        if (pubContractAddr.isPresent()) {
            subService.addSubscription(subAccountAddr, pubContractAddr.get());
        }
    }

    private void onRemoveSubEvent(String subAccountAddr, Log log) {
        Optional<String> pubContractAddr = getPubAddressFromLog(log);
        if (pubContractAddr.isPresent()) {
            subService.removeSubscription(subAccountAddr, pubContractAddr.get());
        }
    }

    private static Optional<String> getPubAddressFromLog(Log log) {
        if (log.getTopics().size() <= 1) {
            return Optional.empty();
        }
        String topic = log.getTopics().get(1);
        Address addr = (Address) FunctionReturnDecoder.decodeIndexedValue(topic,
                new TypeReference<Address>() {});
        return Optional.ofNullable(addr.getValue());
    }

}
