package xyz.yuanjin.project.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import xyz.yuanjin.project.common.dto.ResponseDTO;
import xyz.yuanjin.project.common.enums.ResponseEnum;
import xyz.yuanjin.project.common.util.ArrayUtils;
import xyz.yuanjin.project.common.util.FileUtil;
import xyz.yuanjin.project.common.util.ResponseUtil;
import xyz.yuanjin.project.pojo.FolderBean;
import xyz.yuanjin.project.pojo.PageParam;
import xyz.yuanjin.project.pojo.config.SystemConfig;
import xyz.yuanjin.project.pojo.dto.DropdownDTO;
import xyz.yuanjin.project.pojo.dto.TableQueryDTO;
import xyz.yuanjin.project.pojo.dto.YjFile;
import xyz.yuanjin.project.service.FileManagementService;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/table")
public class TableHomeController {
    @Resource
    private FileManagementService fileManagementService;

    /**
     * 页面加载
     *
     * @return 页面
     */
    @RequestMapping("")
    public String table() {
        return "/table";
    }

    @PostMapping("/getListenList")
    public @ResponseBody
    ResponseDTO getListenList() {
        List<DropdownDTO> dto = fileManagementService.getListenFolderDTO();

        return ResponseUtil
                .success()
                .setData(dto);
    }

    /**
     * 查询列表数据
     *
     * @param pageParam 查询参数
     * @return {ResponseDTO}
     */
    @RequestMapping(value = "/list", produces = "application/json;charset=utf8")
    public @ResponseBody
    ResponseDTO listV2(@RequestBody PageParam<TableQueryDTO> pageParam) {


        FolderBean folderBean = null;
        try {
            YjFile file = fileManagementService.checkFilePathV2(pageParam.getQueryObj().getPath());
            folderBean = fileManagementService.loadFolder(file, pageParam.getQueryObj().getDotBack());

            return ResponseUtil
                    .response(ResponseEnum.SUCCESS)
                    .setData(folderBean.getFiles())
                    .setExtendInfo(folderBean.getAbsolutePath());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseUtil
                    .response(ResponseEnum.PARAMS_ERROR)
                    .setMessage(e.getMessage())
                    .setData(new ArrayList<>());
        }

    }


    /**
     * 创建文件夹
     *
     * @param position 所在位置(绝对路径)
     * @param name     文件夹名
     * @return {ResponseDTO.toString()}
     */
    @PostMapping(value = "/create", produces = "application/json;charset=utf8")
    public @ResponseBody
    ResponseDTO create(
            @RequestParam("position") String position,
            @RequestParam("name") String name
    ) {
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.error("文件夹名成不能为空");
        }
        YjFile positionFile = new YjFile(position);
        if (!(positionFile.exists() && positionFile.isDirectory())) {
            return ResponseUtil.error("位置错误");
        }
        try {
            YjFile newFile = new YjFile(positionFile.getAbsolutePath().concat(File.separator).concat(name));
            if (newFile.exists() && newFile.isDirectory()) {
                return ResponseUtil.error("文件夹已存在");
            }
            boolean success = newFile.mkdirs();
            return success ? ResponseUtil.success() : ResponseUtil.error("新建文件夹失败");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.error("新建文件夹异常");
        }
    }

    /**
     * 删除文件夹或文件
     *
     * @return {ResponseDTO}
     */
    @PostMapping("/delete")
    public @ResponseBody
    ResponseDTO delete(@RequestParam(value = "pathList[]", defaultValue = "") String[] pathArr) {
        List<YjFile> yjFileList = new ArrayList<>();
        for (String path : pathArr) {
            YjFile file = new YjFile(path);

            if (file.exists()) {
                if (file.isListenRoot()) {
                    return ResponseUtil.error("监听跟路径，不可删除");
                }

                if (fileManagementService.isProtectFile(file)) {
                    return ResponseUtil.error("文件受保护，不可删除");
                }
                yjFileList.add(file);
            } else {
                return ResponseUtil.error("文件未找到，不可删除[" + path + "]");
            }
        }
        for (YjFile file : yjFileList) {

            if (file.exists()) {
                try {
                    boolean success = FileUtil.delete(file.getSourceFile());
                    if (!success) {
                        return ResponseUtil.error("删除失败[" + file.getPath() + "]");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return ResponseUtil.error(e.getMessage());
                }
            }

        }

        return ResponseUtil.success();
    }
}
