package kwasilewski.marketplace.interceptor;


import kwasilewski.marketplace.util.JwtTokenUtil;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JwtSecuredInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        try {
            JwtTokenUtil.getIdFromToken(request.getHeader("token"));
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        return super.preHandle(request, response, handler);
    }

}
