package kwasilewski.marketplace.configuration;

import kwasilewski.marketplace.configuration.context.ServiceContextArgumentResolver;
import kwasilewski.marketplace.interceptor.JwtSecuredInterceptor;
import kwasilewski.marketplace.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
@ComponentScan("kwasilewski.marketplace")
public class SpringServerConfig extends WebMvcConfigurerAdapter {

    private final TokenService tokenService;

    @Autowired
    public SpringServerConfig(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JwtSecuredInterceptor())
                .addPathPatterns("/rest/**")
                .excludePathPatterns("/rest/login", "/rest/register");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new ServiceContextArgumentResolver(tokenService));
    }

}
