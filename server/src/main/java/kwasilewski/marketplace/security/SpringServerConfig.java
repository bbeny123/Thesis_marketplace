package kwasilewski.marketplace.security;

import kwasilewski.marketplace.security.context.ServiceContextArgumentResolver;
import kwasilewski.marketplace.service.UserService;
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

    private final UserService userService;

    @Autowired
    public SpringServerConfig(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JwtSecuredInterceptor())
                .addPathPatterns("/rest/**")
                .excludePathPatterns("/rest/provinces", "/rest/hints", "/rest/register", "/rest/login", "/rest/ads", "/rest/ads/*");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new ServiceContextArgumentResolver(userService));
    }

}
