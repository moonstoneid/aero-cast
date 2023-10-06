package com.moonstoneid.web3feedpublisher.controller;

import java.util.List;

import com.moonstoneid.web3feedpublisher.AppProperties;
import com.moonstoneid.web3feedpublisher.controller.model.EntryVM;
import com.moonstoneid.web3feedpublisher.model.Entry;
import com.moonstoneid.web3feedpublisher.service.EntryService;
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
