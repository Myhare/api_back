package com.ming.gateway.config;

import com.ming.apiCommon.dubbo.InnerInterfaceInfoService;
import com.ming.apiCommon.model.entity.InterfaceInfo;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

/**
 * 动态转发路由
 */
@Component
@Slf4j
public class BizLogicRouteGatewayFilterFactory extends AbstractGatewayFilterFactory<BizLogicRouteGatewayFilterFactory.BizLogicRouteConfig> {

    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    public BizLogicRouteGatewayFilterFactory() {
        super(BizLogicRouteConfig.class);
    }

    @Override
    public GatewayFilter apply(BizLogicRouteConfig config) {

        return (exchange, chain) -> {
            // 本次的请求对象
            ServerHttpRequest request =  exchange.getRequest();

            String method = request.getMethod().toString();
            log.info("BizLogicRouteGatewayFilterFactory请求唯一标识：" + request.getId());
            log.info("BizLogicRouteGatewayFilterFactory请求路径：" + request.getPath());
            log.info("BizLogicRouteGatewayFilterFactory请求方法：" + method);
            log.info("BizLogicRouteGatewayFilterFactory请求参数：" + request.getQueryParams());
            String sourceAddress = request.getLocalAddress().getHostString();
            log.info("BizLogicRouteGatewayFilterFactory请求来源地址：" + sourceAddress);
            log.info("BizLogicRouteGatewayFilterFactory请求来源地址：" + request.getRemoteAddress());

            // 调用方请求时的path
            String rawPath = request.getURI().getRawPath();

            log.info("原请求路径 [{}]", rawPath);

            // 请求头
            HttpHeaders headers = request.getHeaders();
            // 从请求头中获取接口id，查询接口详细信息之后转发到对应的服务器

            // 请求方法
            HttpMethod httpMethod = request.getMethod();

            // 请求参数
            MultiValueMap<String, String> queryParams = request.getQueryParams();

            // 获取接口信息
            // 判断接口是否存在，还有请求方式是否匹配
            String path = request.getPath().value();
            InterfaceInfo interfaceInfo = null;
            try {
                interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(path, method);
            } catch (Exception e) {
                log.error("BizLogicRouteGatewayFilterFactory-dubbo-getInterfaceInfo-错误",e);
            }
            // 通过接口信息获取接口服务器地址

            // URI uri;
            // 通过接口信息设置响应地址
            URI uri = UriComponentsBuilder.fromHttpUrl(interfaceInfo.getHost() + rawPath).queryParams(queryParams).build().toUri();
            // 生成新的Request对象，该对象放弃了常规路由配置中的spring.cloud.gateway.routes.uri字段
            ServerHttpRequest serverHttpRequest = request.mutate().uri(uri).method(httpMethod).headers(httpHeaders -> httpHeaders = httpHeaders).build();

            // 取出当前的route对象
            Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
            //从新设置Route地址
            Route newRoute =
                    Route.async().asyncPredicate(route.getPredicate()).filters(route.getFilters()).id(route.getId())
                            .order(route.getOrder()).uri(uri).build();
            // 放回exchange中
            exchange.getAttributes().put(GATEWAY_ROUTE_ATTR,newRoute);

            // 链式处理，交给下一个过滤器
            return chain.filter(exchange.mutate().request(serverHttpRequest).build());
        };
    }

    /**
     * 这是过滤器的配置类，配置信息会保存在此处
     */
    @Data
    @ToString
    public static class BizLogicRouteConfig {
    }
}
