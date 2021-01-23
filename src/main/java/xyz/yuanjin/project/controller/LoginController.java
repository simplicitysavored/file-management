package xyz.yuanjin.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import xyz.yuanjin.project.common.dto.ResponseDTO;
import xyz.yuanjin.project.common.util.ResponseUtil;
import xyz.yuanjin.project.common.util.StringUtil;
import xyz.yuanjin.project.util.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author yuanjin
 */
@Controller
public class LoginController {

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping("/loginCheck")
    public @ResponseBody
    ResponseDTO loginCheck(String username, String password, HttpServletResponse response) {
        if (StringUtil.isAllNotEmpty(username, password)) {
            if ("admin".equals(username) &&
                    "admin123".equals(password)) {
                return ResponseUtil.success().setToken(JwtUtil.createToken());
            }
        }
        return ResponseUtil.error("用户名或密码错误！");
    }
}
