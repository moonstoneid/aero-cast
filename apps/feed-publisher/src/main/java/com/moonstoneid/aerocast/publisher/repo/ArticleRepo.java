package com.moonstoneid.aerocast.publisher.repo;

import com.moonstoneid.aerocast.publisher.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepo extends JpaRepository<Article, String> {

}
