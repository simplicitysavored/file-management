package xyz.yuanjin.project.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import xyz.yuanjin.project.common.dto.ResponseDTO;
import xyz.yuanjin.project.common.util.FileUtil;
import xyz.yuanjin.project.common.util.ResponseUtil;
import xyz.yuanjin.project.common.util.UnitUtils;
import xyz.yuanjin.project.pojo.dto.YjFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author yuanjin
 */
@Slf4j
@Controller
public class UploadController {
    private static final String SUFFIX_DOWNLOADING = ".tmpFileDownloading";

    /**
     * 分片上传(单个文件)
     *
     * @param file     文件
     * @param position 存储位置(绝对路径)
     * @param fileName 文件名
     * @return ResponseUtil.successString()
     */
    @RequestMapping(value = "/uploadBySplit", produces = "application/json;charset=utf8")
    public @ResponseBody
    String uploadBySplit(@RequestParam("uploadFile") MultipartFile file,
                         @RequestParam(value = "position", defaultValue = "/Users/yuanjin/Downloads/tmp") String position,
                         @RequestParam(value = "fileName", defaultValue = "tmpFile") String fileName,
                         @RequestParam(value = "isLastSnippet", defaultValue = "false") Boolean isLastSnippet,
                         @RequestParam(value = "skip", defaultValue = "1") Integer skip) {

        try {
            InputStream is = file.getInputStream();

            YjFile destFile = new YjFile(position + File.separator + fileName);
            if (destFile.exists()) {
                return ResponseUtil.error("已存在同名的文件").toString();
            }

            destFile = new YjFile(destFile.getAbsolutePath() + SUFFIX_DOWNLOADING);
//            if (skip == 1) {
//                if (destFile.exists()) {
//                    return ResponseUtil.error("文件已存在：" + destFile.getSourceFile().getAbsolutePath()).toString();
//                }
//            }

            FileOutputStream fos = new FileOutputStream(destFile.getSourceFile(), destFile.exists());

            byte[] bytes = new byte[1024 * 100];
            int length;
            while ((length = is.read(bytes)) != -1) {
                fos.write(bytes, 0, length);
                fos.flush();
            }

            fos.close();
            is.close();


            if (isLastSnippet) {
                log.info("正在保存最后一个片段,保存到：{}", destFile.getSourceFile().getAbsolutePath().substring(0, destFile.getSourceFile().getAbsolutePath().indexOf(SUFFIX_DOWNLOADING)));
                FileUtil.rename(destFile.getSourceFile(), new File(destFile.getSourceFile().getAbsolutePath().substring(0, destFile.getSourceFile().getAbsolutePath().indexOf(SUFFIX_DOWNLOADING))));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.error(e.getMessage()).toString();
        }
        return ResponseUtil.successString();
    }

    @RequestMapping("/uploadMultiFile")
    public @ResponseBody
    ResponseDTO uploadMultiFile(@RequestParam("uploadFile") MultipartFile file) {

        return ResponseUtil.success();
    }
}
