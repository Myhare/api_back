package com.ming.openApiClientSdk.utils;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

/**
 * 签名工具
 */
public class SignUtils {
    /**
     * 生成签名(通过请求体和用户的sk创建唯一的一个标识)
     * @param body  请求体
     * @param secretKey 用户sk
     * @return  签名结果
     */
    public static String genSign(String body, String secretKey) {
        Digester md5 = new Digester(DigestAlgorithm.SHA256);
        String content = body + "." + secretKey;
        return md5.digestHex(content);
    }

    /**
     * 加密一个字符串
     */
    public static String encryptStr(String body) {
        return new Digester(DigestAlgorithm.SHA256).digestHex(body);
    }
}
