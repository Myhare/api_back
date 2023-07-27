package com.ming.web.utils;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.security.MessageDigest;

/**
 * 文件上传工具
 */
@Component
public class FileUtils {

    /**
     * 获取文件的md5值
     * @param inputStream 文件输入流
     * @return            md5值
     */
    public static String getMd5(InputStream inputStream){
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            byte[] buffer = new byte[8192];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                md5.update(buffer, 0, length);
            }
            return new String(Hex.encode(md5.digest()));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            try {
                if (inputStream!=null){
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取文件拓展名
     * @param fileName 文件名称
     * @return          拓展名
     */
    public static String getFileExpand(String fileName){
        if (StringUtils.isBlank(fileName)){
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }


}
