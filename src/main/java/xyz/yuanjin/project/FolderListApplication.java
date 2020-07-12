package xyz.yuanjin.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * @author yuanjin
 */
@EnableWebMvc
@SpringBootApplication
public class FolderListApplication {

    public static void main(String[] args) {
        SpringApplication.run(FolderListApplication.class, args);
    }

}
