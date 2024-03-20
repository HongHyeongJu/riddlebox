package com.labmate.riddlebox.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StaticResourceConfig {

    @Value("${app.static.url}")
    private String staticResourceUrl;

    public String getStaticResourceUrl() {
        return staticResourceUrl;
    }
}
