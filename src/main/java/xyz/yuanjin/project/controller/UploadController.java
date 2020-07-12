package xyz.yuanjin.project.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import xyz.yuanjin.project.common.util.ResponseUtil;
import xyz.yuanjin.project.common.util.UnitUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author yuanjin
 */
@RestController
public class UploadController {

    @RequestMapping("/uploadBySplit")
    public String uploadBySplit(@RequestParam("uploadFile") MultipartFile file,
                                @RequestParam(value = "position", defaultValue = "F:\\TMP") String position,
                                @RequestParam(value = "fileName", defaultValue = "test.mp4") String fileName) {

        try {
            InputStream is = file.getInputStream();

            File destFile = new File(position + File.separator + fileName);
            FileOutputStream fos = new FileOutputStream(destFile, destFile.exists());

            byte[] bytes = new byte[1024 * 100];
            int length;
            while ((length = is.read(bytes)) != -1) {
                fos.write(bytes, 0, length);
                fos.flush();
            }

            fos.close();
            is.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseUtil.successString();
    }
}
