package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.mapper.UserAddressMapper;
import com.atguigu.gmall.user.mapper.UserInfoMapper;
import com.atguigu.gmall.user.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author ccc
 * @create 2020-09-04 10:59
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    UserAddressMapper userAddressMapper;


    @Override
    public String verify(String token) {

        //验证token是否存在，并返回userId
        String userId = (String)redisTemplate.opsForValue().get("user:login:" + token);

        return userId;
    }

    @Override
    public Map<String, Object> login(UserInfo userInfo) {

        Map<String, Object> map = null;

        QueryWrapper<UserInfo> userInfoWrapper = new QueryWrapper<>();
        String loginName = userInfo.getLoginName();
        //数据库存的是密文，要先加密再比较
        String password = DigestUtils.md5DigestAsHex(userInfo.getPasswd().getBytes());
        userInfoWrapper.eq("login_name",loginName);
        userInfoWrapper.eq("passwd",password);

        userInfo = userInfoMapper.selectOne(userInfoWrapper);
        //账号密码不为空 直接返回user信息，否则返回null
        if (null != userInfo){
            map  = new HashMap<>();
            //生成token
            //key:user:login:token  value userId
            String token = UUID.randomUUID().toString().replace("-","");

            map.put("userInfo",userInfo);
            map.put("token",token);

            //将token保存到redis数据库
            redisTemplate.opsForValue().set("user:login:" +token,userInfo.getId().toString());
        }
        return map;
    }

    @Override
    public List<UserAddress> findUserAddressListByUserId(String userId) {

        QueryWrapper<UserAddress> userAddressWrapper = new QueryWrapper<>();
        userAddressWrapper.eq("user_id",userId);
        List<UserAddress> userAddressList = userAddressMapper.selectList(userAddressWrapper);
        return userAddressList;
    }
}
