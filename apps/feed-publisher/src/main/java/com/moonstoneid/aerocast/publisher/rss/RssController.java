package com.moonstoneid.aerocast.publisher.rss;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.moonstoneid.aerocast.publisher.model.Article;
import com.moonstoneid.aerocast.publisher.service.ArticleService;
import com.moonstoneid.aerocast.publisher.config.AppProperties;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedOutput;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RssController {

    private final String appTitle;
    private final String appSubTitle;
    private final String baseUrl;

    private final ArticleService articleService;

    public RssController(AppProperties appProperties, ArticleService articleService) {
        this.appTitle = appProperties.getTitle();
        this.appSubTitle = appProperties.getSubTitle();
        this.baseUrl = appProperties.getBaseUrl();
        this.articleService = articleService;
    }

    @RequestMapping(value = "/rss", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_XML_VALUE)
    public String getRssFeed() throws FeedException {
        List<Article> articles = articleService.getAllArticles();
        SyndFeed feed = createRssFeed(articles);
        return new SyndFeedOutput().outputString(feed);
    }

    private SyndFeed createRssFeed(List<Article> articles) {
        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType("rss_2.0");
        feed.setTitle(appTitle);
        feed.setDescription(appSubTitle);
        feed.setLink(baseUrl);
        feed.setEntries(createRssEntries(articles));
        return feed;
    }

    private List<SyndEntry> createRssEntries(List<Article> articles) {
        List<SyndEntry> entries = new ArrayList<>();
        for (Article article : articles) {
            entries.add(createRssEntry(article));
        }
        return entries;
    }

    private SyndEntry createRssEntry(Article article) {
        SyndEntry entry = new SyndEntryImpl();
        entry.setTitle(article.getTitle());
        entry.setLink(createRssEntryLink(article.getId()));
        SyndContentImpl description = new SyndContentImpl();
        description.setType("text/plain");
        description.setValue(article.getSummary());
        entry.setDescription(description);
        entry.setPublishedDate(new Date(article.getDate().toInstant().toEpochMilli()));
        return entry;
    }

    private String createRssEntryLink(Integer id) {
        return baseUrl + "/article/" + id;
    }

}
