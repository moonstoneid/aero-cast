package com.moonstoneid.web3feed.publisher.controller;

import com.moonstoneid.web3feed.publisher.service.EntryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/entry")
public class EntryController {

    private final EntryService entryService;

    public EntryController(EntryService entryService) {
        this.entryService = entryService;
    }

    @GetMapping("")
    public String entries() {
        return "redirect:/";
    }

    @GetMapping("/{id}")
    public String entry(@PathVariable String id, Model model) {
        model.addAttribute("entry", entryService.getEntry(id));
        return "entry";
    }

}
