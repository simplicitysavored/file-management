package xyz.yuanjin.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.yuanjin.project.common.enums.ResponseEnum;
import xyz.yuanjin.project.common.util.FileUtil;
import xyz.yuanjin.project.common.util.ResponseUtil;
import xyz.yuanjin.project.common.util.UnitUtils;
import xyz.yuanjin.project.pojo.FolderBean;
import xyz.yuanjin.project.pojo.config.SystemConfig;
import xyz.yuanjin.project.service.FileManagementService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yuanjin
 * @version v1.0 on 2020/4/23 22:21
 */
@Controller
public class HomeController {
    @Resource
    private FileManagementService fileManagementService;

    /**
     * 加载主页
     * 加载根盘(在配置文件中)
     *
     * @param model model
     * @return {String}
     */
    @RequestMapping("/")
    public String home(Model model) {

//        model.addAttribute("listenDrivers", SystemUtil.systemProperty().getNasListenFileList());
        List<String> list = new ArrayList<>();
        SystemConfig.getInstance().getListenFolderList().forEach(path -> list.add(path.getAbsolutePath()));
        model.addAttribute("listenDrivers", list);

        return "home";
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
    String create(
            @RequestParam("position") String position,
            @RequestParam("name") String name
    ) {
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.response(ResponseEnum.PARAMS_ERROR, "参数错误").toString();
        }
        File file = new File(position);
        if (file.exists() && file.isDirectory()) {
            try {
                File newFile = new File(file.getAbsolutePath().concat(File.separator).concat(name));
                boolean success = newFile.mkdirs();
                return success ? ResponseUtil.successString() : ResponseUtil.errorString("创建失败");
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseUtil.errorString(e.getMessage());
            }
        }
        return ResponseUtil.successString();
    }

    /**
     * 删除文件或文件夹
     *
     * @param path 绝对路径
     * @return {ResponseDTO.toString()}
     */
    @PostMapping(value = "/delete", produces = "application/json;charset=utf8")
    public @ResponseBody
    String delete(
            @RequestParam("path") String path
    ) {
        File file = new File(path);

        if (fileManagementService.isProtectFile(file)) {
            return ResponseUtil.errorString("文件受保护，不可删除");
        }


        if (file.exists()) {
            try {
                boolean success = FileUtil.delete(file);
                return success ? ResponseUtil.successString() : ResponseUtil.errorString("删除失败");
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseUtil.errorString(e.getMessage());
            }
        }
        return ResponseUtil.successString();
    }

    /**
     * 跳转到播放到视频页面
     *
     * @param model    model
     * @param filePath 文件绝对路径
     * @return 页面
     */
    @GetMapping(value = "/jumpToVideo")
    public String jumpToVideo(Model model, @RequestParam("filePath") String filePath) {
        model.addAttribute("filePath", filePath);
        return "playVideo";
    }

    /**
     * 返回视频文件流
     *
     * @param response 响应对象
     * @param filePath 视频文件绝对路径
     */
    @GetMapping(value = "/playVideo")
    public @ResponseBody
    void playVideo(HttpServletResponse response, @RequestParam("filePath") String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return;
            }

            FileInputStream inputStream = new FileInputStream(file);
            String fileName = file.getName();
            response.setContentType("video/mp4");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            System.out.println("data.length " + inputStream.available());
            response.setContentLength(inputStream.available());
            response.setHeader("Content-Range", "" + (inputStream.available() - 1));
            response.setHeader("Accept-Ranges", "bytes");
            response.setHeader("Etag", "W/\"9767057-1323779115364\"");
            OutputStream os = response.getOutputStream();

            byte[] bytes = new byte[1024 * 100];
            int length;
            while ((length = inputStream.read(bytes)) != -1) {
                os.write(bytes, 0, length);
            }
            os.flush();

            os.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传文件
     *
     * @param file     文件
     * @param position 存储位置
     * @return {ResponseDTO.toString()}
     * @throws IOException 异常
     */
    @PostMapping(value = "/upload", produces = "application/json;charset=utf8")
    public @ResponseBody
    String upload(@RequestParam("uploadFile") MultipartFile file,
                  @RequestParam("position") String position) throws IOException {

        System.out.println(position);
        System.out.println(file.getOriginalFilename());
        System.out.println(UnitUtils.convertTrafficAuto((long) file.getBytes().length));

        InputStream is = file.getInputStream();

        FileOutputStream fos = new FileOutputStream(new File(position + File.separator + file.getOriginalFilename()));

        byte[] bytes = new byte[1024 * 100];
        int length;
        while ((length = is.read(bytes)) != -1) {
            fos.write(bytes, 0, length);
            fos.flush();
        }

        fos.close();
        is.close();

        System.gc();
        return ResponseUtil.successString();
    }
}
