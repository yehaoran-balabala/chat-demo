package com.example.chat_demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

/**
 * Web配置类
 * 配置静态资源、路由和跨域设置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 配置视图控制器
     * 设置默认页面路由
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 设置默认主页
        registry.addViewController("/").setViewName("forward:/index.html");

        // 添加页面路由，支持单页应用的路由
        registry.addViewController("/chat").setViewName("forward:/chat.html");
        registry.addViewController("/history").setViewName("forward:/history.html");
        registry.addViewController("/about").setViewName("forward:/about.html");

        // 添加组件路由
        registry.addViewController("/components/**").setViewName("forward:/components/{path}");
    }

    /**
     * 配置静态资源处理
     * 设置缓存策略和资源路径
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置静态资源处理
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600) // 缓存1小时
                .resourceChain(true);

        // 特别配置CSS和JS文件
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/")
                .setCachePeriod(3600); // CSS文件缓存1小时

        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/")
                .setCachePeriod(3600); // JS文件缓存1小时

        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/")
                .setCachePeriod(86400); // 图片缓存24小时

        registry.addResourceHandler("/components/**")
                .addResourceLocations("classpath:/static/components/")
                .setCachePeriod(3600); // 组件文件缓存1小时

        // 配置字体文件
        registry.addResourceHandler("/fonts/**")
                .addResourceLocations("classpath:/static/fonts/")
                .setCachePeriod(604800); // 字体文件缓存7天
    }

    /**
     * 配置跨域请求
     * 允许前端开发时的跨域访问
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);

        // 特别配置API路径的跨域
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);

        // SSE流的跨域配置
        registry.addMapping("/stream")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}