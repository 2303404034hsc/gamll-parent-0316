package com.atguigu.gmall.user.service;

import com.atguigu.gmall.model.user.UserInfo;

import java.util.Map;

/**
 * @author ccc
 * @create 2020-09-04 10:59
 */
public interface UserService {
    String verify(String token);

    Map<String, Object> login(UserInfo userInfo);
}
