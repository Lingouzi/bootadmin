package com.ly.bootadmin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ly.bootadmin.config.JsonObjectMapper;
import com.ly.bootadmin.web.listener.InitListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

import java.util.List;

/**
 * 注解解释:
 * @EnableJms : 开启jms 消息队列支持
 *
 * @EnableCaching: 开启redis等,缓存支持
 *
 * @author linyun
 * @EnableCaching: 开启缓存支持, 参考:https://www.cnblogs.com/gdpuzxs/p/7222309.html
 */
@EnableJms
@EnableCaching
@SpringBootApplication
@EnableWebSocketMessageBroker
public class BootadminApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(BootadminApplication.class, args);
    }

    /**
     * object转化为jackson时,将null值转为空串返回.
     *
     * @param converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter(jsonObjectMapper()));
    }

    @Bean
    public ObjectMapper jsonObjectMapper() {
        return new JsonObjectMapper();
    }

    /**
     * listener的配置
     *
     * @return
     */
    @Bean
    public InitListener init() {
        return new InitListener();
    }
//
//    @Bean
//    public Init2Listener init2() {
//        return new Init2Listener();
//    }

//    /**
//     * 自定义拦截器
//     */
//    @Autowired
//    private MyInterceptor myInterceptor;
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        //默认拦截所有的uri请求,也可以单独配置拦截某些特定的请求,去除某些请求等.
//        registry.addInterceptor(myInterceptor).addPathPatterns("/*")
//                .excludePathPatterns(
//                        "/static/**",
//                        "/public/**",
//                        "/",
//                        "/login**");
//    }

    /**
     * 多个filter的设置方式，参看： https://blog.csdn.net/qq_21019419/article/details/83931864
     * 为了开发方便，这里去掉了filter
     * @return
     */
//    @Bean
//    public FilterRegistrationBean<UriFilter> uriFilter() {
//        FilterRegistrationBean<UriFilter> filter = new FilterRegistrationBean<>(new UriFilter());
//        // 监听的url, 正则
//        filter.addUrlPatterns("/*");
//        // 多个过滤器可以设置order级别. 数字越小,优先执行
//        filter.setOrder(Integer.MIN_VALUE + 1);
//        return filter;
//    }
//
//    @Bean
//    public FilterRegistrationBean<UriFilter2> uriFilter2() {
//        FilterRegistrationBean<UriFilter2> filter = new FilterRegistrationBean<>(new UriFilter2());
//        filter.addUrlPatterns("/*");
//        filter.setOrder(Integer.MIN_VALUE);
//        return filter;
//    }


}
