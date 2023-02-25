package com.moonstoneid.web3publisher.config;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeedConfig {

    @Bean
    public SyndFeed getFeed() {
        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType("rss_2.0");
        feed.setTitle("A sample web3 publisher");
        feed.setDescription("A sample web3 feed");
        feed.setLink("https://example.org");

        return feed;
    }

}
