package com.moonstoneid.web3feed.publisher.controller;

import java.time.OffsetDateTime;

import com.moonstoneid.web3feed.publisher.config.AppProperties;
import com.moonstoneid.web3feed.publisher.controller.model.CreateArticleVM;
import com.moonstoneid.web3feed.publisher.model.Article;
import com.moonstoneid.web3feed.publisher.service.ArticleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller()
@RequestMapping(path = "/admin")
public class AdminController extends BaseController {

    private final ArticleService articleService;

    public AdminController(AppProperties appProperties, ArticleService articleService) {
        super(appProperties);
        this.articleService = articleService;
    }

    @GetMapping("")
    public String admin(Model model) {
        model.addAttribute("article", new CreateArticleVM());
        return "admin";
    }

    @PostMapping("/article")
    public String createArticle(@ModelAttribute CreateArticleVM createArticleVM) {
        Article article = ModelMapper.toModel(createArticleVM, OffsetDateTime.now());

        articleService.createArticle(article);

        return "redirect:/admin";
    }

}
