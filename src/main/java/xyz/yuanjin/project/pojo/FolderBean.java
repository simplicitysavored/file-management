package xyz.yuanjin.project.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.util.Assert;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author yuanjin
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FolderBean extends BaseBean {
    private List<BaseBean> files;

    private Integer fileTotal;

    /**
     * 下一级
     */
    private String nextUrl;

    public static FolderBean initialByFile(File file) throws InstantiationException, IllegalAccessException, UnsupportedEncodingException {
        Assert.isTrue(file.isDirectory(), "这不是一个文件夹：" + file.getAbsolutePath());
        FolderBean bean = (FolderBean) BaseBean.initialByFile(FolderBean.class, file);
        bean.setNextUrl("/file?path="+ URLEncoder.encode(file.getAbsolutePath(), "UTF-8"));
        return bean;
    }

    public Integer getFileTotal() {
        return null == files ? 0 : files.size();
    }
}
