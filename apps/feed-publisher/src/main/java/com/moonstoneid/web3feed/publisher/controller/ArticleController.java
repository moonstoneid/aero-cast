package com.moonstoneid.web3feed.publisher.controller;

import com.moonstoneid.web3feed.publisher.service.ArticleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/article")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("")
    public String articles() {
        return "redirect:/";
    }

    @GetMapping("/{id}")
    public String article(@PathVariable String id, Model model) {
        model.addAttribute("article", articleService.getArticle(id));
        return "article";
    }

}
