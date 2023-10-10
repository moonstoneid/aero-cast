package com.moonstoneid.web3feed.publisher.controller;

import java.time.OffsetDateTime;
import java.util.List;

import com.moonstoneid.web3feed.publisher.controller.model.EntryVM;
import com.moonstoneid.web3feed.publisher.model.Entry;
import com.moonstoneid.web3feed.publisher.service.EntryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class EntryController {

    private final EntryService entryService;

    public EntryController(EntryService entryService) {
        this.entryService = entryService;
    }

    @PostMapping("/feed/entry")
    public String createEntry(@ModelAttribute EntryVM entryVM, Model model) {
        Entry entry = ModelMapper.toModel(entryVM);
        entry.setDate(OffsetDateTime.now());

        entryService.saveEntry(entry);

        List<Entry> entries = entryService.getAllEntries();
        model.addAttribute("entries", ModelMapper.toViewModel(entries));
        model.addAttribute("entry", new EntryVM());
        return "redirect:/";
    }

    @GetMapping("/feed/entry/{id}")
    public String getEntry(@PathVariable String id, Model model) {
        model.addAttribute("entry", entryService.getEntry(id));
        return "entry";
    }

}
