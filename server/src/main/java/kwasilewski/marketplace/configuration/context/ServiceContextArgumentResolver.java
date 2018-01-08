package kwasilewski.marketplace.configuration.context;

import io.jsonwebtoken.SignatureException;
import kwasilewski.marketplace.configuration.context.annotation.ServiceContext;
import kwasilewski.marketplace.errors.MKTException;
import kwasilewski.marketplace.services.TokenService;
import kwasilewski.marketplace.util.JwtTokenUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class ServiceContextArgumentResolver extends AbstractHandlerMethodArgumentResolver {

    private final TokenService tokenService;
    private final Logger logger;

    public ServiceContextArgumentResolver(TokenService tokenService) {
        this.tokenService = tokenService;
        this.logger = LogManager.getLogger(ServiceContextArgumentResolver.class);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return findMethodAnnotation(ServiceContext.class, parameter) != null && UserContext.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public UserContext resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String token = webRequest.getHeader("token");
        UserContext ctx = new UserContext();
        if (token != null) {
            try {
                ctx.setUserId(JwtTokenUtil.getIdFromToken(token));
                ctx.changeUser(tokenService.checkToken(token));
            } catch (SignatureException | MKTException e) {
                logger.warn("resolveArgument", "Invalid JwtToken");
            }
        }
        return ctx;
    }

}
