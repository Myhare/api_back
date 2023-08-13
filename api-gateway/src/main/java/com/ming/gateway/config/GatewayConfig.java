package com.ming.gateway.config;

import com.ming.gateway.filter.BizLogicRouteGatewayFilterFactory;
import com.ming.gateway.filter.InterfaceGlobalFilter;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// @Configuration
// public class GatewayConfig {
//
//     @Bean
//     public RouteLocator customRouteLocator(RouteLocatorBuilder builder, InterfaceGlobalFilter filter,
//                                            GatewayFilterFactory<BizLogicRouteGatewayFilterFactory.BizLogicRouteConfig> customGatewayFilterFactory) {
//         //用路由前缀区分路由来源是前端还是接口管理平台
//         return builder.routes()
//                 .route(r ->
//                         r.path("/api/interface/**")
//                                 .filters(f -> f.filter(filter)
//                                         .filter(customGatewayFilterFactory.apply(new BizLogicRouteGatewayFilterFactory.BizLogicRouteConfig()))
//                                 )
//                                 .uri("http://0.0.0.0") // 后面动态转发，这里随便填写本地
//                 )
//                 .build();
//     }
//
//
// }
