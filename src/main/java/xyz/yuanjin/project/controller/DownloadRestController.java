package xyz.yuanjin.project.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import xyz.yuanjin.project.pojo.dto.YjFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@RestController
public class DownloadRestController {

    /**
     * 下载文件(不包含文件夹)
     *
     * @param path 绝对路径
     * @return 文件流
     * @throws IOException 异常
     */
    @GetMapping("/download")
    public ResponseEntity<byte[]> download(String path) throws IOException {
        //获取文件信息
        File file = new File(path);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", file.getName());

        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] bytes = new byte[102400];
        int length;
        while ((length = fis.read(bytes)) != -1) {
            bos.write(bytes, 0, length);
        }
        return new ResponseEntity<byte[]>(bos.toByteArray(),
                headers, HttpStatus.OK);
    }

    @GetMapping("/downloadV2/{fileName}")
    public void downloadV2(
            @PathVariable(value = "fileName", required = false) String fileName,
            String path,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        // application/octet-stream
        YjFile file = new YjFile(path);
        if (file.exists()) {
            String userAgent = request.getHeader("User-Agent");
            String formFileName = file.getName();

            // 针对IE或者以IE为内核的浏览器：
            if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
                formFileName = java.net.URLEncoder.encode(formFileName, "UTF-8");
            } else {
                // 非IE浏览器的处理：
                formFileName = new String(formFileName.getBytes("UTF-8"), "ISO-8859-1");
            }

            response.setContentType("application/octet-stream;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;fileName=" + formFileName);

            FileInputStream fis = new FileInputStream(file.getSourceFile());
            byte[] bytes = new byte[1024 * 1024];
            int length;
            while ((length = fis.read(bytes)) != -1) {
                response.getOutputStream().write(bytes, 0, length);
                response.getOutputStream().flush();
                response.flushBuffer();
            }
        } else {
            response.setContentType("text/html");
            response.getWriter().println("文件未找到");
        }
    }
}