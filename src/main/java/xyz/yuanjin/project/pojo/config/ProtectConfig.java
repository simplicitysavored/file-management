package xyz.yuanjin.project.pojo.config;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 受保护的文件规则配置
 *
 * @author yuanjin
 */
@Data
public class ProtectConfig {
    /**
     * 文件路径
     */
    private List<String> filePath;

    private List<Pattern> filePathRegexList;

    public ProtectConfig() {
        filePath = new ArrayList<>();
        filePathRegexList = new ArrayList<>();
    }
}
