package com.atguigu.gmall.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @author ccc
 * @create 2020-09-02 19:45
 */
@Component
public class AuthGlobalFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return null;
    }

    // 接口鉴权失败返回数据
//    private Mono<Void> out(ServerHttpResponse response, ResultCodeEnum resultCodeEnum) {
//        // 返回用户没有权限登录
//        Result<Object> result = Result.build(null, resultCodeEnum);
//        byte[] bits = JSONObject.toJSONString(result).getBytes(StandardCharsets.UTF_8);
//        DataBuffer wrap = response.bufferFactory().wrap(bits);
//        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
//        // 输出到页面
//        return response.writeWith(Mono.just(wrap));
//    }


}
