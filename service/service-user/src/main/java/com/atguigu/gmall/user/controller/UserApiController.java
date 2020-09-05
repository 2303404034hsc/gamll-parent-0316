package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ccc
 * @create 2020-09-03 10:45
 */
@RestController
@RequestMapping("api/user/passport")
public class UserApiController {

    @Autowired
    UserService userService;

    /**
     * 网关过滤器鉴权
     * @param token
     * @return
     */
    @RequestMapping("inner/verify/{token}")
    public String verify(@PathVariable("token") String token){
        String userId =userService.verify(token);
        System.out.println("能过来");
        return userId;
    }

    /**
     * ajax 异步登录
     * @param userInfo
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody UserInfo userInfo){

        Map<String,Object> map = userService.login(userInfo);

        if(null == map){
            return Result.fail();
        }

        UserInfo userInfoFromDb = (UserInfo)map.get("userInfo");
        String token =(String)map.get("token");
        map.put("token",token);
        map.put("name",userInfoFromDb.getName());
        map.put("nickName",userInfoFromDb.getNickName());

        return Result.ok(map);
    }
}
