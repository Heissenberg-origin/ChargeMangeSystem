package org.example.config;

import org.example.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册拦截器
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**") // 拦截所有路径
                .excludePathPatterns(
                        "/api/auth/login",  // 登录接口
                        "/error",           // 错误页面
                        "/swagger-ui/**",   // Swagger UI
                        "/v3/api-docs/**",  // API文档
                        "/doc.html",        // 文档页面
                        "/webjars/**",      // Webjars
                        "/favicon.ico"      // 图标
                );
    }
}