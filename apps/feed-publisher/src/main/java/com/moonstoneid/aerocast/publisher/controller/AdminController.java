package com.moonstoneid.aerocast.publisher.controller;

import java.time.OffsetDateTime;

import com.moonstoneid.aerocast.publisher.config.AppProperties;
import com.moonstoneid.aerocast.publisher.controller.model.CreateArticleVM;
import com.moonstoneid.aerocast.publisher.model.Article;
import com.moonstoneid.aerocast.publisher.service.ArticleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller()
@RequestMapping(path = "/admin")
public class AdminController extends BaseController {

    private final ArticleService articleService;

    public AdminController(AppProperties appProperties, ArticleService articleService) {
        super(appProperties);
        this.articleService = articleService;
    }

    @GetMapping("")
    public String admin(@RequestParam(value = "success", required = false) Boolean success,
            Model model) {
        model.addAttribute("success", success);
        model.addAttribute("article", new CreateArticleVM());
        return "admin";
    }

    @PostMapping("/article")
    public String createArticle(@ModelAttribute CreateArticleVM createArticleVM) {
        Article article = ModelMapper.toModel(createArticleVM, OffsetDateTime.now());

        articleService.createArticle(article);

        return "redirect:/admin?success=true";
    }

}
