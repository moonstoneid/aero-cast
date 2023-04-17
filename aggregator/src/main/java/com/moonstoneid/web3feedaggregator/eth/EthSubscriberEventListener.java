package com.moonstoneid.web3feedaggregator.eth;

import java.math.BigInteger;
import java.util.Optional;

import com.moonstoneid.web3feedaggregator.eth.contracts.FeedSubscriber;
import com.moonstoneid.web3feedaggregator.model.Subscriber;
import com.moonstoneid.web3feedaggregator.service.SubscriberService;
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
public class EthSubscriberEventListener {

    private final SubscriberService subService;
    private final EthService ethService;
    private final Web3j web3j;

    private final MultiValueMap<String,Disposable> listeners = new LinkedMultiValueMap<>();

    public EthSubscriberEventListener(SubscriberService subService, EthService ethService) {
        this.subService = subService;
        this.ethService = ethService;
        this.web3j = ethService.getWeb3j();
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
        String subAccAddr = subscriber.getAccountAddress();
        String subContrAddr = subscriber.getContractAddress();
        BigInteger blockNumber = Numeric.toBigInt(subscriber.getBlockNumber());

        log.debug("Adding event listener on subscriber contract '{}'.",
                EthUtil.shortenAddress(subContrAddr));

        EthFilter subFilter = EthUtil.createFilter(subContrAddr, blockNumber,
                FeedSubscriber.CREATESUBSCRIPTION_EVENT);
        Disposable sub = web3j.ethLogFlowable(subFilter).subscribe(l ->
                onCreateSubEvent(subAccAddr, l));

        EthFilter unsubFilter = EthUtil.createFilter(subContrAddr, blockNumber,
                FeedSubscriber.REMOVESUBSCRIPTION_EVENT);
        Disposable unsub = web3j.ethLogFlowable(unsubFilter).subscribe(l ->
                onRemoveSubEvent(subAccAddr, l));

        // Store listeners so we can unregister them later
        listeners.add(subContrAddr, sub);
        listeners.add(subContrAddr, unsub);
    }

    public void unregisterSubEventListener(Subscriber subscriber) {
        log.debug("Removing event listener on subscriber contract '{}'.",
                EthUtil.shortenAddress(subscriber.getContractAddress()));

        for (Disposable listener : listeners.get(subscriber.getContractAddress())) {
            listener.dispose();
        }
    }

    private void onCreateSubEvent(String subAccAddr, Log log) {
        Optional<String> pubContrAddr = getPubAddressFromLog(log);
        if (pubContrAddr.isPresent()) {
            subService.addSubscription(subAccAddr, pubContrAddr.get());
        }
    }

    private void onRemoveSubEvent(String subAccAddr, Log log) {
        Optional<String> pubContrAddr = getPubAddressFromLog(log);
        if (pubContrAddr.isPresent()) {
            subService.removeSubscription(subAccAddr, pubContrAddr.get());
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
