package com.moonstoneid.aerocast.publisher.controller;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import com.moonstoneid.aerocast.publisher.controller.model.ArticleVM;
import com.moonstoneid.aerocast.publisher.controller.model.CreateArticleVM;
import com.moonstoneid.aerocast.publisher.model.Article;

public final class ModelMapper {

    private ModelMapper() {}

    public static List<ArticleVM> toViewModel(List<Article> articles) {
        List<ArticleVM> articleVMs = new ArrayList<>();
        for (Article article : articles) {
            articleVMs.add(toViewModel(article));
        }
        return articleVMs;
    }

    public static ArticleVM toViewModel(Article article) {
        ArticleVM articleVM = new ArticleVM();
        articleVM.setId(article.getId());
        articleVM.setTitle(article.getTitle());
        articleVM.setSummary(article.getSummary());
        articleVM.setContent(article.getContent());
        articleVM.setDate(article.getDate());
        return articleVM;
    }

    public static Article toModel(CreateArticleVM createArticleVM, OffsetDateTime date) {
        Article article = new Article();
        article.setTitle(createArticleVM.getTitle());
        article.setSummary(createArticleVM.getSummary());
        article.setContent(createArticleVM.getContent());
        article.setDate(date);
        return article;
    }

}
