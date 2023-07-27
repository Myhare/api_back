package com.ming.openApiClientSdk;

import com.ming.openApiClientSdk.client.OpenApiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Configuration
@ConfigurationProperties("openapi.client")
@Data
@ComponentScan
public class OpenApiClientConfig {

    private String accessKey;

    private String secretKey;

    @Bean
    public OpenApiClient openApiClient(){
        return new OpenApiClient(accessKey, secretKey);
    }


    /**
     * 通过完成直接通过名称测试调用方法
     * @return
     */
    public Object invokeMethod(String methodName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<OpenApiClient> clazz = OpenApiClient.class;
        Method method = clazz.getMethod(methodName, String.class);
        Object invoke = method.invoke(openApiClient(), "测试");
        return invoke;
    }

}
