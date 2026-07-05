package com.ecommerce.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Value("${app.review-upload-dir:uploads/reviews}")
	private String reviewUploadDirectory;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String location = Paths.get(reviewUploadDirectory)
				.toAbsolutePath()
				.normalize()
				.toUri()
				.toString();
		registry.addResourceHandler("/uploads/reviews/**")
				.addResourceLocations(location.endsWith("/") ? location : location + "/");
	}
}
