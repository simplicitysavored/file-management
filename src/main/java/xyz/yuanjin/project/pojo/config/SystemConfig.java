package xyz.yuanjin.project.pojo.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author yuanjin
 */
@Data
@Slf4j
public class SystemConfig {
    /**
     * 监听的文件夹
     */
    private List<File> listenFolder;
    /**
     * 受保护的文件的规则配置
     */
    private ProtectConfig protectConfig;

    private static SystemConfig instance;

    public static SystemConfig getInstance() {
        if (instance == null) {
            synchronized (SystemConfig.class) {
                if (instance == null) {
                    instance = new SystemConfig();
                    try {
                        instance.loadSystemConfig();
                    } catch (IOException | DocumentException e) {
                        log.error("系统初始化失败，请检查配置：file-management-config.xml");
                    }
                }
            }
        }
        return instance;
    }

    private SystemConfig() {
        listenFolder = new ArrayList<>();
        protectConfig = new ProtectConfig();
    }

    private void loadSystemConfig() throws IOException, DocumentException {
        InputStream in = SystemConfig.class.getClassLoader().getResourceAsStream("file-management-config.xml");
        if (in == null) {
            log.error("系统配置初始化失败，请检查配置：{}", "file-management-config.xml");
            return;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024];
        int length;
        while ((length = in.read(bytes)) != -1) {
            bos.write(bytes, 0, length);
        }
        String configXmlString = bos.toString();
        Document document = DocumentHelper.parseText(configXmlString);
        Element rootEl = document.getRootElement();
        Element listenEl = rootEl.element("listen-folder");
        List<Element> listenItemEl = listenEl.elements("item");
        listenItemEl.forEach(itemEl -> {
            File file = new File(itemEl.getTextTrim());
            if (file.exists()) {
                listenFolder.add(file);
            } else {
                log.error("系统配置失效，监听路径不存在：{}", itemEl.getTextTrim());
            }
        });

        Element protectFileEl = rootEl.element("protect-file");

        Element pathEl = protectFileEl.element("path");
        List<Element> pathItemEls = pathEl.elements("item");
        pathItemEls.forEach(item -> this.protectConfig.getFilePath().add(item.getTextTrim()));

        Element regexEl = protectFileEl.element("file-path-regex");
        List<Element> regexItmEls = regexEl.elements("item");
        regexItmEls.forEach(item -> this.protectConfig.getFilePathRegexList().add(Pattern.compile(item.getTextTrim())));

        bos.close();
        in.close();
    }
}
