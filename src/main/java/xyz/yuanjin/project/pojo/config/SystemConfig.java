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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author yuanjin
 */
@Data
@Slf4j
public class SystemConfig {
    private List<String> listenFolderStrList;
    /**
     * 监听的文件夹
     */
    private List<File> listenFolderList;

    private Map<String, File> listenFolderAliasMap;

    private Map<String, File> listenFolderIdMap;

    private Map<String, String> listenFolderIdAliasMap;

    private Map<String, String> listenFolderPathIdMap;

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
        listenFolderStrList = new ArrayList<>();
        listenFolderList = new ArrayList<>();
        protectConfig = new ProtectConfig();
        listenFolderAliasMap = new HashMap<>();
        listenFolderIdMap = new HashMap<>();
        listenFolderIdAliasMap = new HashMap<>();
        listenFolderPathIdMap = new HashMap<>();
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
            String lfId = itemEl.attributeValue("id");
            String lfPath = itemEl.getTextTrim();
            File file = new File(lfPath);
            if (file.exists()) {
                listenFolderStrList.add(lfPath);
                listenFolderList.add(file);
                if (null == lfId || listenFolderIdMap.containsKey(lfId)) {
                    throw new NullPointerException("监听驱动必须要有唯一的id");
                }
                String alias = itemEl.attributeValue("alias");
                if (alias == null) {
                    alias = lfPath;
                }
                listenFolderAliasMap.put(alias, file);
                listenFolderIdMap.put(lfId, file);
                listenFolderIdAliasMap.put(lfId, alias);
                listenFolderPathIdMap.put(file.getAbsolutePath(), lfId);
                log.info("加载监听目录成功：{} => {}", lfId, lfPath);
            } else {
                log.info("加载监听目录失败：{} => {}", lfId, lfPath);
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
