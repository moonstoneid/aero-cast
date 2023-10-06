package com.moonstoneid.web3feed.aggregator.service;

import java.util.List;
import java.util.Optional;

import com.moonstoneid.web3feed.aggregator.eth.EthPublisherEventListener;
import com.moonstoneid.web3feed.aggregator.eth.EthPublisherService;
import com.moonstoneid.web3feed.aggregator.model.Publisher;
import com.moonstoneid.web3feed.aggregator.repo.PublisherRepo;
import com.moonstoneid.web3feed.common.eth.EthUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.utils.Numeric;

@Service
@Slf4j
public class PublisherService {

    private final PublisherRepo publisherRepo;

    private final EntryService entryService;

    private final EthPublisherService ethPubService;
    private final EthPublisherEventListener ethPubEventListener;

    public PublisherService(PublisherRepo publisherRepo, EntryService entryService,
            EthPublisherService ethPubService, EthPublisherEventListener ethPubEventListener) {
        this.publisherRepo = publisherRepo;
        this.entryService = entryService;
        this.ethPubService = ethPubService;
        this.ethPubEventListener = ethPubEventListener;
    }

    public List<Publisher> getPublishers() {
        return publisherRepo.findAll();
    }

    public Publisher createPublisherIfNotExists(String pubContractAddr) {
        pubContractAddr = pubContractAddr.toLowerCase();
        Optional<Publisher> pub = findPublisher(pubContractAddr);
        if (pub.isPresent()) {
            return pub.get();
        }

        log.info("Trying to create publisher '{}' ...", EthUtil.shortenAddress(pubContractAddr));
        Publisher publisher = savePublisher(pubContractAddr);
        log.info("Publisher '{}' was created.", EthUtil.shortenAddress(pubContractAddr));

        // Fetch entries for new publisher
        entryService.fetchEntries(pubContractAddr);

        // Register publisher event listener
        ethPubEventListener.registerPubEventListener(pubContractAddr);

        return publisher;
    }

    public void removePublisher(String pubContractAddr) {
        log.info("Trying to remove publisher '{}' ...", EthUtil.shortenAddress(pubContractAddr));

        Optional<Publisher> pub = findPublisher(pubContractAddr);
        if (pub.isEmpty()) {
            log.info("Publisher '{}' was not found.", EthUtil.shortenAddress(pubContractAddr));
            return;
        }
        ethPubEventListener.unregisterPubEventListener(pubContractAddr);
        entryService.removeEntriesByPublisher(pubContractAddr);
        publisherRepo.deleteById(pubContractAddr);

        log.info("Publisher '{}' has been removed.", EthUtil.shortenAddress(pubContractAddr));
    }

    public Optional<Publisher> findPublisher(String pubContractAddress) {
        return publisherRepo.findById(pubContractAddress);
    }

    public void updateBlockNumber(String pubContractAddr, String blockNumber) {
        Optional<Publisher> pub = findPublisher(pubContractAddr);
        if (pub.isEmpty()) {
            log.info("Publisher '{}' was not found.", EthUtil.shortenAddress(pubContractAddr));
            return;
        }
        pub.get().setBlockNumber(blockNumber);
        publisherRepo.save(pub.get());
    }

    private Publisher savePublisher(String pubContractAddr) {
        Publisher pub = new Publisher();
        pub.setContractAddress(pubContractAddr);
        pub.setFeedUrl(ethPubService.getPublisherFeedUrl(pubContractAddr));
        pub.setBlockNumber(Numeric.toHexStringWithPrefix(ethPubService.getCurrentBlockNumber()));
        return publisherRepo.save(pub);
    }

}
