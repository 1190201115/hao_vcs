package com.cyh.hao_vcs.filter;

import com.cyh.hao_vcs.common.ThreadLocalContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * 需要在启动类配置@ServletComponentScan注解
 */
@Slf4j
@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    public static final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        String uri = httpServletRequest.getRequestURI();
        String[] allowsURIs = {
                "/page/login.html",
                "/page/reset.html",
                "/page/sign_up.html",
                "/image/**",
                "/js/**",
                "/plugins/**",
                "/styles/**",
                "/doSignUp",
                "/doLogin",
                "/doReset",
        };
        String simpleURI = "/";

        if (check(allowsURIs, uri)) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        Long userId = (Long) httpServletRequest.getSession().getAttribute("user");
        if (!Objects.isNull(userId)) {
//            ThreadLocalContext.setCurrentId(userId);
            if (check(simpleURI, uri)) {
                httpServletResponse.sendRedirect("/page/home.html");
            } else {
                filterChain.doFilter(httpServletRequest, httpServletResponse);
            }
            return;
        }
        httpServletResponse.sendRedirect("/page/login.html");


    }

    public boolean check(String[] allowURIs, String uri) {
        for (String allowURI : allowURIs) {
            if (pathMatcher.match(allowURI, uri)) return true;
        }
        return false;
    }

    public boolean check(String allowURI, String uri) {
        return pathMatcher.match(allowURI, uri);
    }
}
