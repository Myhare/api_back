package com.ming.web.strategy.impl;

import com.ming.apiCommon.model.enums.ResultCodeEnum;
import com.ming.web.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;

/**
 * 本地上传文件
 */
@Service("localUploadStrategyImpl")
public class LocalUploadStrategyImpl extends AbstractUploadStrategyImpl{

    // 获取文件上传路径
    @Value("${upload.local.path}")
    private String localPath;
    // 获取文件获取的nginx映射路径
    @Value("${upload.local.url}")
    private String getFileUrl;

    // 执行上传文件
    @Override
    void upload(String path, String fileName, InputStream inputStream) throws IOException {
        // 如果目录不存在，创建目录
        File directory = new File(localPath + path);
        if (!directory.exists()){
            if (!directory.mkdirs()) {
                throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR, "创建目录失败");
            }
        }
        // 上传文件
        File file = new File(localPath + path + fileName);
        // 如果文件不存在就创建一个新的文件
        if (file.createNewFile()){
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(file.toPath()));
            try {
                byte[] bytes = new byte[1024];
                int length;
                while ((length = bis.read(bytes)) != -1) {
                    bos.write(bytes, 0, length);
                }
            } finally {
                inputStream.close();
                bos.close();
                bis.close();
            }
        }
    }

    @Override
    String getFileAccessUrl(String relativePath) {
        return getFileUrl + relativePath;
    }

    @Override
    boolean isExist(String relativePath) {
        return new File(localPath + relativePath).exists();
    }
}
