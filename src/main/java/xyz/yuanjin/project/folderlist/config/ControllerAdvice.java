package xyz.yuanjin.project.folderlist.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author yuanjin
 */
@Slf4j
@org.springframework.web.bind.annotation.ControllerAdvice
@ResponseBody
public class ControllerAdvice {

    @ExceptionHandler(Exception.class)
    public String exceptionHandle(Exception e) {
        log.error(e.getMessage(), e);
        return "{\n" +
                "  \"code\": \"500\",\n" +
                "  \"message\": \"" + e.getMessage() + "\"\n" +
                "}";
    }
}
