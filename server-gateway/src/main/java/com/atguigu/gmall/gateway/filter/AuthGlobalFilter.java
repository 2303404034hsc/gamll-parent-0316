package com.atguigu.gmall.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.client.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * @author ccc
 * @create 2020-09-02 19:45
 */
@Component
public class AuthGlobalFilter implements GlobalFilter {
    //匹配路径用的东西
    AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Autowired
    UserFeignClient userFeignClient;

    @Value("${authUrls.url}")
    private String authUrls;// 白名单 --- 无论如何都需要登录的

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获得服务请求和返回的对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        //获取请求地址
        String uri = request.getURI().toString();//http://item.gmall.com/api/product/testApiController
        String path = request.getPath().toString();//api/product/testApiController

        //不拦截认证中心的请求的请求
        if (uri.indexOf("passport") != -1
                || uri.indexOf(".png") != -1
                || uri.indexOf(".js") != -1
                || uri.indexOf(".ico") != -1) {
            return chain.filter(exchange);
        }

        boolean inner = antPathMatcher.match("/api/**/inner/**", path);
        //如果包含inner 就不给访问 --- 拦截外部访问内部的请求
        if (inner) {
            return out(response, ResultCodeEnum.PERMISSION);
        }

        boolean auth = antPathMatcher.match("/api/**/auth/**", path);
        //如果包含inner 就不给访问 --- 拦截外部访问内部的请求
        if (auth) {
            //鉴权，需要登录后才能访问
            return out(response, ResultCodeEnum.PERMISSION);
        }

        // 网关中通过feign调用cas服务器
        UserInfo userInfo = userFeignClient.verify("token");
        System.out.println("userInfo:" + userInfo);

        //白名单鉴权
        //trade.html,myOrder.html
        String[] split = authUrls.split(",");
        for (String authUrl : split) {
//            if (path.indexOf(authUrl) != -1) {
            if (path.contains(authUrl)) {
                // 鉴权
                // 如果没有权限 重定向到登录页面
                response.setStatusCode(HttpStatus.SEE_OTHER);
                response.getHeaders().set(HttpHeaders.LOCATION, "http://passport.gmall.com/login.html");
                Mono<Void> voidMono = response.setComplete();
                return voidMono;
            }

        }

        //转发请求到下一个servlet或者过滤器
        return chain.filter(exchange);
        //失败
//        return out(response, ResultCodeEnum.PERMISSION);
    }

    // 接口鉴权失败返回数据
    private Mono<Void> out(ServerHttpResponse response, ResultCodeEnum resultCodeEnum) {
        // 返回用户没有权限登录
        Result<Object> result = Result.build(null, resultCodeEnum);
        byte[] bits = JSONObject.toJSONString(result).getBytes(StandardCharsets.UTF_8);
        DataBuffer wrap = response.bufferFactory().wrap(bits);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        // 输出到页面
        return response.writeWith(Mono.just(wrap));
    }


}
