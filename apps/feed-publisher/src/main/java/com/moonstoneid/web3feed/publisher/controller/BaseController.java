package com.moonstoneid.web3feed.publisher.controller;

import com.moonstoneid.web3feed.publisher.config.AppProperties;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

abstract class BaseController {

    private final String appTitle;
    private final String appSubTitle;

    protected BaseController(AppProperties appProperties) {
        this.appTitle = appProperties.getTitle();
        this.appSubTitle = appProperties.getSubTitle();
    }

    @ModelAttribute
    protected void addBaseAttributes(Model model) {
        model.addAttribute("appTitle", appTitle);
        model.addAttribute("appSubTitle", appSubTitle);
    }

}
