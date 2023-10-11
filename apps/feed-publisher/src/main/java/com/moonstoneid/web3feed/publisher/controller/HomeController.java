package com.moonstoneid.web3feed.publisher.controller;

import java.util.ArrayList;
import java.util.List;

import com.moonstoneid.web3feed.common.config.EthPublisherProperties;
import com.moonstoneid.web3feed.common.config.EthRegistryProperties;
import com.moonstoneid.web3feed.publisher.config.AppProperties;
import com.moonstoneid.web3feed.publisher.model.Article;
import com.moonstoneid.web3feed.publisher.service.ArticleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController extends BaseController {

    private final ArticleService articleService;

    private final String regContractAddr;
    private final String pubContractAddr;

    public HomeController(AppProperties appProperties, ArticleService articleService,
            EthRegistryProperties ethRegistryProperties,
            EthPublisherProperties ethPublisherProperties) {
        super(appProperties);
        this.articleService = articleService;
        this.regContractAddr = ethRegistryProperties.getContractAddress();
        this.pubContractAddr = ethPublisherProperties.getContractAddress();
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Article> articles = articleService.getAllArticles();
        model.addAttribute("regContractAddr", regContractAddr);
        model.addAttribute("pubContractAddr", pubContractAddr);
        model.addAttribute("articles", ModelMapper.toViewModel(articles));
        return "home";
    }

}
