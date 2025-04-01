// CorsConfig.java
package com.example.demo.config; // 更换为您的包名

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 对所有的路径应用CORS设置
                .allowedOrigins("http://localhost:3000") // 允许来自 localhost:3000 的跨域请求
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的HTTP方法
                .allowedHeaders("*") // 允许所有的头部信息
                .allowCredentials(true); // 允许发送凭证信息
    }
}
