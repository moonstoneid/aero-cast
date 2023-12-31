package com.moonstoneid.aerocast.aggregator.eth;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.moonstoneid.aerocast.common.eth.BaseEthAdapter;
import com.moonstoneid.aerocast.common.eth.EthUtil;
import com.moonstoneid.aerocast.common.eth.contracts.FeedPublisher;
import com.moonstoneid.aerocast.common.eth.contracts.FeedPublisher.NewPubItemEventResponse;
import com.moonstoneid.aerocast.common.eth.contracts.FeedPublisher.PubItem;
import io.reactivex.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.request.EthFilter;

@Component
@Slf4j
public class EthPublisherAdapter extends BaseEthAdapter {

    public interface EventListener {
        void onNewPubItem(String pubContractAddr, String blockNumber, PubItem pubItem);
    }

    private final Credentials credentials;

    private final MultiValueMap<String, Disposable> eventSubscribers = new LinkedMultiValueMap<>();

    public EthPublisherAdapter(Web3j web3j) {
        super(web3j);
        this.credentials = createDummyCredentials();
    }

    public void registerEventListener(String pubContractAddr, String blockNumber,
            EventListener listener) {
        log.debug("Adding event listener on publisher contract '{}'.",
                EthUtil.shortenAddress(pubContractAddr));

        FeedPublisher contract = getPublisherContract(pubContractAddr);

        EthFilter eventFilter = createEventFilter(pubContractAddr, blockNumber,
                FeedPublisher.NEWPUBITEM_EVENT);
        Disposable eventSubscriber = contract.newPubItemEventFlowable(eventFilter)
                .subscribe(r -> handleNewPubItemEvent(pubContractAddr, r, listener));
        eventSubscribers.add(pubContractAddr, eventSubscriber);
    }

    private void handleNewPubItemEvent(String pubContractAddr, NewPubItemEventResponse response,
            EventListener listener) {
        String blockNumber = getBlockNumberFromEventResponse(response);
        PubItem item = getPublisherItem(pubContractAddr, response.num);
        listener.onNewPubItem(pubContractAddr, blockNumber, item);
    }

    public void unregisterEventListener(String pubContractAddr) {
        log.debug("Removing event listener on publisher contract '{}'.",
                EthUtil.shortenAddress(pubContractAddr));

        for (Disposable eventSubscriber : eventSubscribers.get(pubContractAddr)) {
            eventSubscriber.dispose();
        }
    }

    public String getPublisherFeedUrl(String pubContractAddr) {
        FeedPublisher contract = getPublisherContract(pubContractAddr);
        try {
            return contract.getFeedUrl().sendAsync().get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<PubItem> getPublisherItems(String pubContractAddr) {
        FeedPublisher contract = getPublisherContract(pubContractAddr);
        try {
            List<PubItem> items = new ArrayList<>();
            BigInteger count = contract.getTotalPubItemCount().sendAsync().get();
            if (count == null) {
                return items;
            }
            for (int i = 0; i < count.intValue(); i++) {
                PubItem item = contract.getPubItem(BigInteger.valueOf(i)).sendAsync().get();
                items.add(item);
            }
            return items;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public PubItem getPublisherItem(String pubContractAddr, BigInteger num) {
        FeedPublisher contract = getPublisherContract(pubContractAddr);
        try {
           return contract.getPubItem(num).sendAsync().get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private FeedPublisher getPublisherContract(String contractAddr) {
        return createPublisherContract(contractAddr, credentials);
    }

}
