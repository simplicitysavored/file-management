package xyz.yuanjin.project.pojo.dto;

import lombok.Data;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class TableQueryDTO {
    /**
     * 是否使用 .. 回退到上一级
     */
    private Boolean dotBack;
    /**
     * 绝对路径
     */
    private String path;

    public Boolean getDotBack() {
        return null == dotBack ? false : dotBack;
    }

    public String getPath() throws UnsupportedEncodingException {
        return URLDecoder.decode(path, StandardCharsets.UTF_8.name());
    }

    public void setDotBack(Boolean dotBack) {
        this.dotBack = dotBack;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
