package com.moonstoneid.web3publisher.controller;

import java.util.List;

import com.moonstoneid.web3publisher.AppProperties;
import com.moonstoneid.web3publisher.controller.model.EntryVM;
import com.moonstoneid.web3publisher.model.Entry;
import com.moonstoneid.web3publisher.service.EntryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController extends BaseController {

    private final AppProperties appProperties;
    private final EntryService entryService;

    public HomeController(AppProperties appProperties, EntryService entryService) {
        this.appProperties = appProperties;
        this.entryService = entryService;
    }

    @GetMapping("/")
    public String getIndex(Model model) {
        List<Entry> entries = entryService.getAllEntries();
        model.addAttribute("regContractAddr", appProperties.getEth().getRegContractAddress());
        model.addAttribute("pubContractAddr", appProperties.getEth().getPubContractAddress());
        model.addAttribute("entries", ModelMapper.toViewModel(entries));
        model.addAttribute("entry", new EntryVM());
        return "home";
    }

}
