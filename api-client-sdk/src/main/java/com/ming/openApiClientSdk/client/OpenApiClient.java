package com.ming.openApiClientSdk.client;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.ming.openApiClientSdk.model.User;

import java.util.HashMap;
import java.util.Map;

import static com.ming.openApiClientSdk.utils.SignUtils.encryptStr;
import static com.ming.openApiClientSdk.utils.SignUtils.genSign;
import static com.ming.openApiClientSdk.constant.RequestMethodConstant.*;

public class OpenApiClient {

    /**
     * 网关地址
     */
    private static final String GATEWAY_HOST = "http://localhost:8000/api/interface";

    private String accessKey;

    private String secretKey;

    public OpenApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String getUsernameByPost(User user, String method) {
        String userJson = JSONUtil.toJsonStr(user);
        HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST + "/user/name")
                .addHeaders(getHeaderMap(userJson, method))
                .body(userJson)
                .execute();
        if (httpResponse.getStatus() != 200){
            throw new RuntimeException("请求接口失败");
        }
        System.out.println(httpResponse.getStatus());
        String result = httpResponse.body();
        System.out.println(result);
        return result;
    }

    /**
     * get请求调用api接口
     * 这里的params直接放到参数后面
     */
    public HttpResponse invokeGetInterface(String url, String params, String method){
        // get请求拼接字符串
        String uri = GATEWAY_HOST + url + "?" + params;
        // System.out.println(s);
        HttpResponse response = HttpRequest.get(uri)
                .header("Accept-Charset", CharsetUtil.UTF_8) // 防止中文乱码
                .addHeaders(getHeaderMap(params, method))
                .execute();
        return response;
    }

    /**
     * post请求调用api接口
     * 这里直接使用post请求请求网关接口
     */
    public HttpResponse invokePostInterface(String url, String params, String method){
        HttpResponse response = HttpRequest.post(GATEWAY_HOST + url)
                .header("Accept-Charset", CharsetUtil.UTF_8) // 防止中文乱码
                .addHeaders(getHeaderMap(params, method))
                .body(params)
                .execute();
        return response;
    }

    public HttpResponse invokeInterface(String url, String params, String method){
        switch (method){
            case GET:
                return invokeGetInterface(url, params, method);
            case POST:
            case DELETE:
                return invokePostInterface(url, params, method);
            default:
                // 其他情况直接返回空报错
                return null;
        }
    }

    /**
     * 获取请求头map，用于网关鉴权
     */
    private Map<String, String> getHeaderMap(String body, String method) {
        String encryptBody = encryptStr(body);
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("accessKey", accessKey);
        // 一定不能直接发送
        // hashMap.put("secretKey", secretKey);
        hashMap.put("nonce", RandomUtil.randomNumbers(6));  // 随机数防止请求重放，随机数只能用一次
        hashMap.put("body", encryptBody);  // 传递请求体，用来加密使用
        hashMap.put("method", method);  // 传递请求体，用来加密使用
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        // 通过请求体和sk进行签名，防止其他人恶意请求接口
        hashMap.put("sign", genSign(encryptBody, secretKey));
        return hashMap;
    }

}
