package com.ming.web.strategy.context;

import com.ming.web.model.enums.UploadFileModelEnum;
import com.ming.web.strategy.UploadStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 上传文件上下文
 */
@Service
public class UploadStrategyContext {

    @Value("${upload.mode}")
    private String mode;

    @Resource
    private Map<String, UploadStrategy> uploadStrategyMap;

    public String executeUploadStrategyMap(MultipartFile multipartFile, String relativePath){
        return uploadStrategyMap.get(UploadFileModelEnum.getStrategy(mode)).uploadFile(multipartFile, relativePath);
    }

}
