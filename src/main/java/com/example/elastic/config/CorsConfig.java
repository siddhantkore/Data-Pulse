//package com.example.elastic.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class CorsConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**") // Apply CORS configuration to all paths
//                .allowedOrigins("http://localhost:9002") // ⬅️ **CHANGE THIS to your frontend's exact URL**
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow common HTTP methods
//                .allowedHeaders("*") // Allow all headers
//                .allowCredentials(true); // Allow sending cookies/auth headers
//    }
//}
