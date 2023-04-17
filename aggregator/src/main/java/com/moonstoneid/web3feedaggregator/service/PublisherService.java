package com.moonstoneid.web3feedaggregator.service;

import java.util.List;
import java.util.Optional;

import com.moonstoneid.web3feedaggregator.eth.EthPublisherEventListener;
import com.moonstoneid.web3feedaggregator.eth.EthService;
import com.moonstoneid.web3feedaggregator.eth.EthUtil;
import com.moonstoneid.web3feedaggregator.model.Publisher;
import com.moonstoneid.web3feedaggregator.repo.PublisherRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.utils.Numeric;

@Service
@Slf4j
public class PublisherService {

    private final PublisherRepo publisherRepo;

    private final EntryService entryservice;

    private final EthService ethService;
    private final EthPublisherEventListener ethPubEventListener;

    public PublisherService(PublisherRepo publisherRepo, EntryService entryservice,
            EthService ethService) {
        this.publisherRepo = publisherRepo;
        this.entryservice = entryservice;
        this.ethService = ethService;
        this.ethPubEventListener = new EthPublisherEventListener(this, entryservice,
                ethService, ethService.getWeb3j());
    }

    public List<Publisher> getPublishers() {
        return publisherRepo.findAll();
    }

    public Publisher createPublisherIfNotExists(String pubContrAddr) {
        pubContrAddr = pubContrAddr.toLowerCase();
        Optional<Publisher> pub = findPublisher(pubContrAddr);
        if (pub.isPresent()) {
            return pub.get();
        }

        log.info("Trying to create publisher '{}' ...", pubContrAddr);
        Publisher publisher = savePublisher(pubContrAddr);
        log.info("Publisher '{}' was created.", EthUtil.shortenAddress(pubContrAddr));

        // Fetch entries for new publisher
        entryservice.fetchEntries(pubContrAddr);

        // Register publisher event listener
        ethPubEventListener.registerPubEventListener(pubContrAddr);

        return publisher;
    }

    public void removePublisher(String pubContrAddr) {
        log.info("Trying to remove publisher '{}' ...", EthUtil.shortenAddress(pubContrAddr));

        Optional<Publisher> pub = findPublisher(pubContrAddr);
        if (pub.isEmpty()) {
            log.info("Publisher '{}' was not found.", EthUtil.shortenAddress(pubContrAddr));
            return;
        }
        ethPubEventListener.unregisterPubEventListener(pubContrAddr);
        entryservice.removeEntriesByPublisher(pubContrAddr);
        publisherRepo.deleteById(pubContrAddr);

        log.info("Publisher '{}' has been removed.", EthUtil.shortenAddress(pubContrAddr));
    }

    public Optional<Publisher> findPublisher(String pubContrAddr) {
        return publisherRepo.findById(pubContrAddr);
    }

    public void updateBlockNumber(String pubContrAddr, String blockNumber) {
        Optional<Publisher> pub = findPublisher(pubContrAddr);
        if (pub.isEmpty()) {
            log.info("Publisher '{}' was not found.", EthUtil.shortenAddress(pubContrAddr));
            return;
        }
        pub.get().setBlockNumber(blockNumber);
        publisherRepo.save(pub.get());
    }

    private Publisher savePublisher(String pubContrAddr) {
        Publisher pub = new Publisher();
        pub.setContractAddress(pubContrAddr);
        pub.setFeedUrl(ethService.getPublisherFeedUrl(pubContrAddr));
        pub.setBlockNumber(Numeric.toHexStringWithPrefix(ethService.getCurrentBlockNumber()));
        return publisherRepo.save(pub);
    }

}
