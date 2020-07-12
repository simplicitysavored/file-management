package xyz.yuanjin.project.folderlist.service;

import org.springframework.stereotype.Service;
import xyz.yuanjin.project.folderlist.pojo.BaseBean;
import xyz.yuanjin.project.folderlist.pojo.FileBean;
import xyz.yuanjin.project.folderlist.pojo.FolderBean;
import xyz.yuanjin.project.folderlist.property.NasProperty;
import xyz.yuanjin.project.folderlist.util.NasUtil;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author yuanjin
 */
@Service
public class NasService {

    public File newFile(String path) {
        return new File(NasUtil.systemProperty().getNasListenFileList()[0].concat(path));
    }

    public FolderBean loadFolder(File file) throws Exception {
        if (!file.exists()) {
            throw new Exception("文件不存在");
        }

        FolderBean folderBean = FolderBean.initialByFile(file);

        List<BaseBean> files = new ArrayList<>();

        File[] tmpFiles = file.listFiles();

        if (null != tmpFiles) {

            for (File tmpFile : tmpFiles) {
                files.add(tmpFile.isDirectory() ? FolderBean.initialByFile(tmpFile) : FileBean.initialByFile(tmpFile));
            }
        }

        folderBean.setFiles(files);
        return folderBean;
    }

    public boolean isProtectFile(File file) {
        return NasUtil.isProtectFile(file);
    }
}
