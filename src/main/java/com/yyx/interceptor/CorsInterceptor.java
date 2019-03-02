package com.yyx.interceptor;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CorsInterceptor extends HandlerInterceptorAdapter {
    private static final String ALLOW_METHODS = String.join(",",
            "GET", "HEAD", "OPTIONS", "POST", "PUT"
    );
    private static final String ALLOW_HEADERS = String.join(",",
            "Access-Control-Allow-Headers", "Origin", "Accept",
            "X-Requested-With", "Content-Type", "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        response.addHeader("Access-Control-Allow-Origin", "http://localhost:8080");
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.addHeader("Access-Control-Allow-Headers", ALLOW_HEADERS);
        response.addHeader("Access-Control-Allow-Methods", ALLOW_METHODS);
        response.addHeader("Access-Control-Expose-Headers", "*");
        return true;
    }
}
