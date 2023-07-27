package com.ming.web.strategy;

import org.springframework.web.multipart.MultipartFile;

/**
 * 上传文件接口
 */
public interface UploadStrategy {

    /**
     * 上传文件
     * @param multipartFile 要上传的文件
     * @param relativePath  上传文件的相对路径
     * @return              获取文件的路径
     */
    String uploadFile(MultipartFile multipartFile, String relativePath);

}
