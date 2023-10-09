package com.moonstoneid.web3feed.publisher.eth;

import com.moonstoneid.web3feed.common.config.EthPublisherProperties;
import com.moonstoneid.web3feed.common.eth.BaseEthProxy;
import com.moonstoneid.web3feed.common.eth.contracts.FeedPublisher;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

@Service
public class EthPublisherAdapter extends BaseEthProxy {

    private final Credentials pubCredentials;
    private final String pubContractAddr;

    public EthPublisherAdapter(Web3j web3j, EthPublisherProperties ethPublisherProperties) {
        super(web3j);
        this.pubCredentials = Credentials.create(ethPublisherProperties.getPrivateKey());
        this.pubContractAddr = ethPublisherProperties.getContractAddress();
    }

    public void publish(String link) {
        FeedPublisher contract = FeedPublisher.load(pubContractAddr, web3j, pubCredentials,
                contractGasProvider);
        try {
            contract.publish(link).sendAsync().get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
