package com.ecommerceapi.ecomerceapi;

import com.ecommerceapi.ecomerceapi.filters.AuthFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.TimeZone;

@EnableJms
@SpringBootApplication(scanBasePackages={
		"com.ecommerceapi.ecomerceapi"})
public class EcomerceApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcomerceApiApplication.class, args);
	}

	@Bean
	public WebClient.Builder getWebClientBuilder(){
		return WebClient.builder().defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
	}

	@Bean
	public FilterRegistrationBean<AuthFilter> FilterRegistrationBean(){
		FilterRegistrationBean<AuthFilter> registrationBean = new FilterRegistrationBean<>();
		AuthFilter authFilter  = new AuthFilter();
		registrationBean.setFilter(authFilter);
		registrationBean.addUrlPatterns("/merchant/profile_view");
		registrationBean.addUrlPatterns("/merchant/update");
		registrationBean.addUrlPatterns("/merchant/dashboard");
		registrationBean.addUrlPatterns("/merchant/update");
		registrationBean.addUrlPatterns("/merchant/list");

		registrationBean.addUrlPatterns("/item/*");

		registrationBean.addUrlPatterns("/order_type/*");
		registrationBean.addUrlPatterns("/order/incoming");
		registrationBean.addUrlPatterns("/order/incoming/reject");
		registrationBean.addUrlPatterns("/order/ongoing");
		registrationBean.addUrlPatterns("/order/ongoing/reject");
		registrationBean.addUrlPatterns("/order/settled");

		registrationBean.addUrlPatterns("/officer");
		registrationBean.addUrlPatterns("/officer/profile_view");
		registrationBean.addUrlPatterns("/officer/profile_delete");
		registrationBean.addUrlPatterns("/officer/photo");
		registrationBean.addUrlPatterns("/officer/pin");
		registrationBean.addUrlPatterns("/officer/merchant");
		registrationBean.addUrlPatterns("/officer/merchant_view");
		registrationBean.addUrlPatterns("/officer/merchant_delete");
		registrationBean.addUrlPatterns("/officer/customer");
		registrationBean.addUrlPatterns("/officer/customer_view");
		registrationBean.addUrlPatterns("/officer/customer_delete");
		registrationBean.addUrlPatterns("/officer/order");
		registrationBean.addUrlPatterns("/officer/order/incoming");
		registrationBean.addUrlPatterns("/officer/order/incoming-reject");
		registrationBean.addUrlPatterns("/officer/order/ongoing");
		registrationBean.addUrlPatterns("/officer/order/settled");
		registrationBean.addUrlPatterns("/officer/order/all");
		registrationBean.addUrlPatterns("/officer/order/all-list");
		registrationBean.addUrlPatterns("/officer/upload/*");
		registrationBean.addUrlPatterns("/officer/upload_attribute");
		registrationBean.addUrlPatterns("/officer/upload_merchant");

		registrationBean.addUrlPatterns("/role/*");

		return registrationBean;
	}
}
