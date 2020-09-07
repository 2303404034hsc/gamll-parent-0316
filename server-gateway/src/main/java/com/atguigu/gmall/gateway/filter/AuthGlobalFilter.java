package com.atguigu.gmall.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.client.UserFeignClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MultiValueMap;
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

    /**
     * 匹配路径用的东西
     */
    AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Autowired
    UserFeignClient userFeignClient;

    @Value("${authUrls.url}")
    // 白名单 --- 无论如何都需要登录的
    private String authUrls;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获得服务请求和返回的对象
        ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

        //获取请求地址
        //http://item.gmall.com/api/product/testApiController
        String uri = request.getURI().toString();
        //api/product/testApiController
        String path = request.getPath().toString();


        ///js/plugins/axios.min.map
        //不拦截认证中心的请求的请求
        if(StringUtils.containsAny(uri,
                "passport",".png",".jpg", ".js",".ico",".css",".map")){
            return chain.filter(exchange);
        }

        boolean inner = antPathMatcher.match("/api/**/inner/**", path);
        //如果包含inner 就不给访问 --- 拦截外部访问内部的请求
        if (inner) {
            return out(response, ResultCodeEnum.PERMISSION);
        }

        // 网关中通过feign调用cas服务器
        // 从request对象中获取token
        String userId = "";
        String token = getToken(request);
        if(!StringUtils.isEmpty(token)){
            userId = userFeignClient.verify(token);
        }
        //auth - 需要一定登录权限才能使用的异步方法
        boolean auth = antPathMatcher.match("/api/**/auth/**", path);
        //
        if (auth) {
            //判断用户有没有登录
            if(!StringUtils.isEmpty(userId)){
                //登录了 让它过去
                return chain.filter(exchange);
            }else{
                //返回错误信息
                return out(response, ResultCodeEnum.PERMISSION);
            }

        }

        //白名单鉴权
        //trade.html,myOrder.html
        String[] split = authUrls.split(",");
        for (String authUrl : split) {
//            if (path.indexOf(authUrl) != -1) {
            if (path.contains(authUrl)) {
                // 鉴权
                if(StringUtils.isEmpty(userId)){
                    // 如果没有权限 重定向到登录页面
                    response.setStatusCode(HttpStatus.SEE_OTHER);
                    response.getHeaders().set(HttpHeaders.LOCATION, "http://passport.gmall.com/login.html?originUrl="+uri);
                    Mono<Void> voidMono = response.setComplete();
                    return voidMono;
                }
            }

        }
        //通过request 传递userId
        if(!StringUtils.isEmpty(userId)){
            request.mutate().header("userId",userId).build();// 刷新request放入userId
            exchange.mutate().request(request).build();//刷新exchange放入userId
        }else {
            //网关传递临时Id,给购物车使用
            String userTempId = getUserTempId(request);
            request.mutate().header("userTempId",userTempId).build();// 刷新request放入userId
            exchange.mutate().request(request).build();//刷新exchange放入userId
        }

        //转发请求到下一个servlet或者过滤器
        return chain.filter(exchange);
        //失败
//        return out(response, ResultCodeEnum.PERMISSION);
    }
    private String getUserTempId(ServerHttpRequest request) {
        String userTempId = "";

        MultiValueMap<String, HttpCookie> cookies = request.getCookies();//默认取.gmall.com这个Domain

        if(null!=cookies){
            HttpCookie tokenCookie = cookies.getFirst("userTempId");
            if(null!=tokenCookie){
                userTempId = tokenCookie.getValue();
            }
        }

        //ajax异步访问会为空，进行特俗处理
        if(StringUtils.isEmpty(userTempId)){
            userTempId = request.getHeaders().getFirst("userTempId");
        }

        return userTempId;
    }

    private String getToken(ServerHttpRequest request) {
        String token = "";

        MultiValueMap<String, HttpCookie> cookies = request.getCookies();//默认取.gmall.com这个Domain

        if(null!=cookies){
            HttpCookie tokenCookie = cookies.getFirst("token");
            if(null!=tokenCookie){
                token = tokenCookie.getValue();
            }
        }

        //ajax异步访问会为空，进行特俗处理
        if(StringUtils.isEmpty(token)){
            token = request.getHeaders().getFirst("token");
        }

        return token;
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
