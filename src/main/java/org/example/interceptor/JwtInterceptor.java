package org.example.interceptor;

import org.example.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JwtInterceptor 类用于拦截 HTTP 请求，验证 JWT Token 的有效性。
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;  // 用于处理 JWT Token 的工具类

    /**
     * 在请求处理之前进行 JWT Token 验证。
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @param handler  处理器
     * @return boolean  是否继续处理请求
     * @throws Exception 可能抛出的异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader("Authorization");  // 从请求头中获取 Authorization

        // 检查 Authorization 头是否存在且以 "Bearer " 开头
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "缺少有效的token");  // 返回 401 未授权错误
            return false;  // 拦截请求
        }

        String token = authHeader.substring(7);  // 提取 Token
        System.out.println(token);  // 输出 Token（仅用于调试）

        try {
            // 验证 Token 是否过期
            if (jwtTokenUtil.isTokenExpired(token)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "token已过期");  // 返回 401 未授权错误
                return false;  // 拦截请求
            }
            // 从 Token 中获取用户名，并存入请求属性以供后续使用
            String username = jwtTokenUtil.getUsernameFromToken(token);
            request.setAttribute("username", username);
            return true;  // 继续处理请求
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "无效的token");  // 返回 401 未授权错误
            return false;  // 拦截请求
        }
    }
}