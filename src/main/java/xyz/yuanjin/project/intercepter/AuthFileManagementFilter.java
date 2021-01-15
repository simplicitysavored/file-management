package xyz.yuanjin.project.intercepter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import xyz.yuanjin.project.common.util.FileUtil;
import xyz.yuanjin.project.common.util.StringUtil;
import xyz.yuanjin.project.util.JwtPayload;
import xyz.yuanjin.project.util.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author yuanjin
 */
@Slf4j
public class AuthFileManagementFilter implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = request.getHeader("token");
        if (null == token) {
            token = request.getParameter("token");
        }

        log.debug("preHandle remote uri: {}, token: {}", request.getRequestURI(), token);

        String uri = request.getRequestURI();
        if (uri.startsWith("/login") || uri.startsWith("/loginCheck") || uri.startsWith("/error") || uri.startsWith("/static")) {
            return true;
        }

        JwtPayload payload = JwtUtil.authToken(token);
        if (payload == null) {
            response.sendRedirect("/login");
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        log.debug("postHandle execute");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.debug("afterCompletion execute");
    }
}
