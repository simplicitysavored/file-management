package xyz.yuanjin.project.pojo;

import lombok.Data;
import xyz.yuanjin.project.common.util.FileUtil;
import xyz.yuanjin.project.util.SystemUtil;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

/**
 * 基础
 *
 * @author yuanjin
 */
@Data
public class BaseBean {

    private String name;

    private String absolutePath;

    private Date lastModifyDate;

    private boolean folder;

    private boolean canRead;

    private boolean canWrite;

    private boolean hidden;

    /**
     * 上一级文件夹名
     */
    private String previousName;
    /**
     * 上一级
     */
    private String previousUrl;
    /**
     * 是否受保护
     */
    private boolean protect;

    private boolean video;

    public BaseBean() {
    }

    private void initialAuto(File file) {
        this.name = file.getName();
        this.absolutePath = file.getAbsolutePath();
        this.lastModifyDate = new Date(file.lastModified());
        this.folder = file.isDirectory();
        this.video = FileUtil.isVideo(file);
        this.canRead = file.canRead();
        this.canWrite = file.canWrite();
        this.hidden = file.isHidden();
    }

    public static BaseBean initialByFile(Class<? extends BaseBean> clz, File file) throws IllegalAccessException, InstantiationException, UnsupportedEncodingException {
        BaseBean baseBean = clz.newInstance();
        baseBean.initialAuto(file);

        File prev = file.getParentFile();

        if (null != prev) {
            baseBean.setPreviousName(prev.getName());
            baseBean.setPreviousUrl("/nas?path=" + URLEncoder.encode(prev.getAbsolutePath(), "UTF-8"));
            baseBean.setProtect(SystemUtil.isProtectFile(file));
        }

        return baseBean;
    }


}
