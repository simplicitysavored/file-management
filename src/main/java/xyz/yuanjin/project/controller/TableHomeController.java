package xyz.yuanjin.project.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import xyz.yuanjin.project.common.dto.ResponseDTO;
import xyz.yuanjin.project.common.enums.ResponseEnum;
import xyz.yuanjin.project.common.util.ResponseUtil;
import xyz.yuanjin.project.pojo.FolderBean;
import xyz.yuanjin.project.pojo.PageParam;
import xyz.yuanjin.project.pojo.dto.TableQueryDTO;
import xyz.yuanjin.project.service.FileManagementService;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;

@Slf4j
@Controller
public class TableHomeController {
    @Resource
    private FileManagementService fileManagementService;

    @GetMapping("/table")
    public String table() {
        return "/table";
    }

    @RequestMapping(value = "/table/list")
    public @ResponseBody
    ResponseDTO list(@RequestBody PageParam<String> pageParam) throws Exception {

        File file = fileManagementService.checkFilePath(pageParam.getQueryObj());

        FolderBean folderBean = fileManagementService.loadFolder(file);

        return ResponseUtil
                .response(ResponseEnum.SUCCESS)
                .setData(folderBean);
    }

    @RequestMapping(value = "/table/listV2", produces = "application/json;charset=utf8")
    public @ResponseBody
    ResponseDTO listV2(@RequestBody PageParam<TableQueryDTO> pageParam) throws Exception {


        FolderBean folderBean = null;
        try {
            File file = fileManagementService.checkFilePath(pageParam.getQueryObj().getPath());
            folderBean = fileManagementService.loadFolder(file, pageParam.getQueryObj().getDotBack());

            return ResponseUtil
                    .response(ResponseEnum.SUCCESS)
                    .setData(folderBean.getFiles());
        } catch (Exception e) {
            //log.error(e.getMessage(), e);
            return ResponseUtil
                    .response(ResponseEnum.PARAMS_ERROR)
                    .setData(new ArrayList<>());
        }

    }
}
