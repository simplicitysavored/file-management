package xyz.yuanjin.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import xyz.yuanjin.project.util.SystemUtil;

/**
 * @author yuanjin
 */
@EnableWebMvc
@SpringBootApplication
public class FileManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileManagementApplication.class, args);
        SystemUtil.printSystemLink();
    }

}
