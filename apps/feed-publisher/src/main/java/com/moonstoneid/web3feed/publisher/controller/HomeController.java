package com.moonstoneid.web3feed.publisher.controller;

import java.util.List;

import com.moonstoneid.web3feed.common.config.EthPublisherProperties;
import com.moonstoneid.web3feed.common.config.EthRegistryProperties;
import com.moonstoneid.web3feed.publisher.controller.model.EntryVM;
import com.moonstoneid.web3feed.publisher.model.Entry;
import com.moonstoneid.web3feed.publisher.service.EntryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final EntryService entryService;

    private final String regContractAddr;
    private final String pubContractAddr;

    public HomeController(EntryService entryService, EthRegistryProperties ethRegistryProperties,
            EthPublisherProperties ethPublisherProperties) {
        this.entryService = entryService;
        this.regContractAddr = ethRegistryProperties.getContractAddress();
        this.pubContractAddr = ethPublisherProperties.getContractAddress();
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Entry> entries = entryService.getAllEntries();
        model.addAttribute("regContractAddr", regContractAddr);
        model.addAttribute("pubContractAddr", pubContractAddr);
        model.addAttribute("entries", ModelMapper.toViewModel(entries));
        return "home";
    }

}
