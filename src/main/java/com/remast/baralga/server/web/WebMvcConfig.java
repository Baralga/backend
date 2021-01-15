package com.remast.baralga.server.web;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.Filter;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(CacheControl.maxAge(10, TimeUnit.DAYS))
                .resourceChain(true);
    }

    @Bean
    public Filter filter(){
        var shallowEtagHeaderFilter = new ShallowEtagHeaderFilter();
        shallowEtagHeaderFilter.setWriteWeakETag(true);
        return shallowEtagHeaderFilter;
    }

    @Bean
    @ConditionalOnMissingBean(
            name = {"thymeleafTurboViewResolver"}
    )
    ThymeleafViewResolver thymeleafTurboViewResolver(ThymeleafProperties properties, SpringTemplateEngine templateEngine) {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine);
        resolver.setCharacterEncoding(properties.getEncoding().name());
        resolver.setContentType("text/vnd.turbo-stream.html");
        resolver.setProducePartialOutputWhileProcessing(properties.getServlet().isProducePartialOutputWhileProcessing());
        resolver.setExcludedViewNames(properties.getExcludedViewNames());
        resolver.setViewNames(properties.getViewNames());
        resolver.setOrder(2147483644);
        resolver.setCache(properties.isCache());
        return resolver;
    }
}
