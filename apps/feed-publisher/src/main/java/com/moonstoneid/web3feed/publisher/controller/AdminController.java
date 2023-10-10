package com.moonstoneid.web3feed.publisher.controller;

import java.time.OffsetDateTime;

import com.moonstoneid.web3feed.publisher.controller.model.EntryVM;
import com.moonstoneid.web3feed.publisher.model.Entry;
import com.moonstoneid.web3feed.publisher.service.EntryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller()
@RequestMapping(path = "/admin")
public class AdminController {

    private final EntryService entryService;

    public AdminController(EntryService entryService) {
        this.entryService = entryService;
    }

    @GetMapping("")
    public String admin(Model model) {
        model.addAttribute("entry", new EntryVM());
        return "admin";
    }

    @PostMapping("/entry")
    public String createEntry(@ModelAttribute EntryVM entryVM) {
        Entry entry = ModelMapper.toModel(entryVM);
        entry.setDate(OffsetDateTime.now());

        entryService.saveEntry(entry);

        return "redirect:/admin";
    }

}
