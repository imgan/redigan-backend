package com.ecommerceapi.ecomerceapi.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

@Configuration
public class CorsConfig {

    @PostConstruct
    public void init() {

        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Bangkok"));

        System.out.println("Date in UTC: " + new Date().toString());
    }

    @Bean
    public WebMvcConfigurer getCorsConfiguration() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("*")
                        .allowedHeaders("*");
            };
        };
    }

    @Bean
    public FilterRegistrationBean customCorsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.setAllowCredentials(false);
        corsConfiguration
                .setAllowedOrigins(Arrays.asList(CorsConfiguration.ALL));
//        corsConfiguration
//                .setAllowedOrigins(Arrays.asList("https://uat-admin.redigan.co.id","https://admin.redigan.co.id","http://172.26.1.10","http://localhost:3000", "https://redigan.cranium.id","https://redigancms.cranium.id",
//                        "http://localhost:3001","http://localhost:3002",
//                        "https://redigancs.cranium.id","https://uat-app.redigan.co.id","https://merchant.redigan.co.id","https://customer.redigan.co.id",
//                        "https://uat-merchant.redigan.co.id","https://uat-customer.redigan.co.id"));
        corsConfiguration.setAllowedMethods(Arrays.asList(CorsConfiguration.ALL));
        corsConfiguration.setAllowedHeaders(Arrays.asList(CorsConfiguration.ALL));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setName("CORS");
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}
