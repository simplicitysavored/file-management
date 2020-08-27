package xyz.yuanjin.project.service;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import xyz.yuanjin.project.pojo.BaseBean;
import xyz.yuanjin.project.pojo.FileBean;
import xyz.yuanjin.project.pojo.FolderBean;
import xyz.yuanjin.project.pojo.config.SystemConfig;
import xyz.yuanjin.project.pojo.dto.DropdownDTO;
import xyz.yuanjin.project.util.SystemUtil;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author yuanjin
 */
@Service
public class FileManagementService {

    public File newFile(String path) {
        return new File(SystemUtil.systemProperty().getNasListenFileList()[0].concat(path));
    }

    public FolderBean loadFolder(File file) throws Exception {
        return loadFolder(file, false);
    }

    /**
     * @param file    文件夹
     * @param dotBack 是否使用 .. 返回上一层
     * @return {FolderBean}
     * @throws Exception 异常
     */
    public FolderBean loadFolder(File file, Boolean dotBack) throws Exception {
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

        // 按照名称升序
        files.sort(Comparator.comparing(BaseBean::getName));

        // 按照类型排序
        files.sort((o1, o2) -> {
            int int1 = o1.isFolder() ? 0 : 1;
            int int2 = o2.isFolder() ? 0 : 1;
            return int1 - int2;
        });

        if (dotBack) {
            List<BaseBean> newFiles = new ArrayList<>();
            if (!CollectionUtils.isEmpty(files)) {

                BaseBean dotBackBean = BaseBean.newBaseBeanOfDotBack(files.get(0));
                if (null != dotBackBean.getAbsolutePath()) {
                    newFiles.add(dotBackBean);
                }

            } else {
                BaseBean dotBackBean = BaseBean.newBaseBeanOfDotBack(file);
                if (null != dotBackBean.getAbsolutePath()) {
                    newFiles.add(dotBackBean);
                }
            }

            newFiles.addAll(files);
            folderBean.setFiles(newFiles);
        } else {
            folderBean.setFiles(files);
        }


        return folderBean;
    }

    public boolean isProtectFile(File file) {
        return SystemUtil.isProtectFile(file);
    }

    public File checkFilePath(String path) throws Exception {
        File file = new File(path);
        if (!file.isDirectory()) {
            throw new Exception("这个不是文件夹");
        }
        return file;
    }

    public List<DropdownDTO> getListenFolderDTO() {
        List<DropdownDTO> dtoList = new ArrayList<>();
//        SystemConfig.getInstance().getListenFolderList().forEach(file -> dtoList.add(DropdownDTO.loadFile(file)));

        SystemConfig.getInstance().getListenFolderMap().forEach((alias, file)-> dtoList.add(new DropdownDTO(file.getAbsolutePath(), alias)));
        return dtoList;
    }
}
