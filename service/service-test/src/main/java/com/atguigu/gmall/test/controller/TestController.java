package com.atguigu.gmall.test.controller;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author ccc
 * @create 2020-08-26 9:39
 */
@Controller
public class TestController {

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    RedisTemplate redisTemplate;

    @RequestMapping("test")
    @ResponseBody
    public String test(){

        Integer ticket = (Integer)redisTemplate.opsForValue().get("ticket");
        ticket--;
        System.out.println("剩余票数:" + ticket);

        redisTemplate.opsForValue().set("ticket",ticket);

        return ticket+"";
    }

    @RequestMapping("testLock")
    @ResponseBody
    public String testLock(){

        RLock lock = redissonClient.getLock("lock");
        Integer ticket = 0;

        try {
            lock.lock();
            ticket = (Integer)redisTemplate.opsForValue().get("ticket");
            ticket--;
            System.out.println("剩余票数:" + ticket);
            redisTemplate.opsForValue().set("ticket",ticket);
        }finally {
            lock.unlock();
        }
        return ticket+"";
    }
}
