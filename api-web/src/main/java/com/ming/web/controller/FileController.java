package com.ming.web.controller;

import cn.hutool.http.server.HttpServerResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${web.sdkPath}")
    private String path;

    /**
     * 下载SDK
     */
    @GetMapping("/downloadSdk")
    public void downloadSdkFile(HttpServletResponse response) throws IOException {

        InputStream inputStream = new FileInputStream(path);// 文件的存放路径
        try {
            // 读到流中
            response.reset();
            response.setContentType("application/octet-stream");
            String filename = new File(path).getName();
            response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));
            ServletOutputStream outputStream = response.getOutputStream();
            byte[] b = new byte[1024];
            int len;
            //从输入流中读取一定数量的字节，并将其存储在缓冲区字节数组中，读到末尾返回-1
            while ((len = inputStream.read(b)) > 0) {
                outputStream.write(b, 0, len);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            inputStream.close();
        }
    }

}
