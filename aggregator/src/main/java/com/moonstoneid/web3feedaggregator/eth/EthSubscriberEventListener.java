package com.moonstoneid.web3feedaggregator.eth;

import java.math.BigInteger;
import java.util.Optional;

import com.moonstoneid.web3feedaggregator.eth.contracts.FeedSubscriber;
import com.moonstoneid.web3feedaggregator.model.Subscriber;
import com.moonstoneid.web3feedaggregator.service.SubscriberService;
import io.reactivex.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;
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
public class EthSubscriberEventListener {

    private final SubscriberService subscriberService;
    private final EthService ethService;
    private final Web3j web3j;

    private final MultiValueMap<String,Disposable> listeners = new LinkedMultiValueMap<>();

    public EthSubscriberEventListener(SubscriberService subscriberService, EthService ethService) {
        this.subscriberService = subscriberService;
        this.ethService = ethService;
        this.web3j = ethService.getWeb3j();
    }

    public void registerSubscriberEventListeners() {
        subscriberService.getSubscribers().forEach(this::registerSubscriberEventListener);
    }

    public void registerSubscriberEventListener(Subscriber subscriber) {
        String accountAddr = subscriber.getAccountAddress();
        String contractAddr = subscriber.getContractAddress();
        BigInteger blockNumber = Numeric.toBigInt(subscriber.getBlockNumber());

        log.debug("Adding event listener on subscriber contract '{}'.",
                EthUtil.shortenAddress(contractAddr));

        EthFilter subFilter = EthUtil.createFilter(contractAddr, blockNumber, FeedSubscriber.CREATESUBSCRIPTION_EVENT);
        Disposable sub = web3j.ethLogFlowable(subFilter).subscribe(l -> onCreateSubscriptionEvent(accountAddr, l));

        EthFilter unsubFilter = EthUtil.createFilter(contractAddr, blockNumber, FeedSubscriber.REMOVESUBSCRIPTION_EVENT);
        Disposable unsub = web3j.ethLogFlowable(unsubFilter).subscribe(l -> onRemoveSubscriptionEvent(accountAddr, l));

        // Store listeners so we can unregister them later
        listeners.add(contractAddr, sub);
        listeners.add(contractAddr, unsub);
    }

    private void onCreateSubscriptionEvent(String subAddress, Log log) {
        Optional<String> pubAddr = getPublisherAddressFromLog(log);
        if (pubAddr.isPresent()) {
            subscriberService.addSubscription(subAddress, pubAddr.get());
        }
    }

    private void onRemoveSubscriptionEvent(String subAddress, Log log) {
        Optional<String> pubAddr = getPublisherAddressFromLog(log);
        if (pubAddr.isPresent()) {
            subscriberService.removeSubscription(subAddress, pubAddr.get());
        }
    }

    private static Optional<String> getPublisherAddressFromLog(Log log) {
        if (log.getTopics().size() <= 1) {
            return Optional.empty();
        }
        String topic = log.getTopics().get(1);
        Address address = (Address) FunctionReturnDecoder.decodeIndexedValue(topic, new TypeReference<Address>() {});
        return Optional.ofNullable(address.getValue());
    }

    public void unregisterSubscriberEventListener(Subscriber subscriber) {
        log.debug("Removing event listener on subscriber contract '{}'.",
                EthUtil.shortenAddress(subscriber.getContractAddress()));

        for (Disposable listener : listeners.get(subscriber.getContractAddress())) {
            listener.dispose();
        }
    }

}
