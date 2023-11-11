package com.moonstoneid.aerocast.publisher.eth;

import com.moonstoneid.aerocast.common.config.EthPublisherProperties;
import com.moonstoneid.aerocast.common.eth.BaseEthAdapter;
import com.moonstoneid.aerocast.common.eth.contracts.FeedPublisher;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

@Service
public class EthPublisherAdapter extends BaseEthAdapter {

    private final Credentials pubCredentials;
    private final String pubContractAddr;

    public EthPublisherAdapter(Web3j web3j, EthPublisherProperties ethPublisherProperties) {
        super(web3j);
        this.pubCredentials = Credentials.create(ethPublisherProperties.getPrivateKey());
        this.pubContractAddr = ethPublisherProperties.getContractAddress();
    }

    public void publish(String link) {
        FeedPublisher contract = createPublisherContract(pubContractAddr, pubCredentials);
        try {
            contract.publish(link).sendAsync().get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
