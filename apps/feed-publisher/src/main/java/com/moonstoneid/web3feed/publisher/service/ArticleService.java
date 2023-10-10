package com.moonstoneid.web3feed.publisher.service;

import java.util.List;

import com.moonstoneid.web3feed.publisher.config.AppProperties;
import com.moonstoneid.web3feed.publisher.eth.EthPublisherAdapter;
import com.moonstoneid.web3feed.publisher.repo.ArticleRepo;
import com.moonstoneid.web3feed.publisher.model.Article;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class ArticleService {

    private final String baseUrl;

    private final ArticleRepo articleRepo;
    private final EthPublisherAdapter ethPublisherAdapter;

    public ArticleService(AppProperties appProperties, ArticleRepo articleRepo,
            EthPublisherAdapter ethPublisherAdapter) {
        this.baseUrl = appProperties.getBaseUrl();
        this.articleRepo = articleRepo;
        this.ethPublisherAdapter = ethPublisherAdapter;
    }

    public List<Article> getAllArticles() {
        return articleRepo.findAll();
    }

    public Article getArticle(String id) {
        return articleRepo.findById(id).get();
    }

    public void createArticle(Article article) {
        Assert.notNull(article, "article cannot be null");

        Article savedArticle = articleRepo.save(article);

        ethPublisherAdapter.publish(baseUrl + "/article/" + savedArticle.getId());
    }

}
