package com.moonstoneid.web3publisher.controller;

import com.moonstoneid.web3publisher.controller.model.ApiItem;
import com.moonstoneid.web3publisher.repo.model.DbItem;
import com.moonstoneid.web3publisher.service.EntryService;
import com.rometools.rome.io.FeedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Controller
public class ViewController {

    private final EntryService entryService;

    public ViewController(EntryService entryService) {
        this.entryService = entryService;
    }

    @GetMapping("/")
    public String getIndex(Model model) {
        List<DbItem> list = entryService.getAll();
        model.addAttribute("items", toApiModel(entryService.getAll()));
        model.addAttribute("item", new ApiItem());
        return "index";
    }

    @GetMapping("/feed/item/{id}")
    public String getItem(@PathVariable String id, Model model) throws FeedException {
        model.addAttribute("item", entryService.getItem(id));
        return "item";
    }

    @PostMapping("/feed/item")
    public String createItem(@ModelAttribute ApiItem apiItem, Model model, HttpServletRequest request) {
        apiItem.setPubDate(OffsetDateTime.now());
        DbItem dbItem = toDbModel(apiItem);

        // Get url
        String url = request.getRequestURL().toString();
        url = url.substring(0, url.indexOf("/feed/item"));
        entryService.save(dbItem, url);

        List<DbItem> list = entryService.getAll();
        model.addAttribute("items", toApiModel(entryService.getAll()));
        model.addAttribute("item", new ApiItem());
        return "redirect:/";
    }

    private static List<ApiItem> toApiModel(List<DbItem> dbItems) {
        List<ApiItem> item = new ArrayList<>();
        for (DbItem dbItem : dbItems) {
            item.add(toApiModel(dbItem));
        }
        return item;
    }

    private static ApiItem toApiModel(DbItem dbItem) {
        ApiItem item = new ApiItem();
        item.setId(dbItem.getId());
        item.setTitle(dbItem.getTitle());
        item.setDescription(dbItem.getDescription());
        item.setPubDate(dbItem.getPubDate());
        return item;
    }

    private static DbItem toDbModel(ApiItem apiItem) {
        DbItem item = new DbItem();
        item.setTitle(apiItem.getTitle());
        item.setDescription(apiItem.getDescription());
        item.setPubDate(apiItem.getPubDate());
        return item;
    }

}
