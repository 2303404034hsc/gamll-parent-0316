package com.atguigu.gmall.user.client;

import com.atguigu.gmall.model.user.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author ccc
 * @create 2020-09-03 11:21
 */
@FeignClient(value = "service-user")
public interface UserFeignClient {

    @RequestMapping("api/user/inner/verify/{token}")
    public UserInfo verify(@PathVariable("token") String token);
}
