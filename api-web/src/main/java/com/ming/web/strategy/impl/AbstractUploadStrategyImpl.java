package com.ming.web.strategy.impl;

import com.ming.apiCommon.model.enums.ResultCodeEnum;
import com.ming.web.exception.BusinessException;
import com.ming.web.strategy.UploadStrategy;
import com.ming.web.utils.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * 抽象上传文件模板
 */
public abstract class AbstractUploadStrategyImpl implements UploadStrategy {

    // 上传文件
    @Override
    public String uploadFile(MultipartFile multipartFile, String relativePath) {
        if (Objects.isNull(multipartFile)){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "文件为空");
        }
        // 上传文件
        try {
            // 获取唯一的文件名
            String md5 = FileUtils.getMd5(multipartFile.getInputStream());
            // 获取原文件后缀
            String fileExpand = FileUtils.getFileExpand(multipartFile.getOriginalFilename());
            // 拼接新的文件名
            String fileName = md5 + fileExpand;
            // 判断文件是否上传
            if (!isExist(relativePath + fileName)){
                // 第一次上传，直接上传文件
                upload(relativePath, fileName, multipartFile.getInputStream());
            }
            // 获取文件访问路径
            return getFileAccessUrl(relativePath + fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 上传文件实际操作
     * @param path      上传相对路径
     * @param fileName  文件名
     * @param inputStream 文件输入流
     */
    abstract void upload(String path, String fileName, InputStream inputStream) throws IOException;

    /**
     * 获取文件访问路径
     * @param relativePath 相对路径，这里的相对路径包括文件名
     * @return             访问路径
     */
    abstract String getFileAccessUrl(String relativePath);

    /**
     * 判断文件是否已经上传过了
     * @param relativePath 文件相对路径，包括文件名
     * @return             是否已上传
     */
    abstract boolean isExist(String relativePath);
}
