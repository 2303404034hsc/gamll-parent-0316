package com.atguigu.gmall.common.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author ccc
 * @create 2020-08-25 15:25
 */
@Aspect
@Component
public class GmallCacheAspect {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Around("@annotation(com.atguigu.gmall.common.cache.GmallCache)")
    public Object cacheAroundAdvice(ProceedingJoinPoint point) {

//        return getObject(point);
        return getObject2(point);

//        return getObject(point);
    }

    private Object getObject(ProceedingJoinPoint point) {
        Object result;

        Integer count = 10;

        //final  ThreadLocal<Integer> threadLocal = new ThreadLocal<>();
        //threadLocal.set(10);


        //local.set(++normalNum);
        System.out.println("执行被代理之前");
        //获得参数
        Object[] args = point.getArgs();

        //
        MethodSignature signature = (MethodSignature) point.getSignature();
        GmallCache annotation = signature.getMethod().getAnnotation(GmallCache.class);
        String key = annotation.prefix() + ":" + args[0];

        String lockId = UUID.randomUUID().toString();

        //查询缓存
        result = cacheHit(signature, key, count, true);
        //result = cacheHit(signature, key, threadLocal, true);
        //判断有没有值
        if (null == result) {
            //获取分布式锁
            //分布式缓存锁
            //redis里面的 setnx 只有当键不存在时才会返回成功
            Boolean lock = redisTemplate.opsForValue().setIfAbsent(key + ":lock", lockId, 100, TimeUnit.SECONDS);
            if (lock) {
                try {
                    //访问db
                    result = point.proceed();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                //同步到缓存
                if (null != result) {
                    redisTemplate.opsForValue().set(key + ":info", JSON.toJSONString(result));
                } else {
                    //在缓存添加一个空值 10秒后过期
                    Object o = null;
                    try {
                        o = signature.getReturnType().newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    redisTemplate.opsForValue().set(key + ":info", JSON.toJSONString(o), 10, TimeUnit.SECONDS);
                }

                //使用LUA脚本删除锁
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                // 设置lua脚本返回的数据类型
                DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
                // 设置lua脚本返回类型为Long
                redisScript.setResultType(Long.class);
                redisScript.setScriptText(script);

                redisTemplate.execute(redisScript, Arrays.asList(key + ":lock"), lockId);
                System.out.println(Thread.currentThread().getName() + "归还分布式锁");

            } else {
                //自旋,如果在这里写自旋，会不停的代理，不能再这里自旋
                //在缓存中自旋
                return cacheHit(signature, key, count, false);
                //return cacheHit(signature, key, threadLocal,false);
            }
        }
        //后置通知
        return result;
    }

    private Object getObject2(ProceedingJoinPoint point) {
        Object result;

        Integer count = 10;

        System.out.println("执行被代理之前");
        //获得参数
        Object[] args = point.getArgs();

        //
        MethodSignature signature = (MethodSignature) point.getSignature();
        GmallCache annotation = signature.getMethod().getAnnotation(GmallCache.class);
        String key = annotation.prefix() + ":" + args[0];

        //查询缓存
        result = cacheHit(signature, key, count, true);
        //result = cacheHit(signature, key, threadLocal, true);
        //判断有没有值
        if (null == result) {
            //获取分布式锁
            //分布式缓存锁
            //redis里面的 setnx 只有当键不存在时才会返回成功
            RLock redissonClientLock = redissonClient.getLock(key);
            try {
                boolean lock = redissonClientLock.tryLock(100, 10, TimeUnit.SECONDS);
                if (lock) {
                    try {
                        //访问db
                        result = point.proceed();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    //同步到缓存
                    if (null != result) {
                        redisTemplate.opsForValue().set(key + ":info", JSON.toJSONString(result));
                    } else {
                        //在缓存添加一个空值 10秒后过期
                        Object o = null;
                        try {
                            o = signature.getReturnType().newInstance();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        redisTemplate.opsForValue().set(key + ":info", JSON.toJSONString(o), 10, TimeUnit.SECONDS);
                    }
                } else {
                    //在缓存中自旋
                    return cacheHit(signature, key, count, false);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                redissonClientLock.unlock();
            }
        }
        //后置通知
        return result;
    }


    /**
     * @param signature 可以获取到方法的返回值
     * @param key       需要获取数据使用
     * @param count     自旋上限
     * @param isHit     判断该方法是查询缓存还是进入自旋状态，也可以不要
     * @return
     */
    private Object cacheHit(MethodSignature signature, String key, Integer count, boolean isHit) {
        String cache = (String) redisTemplate.opsForValue().get(key + ":info");
        if (StringUtils.isNotBlank(cache)) {
            Class returnType = signature.getReturnType();
            return JSONObject.parseObject(cache, returnType);
        } else {
            if (!isHit && count > 0) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    //  ignore
                }
                count--;
                return cacheHit(signature, key, count, isHit);
            } else {
                return null;
            }

        }
    }

    /**
     * @param signature 可以获取到方法的返回值
     * @param key       需要获取数据使用
     * @return
     */
    private Object cacheHit(MethodSignature signature, String key, ThreadLocal<Integer> threadLocal, boolean isHit) {
        String cache = (String) redisTemplate.opsForValue().get(key + ":info");

        if (StringUtils.isNotBlank(cache)) {
            Class returnType = signature.getReturnType();
            return JSONObject.parseObject(cache, returnType);
        } else {
            Integer count = threadLocal.get();
            if (!isHit && count > 0) {
                count--;
                threadLocal.set(count);
                //threadLocal.set(--count);
                return cacheHit(signature, key, count, isHit);
            } else {
                return null;
            }

        }
    }


}
