package com.atguigu.gmall.user.service;

import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.user.UserInfo;

import java.util.List;
import java.util.Map;

/**
 * @author ccc
 * @create 2020-09-04 10:59
 */
public interface UserService {
    String verify(String token);

    Map<String, Object> login(UserInfo userInfo);

    List<UserAddress> findUserAddressListByUserId(String userId);
}
