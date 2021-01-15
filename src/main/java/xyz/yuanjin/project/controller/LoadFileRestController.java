package xyz.yuanjin.project.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.web.bind.annotation.*;
import xyz.yuanjin.project.pojo.FolderBean;
import xyz.yuanjin.project.service.FileManagementService;
import xyz.yuanjin.project.util.SystemUtil;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yuanjin
 */
@Deprecated
@RestController
public class LoadFileRestController {
    @Resource
    private FileManagementService fileManagementService;
/*
    *//**
     * 暂时没有用到
     *
     * @param path 文件绝对路径
     * @return JsonString of List<FolderBean>
     * @throws Exception 异常
     *//*
    @GetMapping(value = "/file", produces = "application/json;charset=utf8")
    public String file(@RequestParam(value = "path", required = false) String path) throws Exception {
        if (null != path) {
            File file = new File(path);
            if (!file.isDirectory()) {
                throw new Exception("这个不是文件夹");
            }

            FolderBean folderBean = fileManagementService.loadFolder(file);

            return JSON.toJSONString(folderBean);
        }


        String[] listenFilePathList = SystemUtil.systemProperty().getNasListenFileList();

        List<FolderBean> listenDrivers = new ArrayList<>();

        for (String rootPath : listenFilePathList) {
            File file = new File(rootPath);

            if (!file.isDirectory()) {
                throw new Exception("这个不是文件夹");
            }

            FolderBean folderBean = fileManagementService.loadFolder(file);

            listenDrivers.add(folderBean);
        }

        return JSON.toJSONString(listenDrivers);
    }*/

}
