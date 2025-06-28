package org.example.interceptor;

import org.example.common.Constants;
import org.example.entity.LoginInfo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * 登录拦截器
 * 用于拦截未登录的请求并重定向到登录页面
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // 排除不需要拦截的路径
        if (isExcludedPath(request)) {
            return true;
        }

        HttpSession session = request.getSession();
        LoginInfo user = (LoginInfo) session.getAttribute(Constants.USER_SESSION_KEY);

        if (user == null) {
            handleUnauthorizedRequest(request, response);
            return false;
        }
        return true;
    }

    private boolean isExcludedPath(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/auth") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.equals("/error");
    }

    private void handleUnauthorizedRequest(HttpServletRequest request,
                                           HttpServletResponse response) throws IOException {

        String requestedWith = request.getHeader("X-Requested-With");
        boolean isAjax = "XMLHttpRequest".equals(requestedWith) ||
                "application/json".equals(request.getContentType());

        if (isAjax) {
            // AJAX请求返回JSON
            response.setContentType("application/json");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("{\"code\":401,\"message\":\"请先登录\"}");
        } else {
            // 普通请求重定向到登录页面
            String redirectUrl = request.getRequestURL().toString();
            response.sendRedirect("/api/auth/login?redirect=" +
                    URLEncoder.encode(redirectUrl, "UTF-8"));
        }
    }
}