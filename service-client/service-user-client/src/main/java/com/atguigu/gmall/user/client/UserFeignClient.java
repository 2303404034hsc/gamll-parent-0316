package com.atguigu.gmall.user.client;

import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.user.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author ccc
 * @create 2020-09-03 11:21
 */
@FeignClient(value = "service-user")
public interface UserFeignClient {

    @RequestMapping("api/user/passport/inner/verify/{token}")
    public String verify(@PathVariable("token") String token);
    @RequestMapping("api/user/passport/inner/findUserAddressListByUserId/{userId}")
    List<UserAddress> findUserAddressListByUserId(@PathVariable("userId") String userId);
}
