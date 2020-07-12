package xyz.yuanjin.project.folderlist.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import xyz.yuanjin.project.folderlist.property.NasProperty;

import java.io.File;
import java.util.regex.Pattern;

/**
 * @author yuanjin
 */
@Component
public class NasUtil implements ApplicationContextAware {
    private static ApplicationContext APPLICATION_CONTEXT;

    public static NasProperty systemProperty() {
        return APPLICATION_CONTEXT.getBean(NasProperty.class);
    }

    public static boolean isProtectFile(File file) {
        for (Pattern pattern : NasUtil.systemProperty().getNasProtectFilePatterns()) {
            if (pattern.matcher(file.getAbsolutePath()).matches()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        NasUtil.APPLICATION_CONTEXT = applicationContext;
    }
}
