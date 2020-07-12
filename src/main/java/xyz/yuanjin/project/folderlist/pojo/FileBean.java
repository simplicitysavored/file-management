package xyz.yuanjin.project.folderlist.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.util.Assert;
import xyz.yuanjin.project.common.util.UnitUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * @author yuanjin
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FileBean extends BaseBean {
    private Long byteSize;

    private String byteSizeDesc;

    private String suffix;

    public static FileBean initialByFile(File file) throws InstantiationException, IllegalAccessException, UnsupportedEncodingException {
        Assert.isTrue(file.isFile(), "这不是一个文件：" + file.getAbsolutePath());
        FileBean bean = (FileBean) BaseBean.initialByFile(FileBean.class, file);
        bean.setByteSize(file.length());
        bean.setByteSizeDesc(UnitUtils.convertTrafficAuto(file.length()));
        if (file.getName().contains(".")) {
            bean.setSuffix(file.getName().substring(file.getName().lastIndexOf(".")));
        }
        return bean;
    }
}
