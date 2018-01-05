package kwasilewski.marketplace.configuration;

import kwasilewski.marketplace.configuration.context.ServiceContextArgumentResolver;
import kwasilewski.marketplace.interceptor.JwtSecuredInterceptor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
@ComponentScan("kwasilewski.marketplace")
public class SpringServerConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JwtSecuredInterceptor())
                .addPathPatterns("/rest/**")
                .excludePathPatterns("/rest/login", "/rest/users/create");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new ServiceContextArgumentResolver());
    }

}
