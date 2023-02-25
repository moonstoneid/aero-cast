package com.moonstoneid.web3publisher.controller;

import com.moonstoneid.web3publisher.controller.model.ApiItem;
import com.moonstoneid.web3publisher.repo.model.DbItem;
import com.moonstoneid.web3publisher.service.EntryService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

    @PostMapping("/feed/create")
    public String createItem(@ModelAttribute ApiItem apiItem, Model model) {
        apiItem.setPubDate(OffsetDateTime.now());
        DbItem dbItem = toDbModel(apiItem);
        entryService.save(dbItem);
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
