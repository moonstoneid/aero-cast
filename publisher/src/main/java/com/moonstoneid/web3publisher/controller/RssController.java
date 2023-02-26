package com.moonstoneid.web3publisher.controller;

import com.moonstoneid.web3publisher.config.FeedConfig;
import com.moonstoneid.web3publisher.repo.model.DbItem;
import com.moonstoneid.web3publisher.service.EntryService;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedOutput;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class RssController {

    private final FeedConfig cfg;
    private final EntryService entryService;

    public RssController(FeedConfig cfg, EntryService entryService) {
        this.cfg = cfg;
        this.entryService = entryService;
    }

    @RequestMapping(value = "/rss", produces = MediaType.APPLICATION_XML_VALUE, method = RequestMethod.GET)
    public String getRss(HttpServletRequest request) throws FeedException {
        String url = request.getRequestURL().toString();
        url = url.substring(0, url.indexOf("/rss"));
        List<SyndEntry> list = toRss(entryService.getAll(), url);
        SyndFeed feed = cfg.getFeed();
        feed.setEntries(list);
        return new SyndFeedOutput().outputString(feed);
    }

    private static List<SyndEntry> toRss(List<DbItem> dbItems, String url) {
        List<SyndEntry> entries = new ArrayList<>();
        for (DbItem dbItem : dbItems) {
            SyndEntry entry = new SyndEntryImpl();
            entry.setTitle(dbItem.getTitle());
            SyndContentImpl description = new SyndContentImpl();
            description.setType("text/plain");
            description.setValue(dbItem.getDescription());
            entry.setPublishedDate(new Date(dbItem.getPubDate().toInstant().toEpochMilli()));
            entry.setDescription(description);
            entry.setLink(url + "/feed/item/" + dbItem.getId());

            entries.add(entry);
        }
        return entries;
    }

}
