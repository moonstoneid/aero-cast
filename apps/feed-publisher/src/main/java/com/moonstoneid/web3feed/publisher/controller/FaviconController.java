package com.moonstoneid.web3feed.publisher.controller;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import com.moonstoneid.web3feed.publisher.config.AppProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

@Controller
@Slf4j
public class FaviconController {

    private final byte[] icon;

    public FaviconController(AppProperties appProperties) {
        icon = readIcon(appProperties.getIconPath());
    }

    @GetMapping(value = "/favicon.ico", produces = { "image/x-icon" })
    public @ResponseBody byte[] getFavIcon() {
        if (icon == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return icon;
    }

    private static byte[] readIcon(String path) {
        try (BufferedInputStream is = new BufferedInputStream(new FileInputStream(path))) {
            return is.readAllBytes();
        } catch (IOException e) {
            log.error("Could not read icon {}!", path, e);
            return null;
        }
    }

}
