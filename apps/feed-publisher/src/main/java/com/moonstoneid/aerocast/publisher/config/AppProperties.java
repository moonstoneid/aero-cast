package com.moonstoneid.aerocast.publisher.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
@Data
public class AppProperties {
    private String baseUrl;
    private String title;
    private String subTitle;
    private String iconPath;
}
