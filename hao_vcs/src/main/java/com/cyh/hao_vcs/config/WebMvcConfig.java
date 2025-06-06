package com.cyh.hao_vcs.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/bigWork/image/**").addResourceLocations("file:D:/ADeskTop/project/bigWork/image/");
        registry.addResourceHandler("/html/diff/**").addResourceLocations("file:D:/ADeskTop/project/bigWork/html/diff/");
        registry.addResourceHandler("/repository/**").addResourceLocations("file:D:/ADeskTop/project/bigWork/repository/");
        registry.addResourceHandler("/player/**").addResourceLocations("file:E:/project/test/");
    }


}
