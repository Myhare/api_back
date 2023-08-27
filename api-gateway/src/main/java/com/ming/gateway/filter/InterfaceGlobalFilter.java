package com.ming.gateway.filter;

import com.ming.apiCommon.constant.RedisPrefixConst;
import com.ming.apiCommon.constant.RequestHeaderConstant;
import com.ming.apiCommon.dubbo.InnerInterfaceInfoService;
import com.ming.apiCommon.dubbo.InnerUserInterfaceService;
import com.ming.apiCommon.dubbo.InnerUserService;
import com.ming.apiCommon.dubbo.RedisService;
import com.ming.apiCommon.model.entity.InterfaceInfo;
import com.ming.apiCommon.model.entity.User;
import com.ming.apiCommon.model.entity.UserInterfaceInfo;
import com.ming.openApiClientSdk.utils.SignUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 接口全局过滤器
 */
@Log4j2
@Component
public class InterfaceGlobalFilter implements GlobalFilter, Ordered {

    /**
     * 网关前缀
     */
    private String prefixStr = "/api/interface/";


    @DubboReference
    private InnerUserInterfaceService innerUserInterfaceService;

    @DubboReference
    private InnerUserService innerUserService;

    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @DubboReference
    private RedisService redisService;

    /**
     * 白名单临时测试列表
     */
    // private static final List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // 判断接口是否存在，还有请求方式是否匹配
        String path = request.getPath().value();
        // 判断是不是调用接口请求，如果不是，直接放行交给后面的过滤器
        if (!path.contains(prefixStr)){
            return chain.filter(exchange);
        }
        // 清除接口的前面的/api/interface
        path = path.substring(prefixStr.length() - 1);

        ServerHttpResponse response = exchange.getResponse();
        // 获取请求方法
        String method = request.getMethod().toString();
        log.info("请求唯一标识：" + request.getId());
        log.info("请求路径：" + request.getPath());
        log.info("请求方法：" + method);
        log.info("请求参数：" + request.getQueryParams());
        String sourceAddress = request.getLocalAddress().getHostString();
        log.info("请求来源地址：" + sourceAddress);
        log.info("请求来源地址：" + request.getRemoteAddress());
        // TODO 白名单用户鉴权 改成黑名单拒绝访问
        // if (!IP_WHITE_LIST.contains(sourceAddress)){
        //     return handleNoAuth(response, "");
        // }
        // 角色鉴权
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst(RequestHeaderConstant.ACCESS_KEY);
        // 获取随机数防重放
        String nonce = headers.getFirst(RequestHeaderConstant.NONCE);
        Boolean nonceSuccess = redisService.setIfAbsent(nonce, "1", 3, TimeUnit.MINUTES);
        if (!nonceSuccess){
            log.error("有重放请求");
            return handleNoAuth(response, "请求重复");
        }
        // 通过ak获取用户
        // 从数据库中查询sk，鉴权使用
        User user = null;
        try {
            // dubbo远程获取当前登录的用户
            user = innerUserService.getUserByAk(accessKey);
        } catch (Exception e) {
            log.error("dubbo--getUserByAk--错误",e);
        }
        if (user == null){
            log.error("用户鉴权失败,无当前用户");
            return handleNoAuth(response, "无当前用户");
        }
        // 用户校验
        String oldSign = headers.getFirst(RequestHeaderConstant.SIGN);
        String body = headers.getFirst(RequestHeaderConstant.BODY); // 这里获取请求头中存取的请求体数据，用来加密和原来的校验
        // 获取用户sk
        String secretKey = user.getSecretKey();
        // 重新进行加密，用来权限校验
        // 为了防止被截取sk不能经过网络传输，通过传入的body和查询的sk重新生成签名进行比对
        String getSign = SignUtils.genSign(body, secretKey);
        if (!getSign.equals(oldSign)){
            return handleNoAuth(response, "密钥鉴权失败");
        }


        // 时间戳防止重放
        String timestamp = headers.getFirst(RequestHeaderConstant.TIMESTAMP);
        // 防止消息重放,发送时间和当前时间不能超过2分钟
        long currentTime = System.currentTimeMillis() / 1000;
        final Long TWO_TIME = 60 * 2L;
        if (currentTime - Long.parseLong(timestamp) >= TWO_TIME){
            return handleNoAuth(response, "请求超时");
        }

        // 通过请求方法获取请求接口实体
        InterfaceInfo interfaceInfo = null;
        try {
            interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(path, method);
        } catch (Exception e) {
            log.error("dubbo-getInterfaceInfo-错误",e);
        }
        if (interfaceInfo == null){
            return handleNoAuth(response, "接口不存在");
        }
        // 判断用户是否还有这个接口的调用次数
        UserInterfaceInfo userInterface = innerUserInterfaceService.getUserInterface(user.getId(), interfaceInfo.getId());
        if (userInterface == null || userInterface.getLeftNum() <= 0){
            return handleNoAuth(response, "接口调用次数不足");
        }

        String forwardedPath = exchange.getRequest().getURI().toString();
        System.out.println("转发后的完整路径：" + forwardedPath);

        // 放行
        return handleResponse(exchange, chain, interfaceInfo.getId(), user.getId());
    }

    @Override
    public int getOrder() {
        // 最先执行的过滤器
        return -1;
    }


    /**
     * 处理响应
     *
     * @param exchange
     * @param chain
     * @return
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceInfoId, long userId) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 缓存数据的工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 拿到响应码
            HttpStatus statusCode = originalResponse.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                // 装饰，增强能力
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    // 等调用完转发的接口后才会执行
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            // 往返回值里写数据
                            // 拼接字符串
                            return super.writeWith(
                                    fluxBody.map(dataBuffer -> {
                                        // 7. 调用成功，接口调用次数 + 1 invokeCount
                                        try {
                                            innerUserInterfaceService.invokeCount(interfaceInfoId, userId);
                                        } catch (Exception e) {
                                            log.error("invokeCount error", e);
                                        }
                                        // 更新统计信息
                                        updateInvokeCount();
                                        byte[] content = new byte[dataBuffer.readableByteCount()];
                                        dataBuffer.read(content);
                                        DataBufferUtils.release(dataBuffer);//释放掉内存
                                        // 构建日志
                                        StringBuilder sb2 = new StringBuilder(200);
                                        List<Object> rspArgs = new ArrayList<>();
                                        rspArgs.add(originalResponse.getStatusCode());
                                        String data = new String(content, StandardCharsets.UTF_8); //data
                                        sb2.append(data);
                                        // 打印日志
                                        log.info("响应结果：" + data);
                                        return bufferFactory.wrap(content);
                                    }));
                        } else {
                            // 8. 调用失败，返回一个规范的错误码
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                // 设置 response 对象为装饰过的
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange); // 降级处理返回数据
        } catch (Exception e) {
            log.error("网关处理响应异常" + e);
            return chain.filter(exchange);
        }
    }

    /**
     * 403,资源不可用
     */
    public Mono<Void> handleNoAuth(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        DataBuffer dataBuffer = response.bufferFactory().wrap(message.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(dataBuffer));
    }

    /**
     * 更新当天的统计信息
     */
    private void updateInvokeCount(){
        // 接口调用次数自增
        redisService.incr(RedisPrefixConst.INVOKE_DATE_COUNT, 1L);
    }

}
