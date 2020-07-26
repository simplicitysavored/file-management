package xyz.yuanjin.project.pojo;

import lombok.Data;
import org.springframework.beans.BeanUtils;
import xyz.yuanjin.project.common.util.FileUtil;
import xyz.yuanjin.project.util.SystemUtil;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 基础
 *
 * @author yuanjin
 */
@Data
public class BaseBean {

    private String name;

    /**
     * 当前所在文件夹的绝对路径
     */
    private String absolutePath;

    /**
     * 当前所在文件夹的绝对路径
     * URL Encode
     */
    private String absolutePathEncode;

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
     * 父级所在文件夹的绝对路径
     */
    private String preAbsolutePath;

    /**
     * 父级所在文件夹的绝对路径
     * URL Encode
     */
    private String preAbsolutePathEncode;
    /**
     * 上一级 URL
     */
    private String previousUrl;
    /**
     * 是否受保护
     */
    private boolean protect;

    private boolean video;

    public BaseBean() {
    }

    private void initialAuto(File file) throws UnsupportedEncodingException {
        this.name = file.getName();
        this.absolutePath = file.getAbsolutePath();
        this.absolutePathEncode = URLEncoder.encode(file.getAbsolutePath(), StandardCharsets.UTF_8.name());
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
            File prevParent = prev.getParentFile();
            if (null != prevParent) {
                baseBean.setPreAbsolutePath(prevParent.getAbsolutePath());
                baseBean.setPreAbsolutePathEncode(URLEncoder.encode(prevParent.getAbsolutePath(), StandardCharsets.UTF_8.name()));
            }

            baseBean.setPreviousUrl("/nas?path=" + URLEncoder.encode(prev.getAbsolutePath(), StandardCharsets.UTF_8.name()));
            baseBean.setProtect(SystemUtil.isProtectFile(file));
        }

        return baseBean;
    }

    public static BaseBean newBaseBeanOfDotBack(BaseBean file) {
        BaseBean baseBean = new BaseBean();

        baseBean.setName("..");
        baseBean.setAbsolutePath(file.getPreAbsolutePath());
        baseBean.setAbsolutePathEncode(file.getPreAbsolutePathEncode());
        baseBean.setFolder(true);
        baseBean.setHidden(null == file.getPreAbsolutePath());

        return baseBean;
    }


    public static BaseBean newBaseBeanOfDotBack(File file) throws UnsupportedEncodingException {
        BaseBean baseBean = new BaseBean();
        baseBean.initialAuto(file);

        File parent = file.getParentFile();

        if (null != parent) {
            baseBean.setName("..");
            baseBean.setAbsolutePath(parent.getAbsolutePath());
            baseBean.setAbsolutePathEncode(URLEncoder.encode(parent.getAbsolutePath(), StandardCharsets.UTF_8.name()));
            baseBean.setFolder(true);
            baseBean.setHidden(false);
        } else {
            baseBean.setHidden(true);
        }
        return baseBean;

    }

}
