// package com.cumulusclouds.w4153cumuluscloudsmsusermanagement.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.cors.CorsConfiguration;
// import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
// import org.springframework.web.filter.CorsFilter;

// @Configuration
// public class CorsConfig {

//     @Bean
//     public CorsFilter corsFilter() {
//         CorsConfiguration config = new CorsConfiguration();
//         config.addAllowedOrigin("http://localhost:5173"); // Allow frontend origin
//         config.addAllowedMethod("*");                    // Allow all HTTP methods (POST, GET, OPTIONS, etc.)
//         config.addAllowedHeader("*");                    // Allow all headers
//         config.setAllowCredentials(true);                // Allow credentials (if needed)

//         UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//         source.registerCorsConfiguration("/**", config);
//         return new CorsFilter(source);
//     }
// }

