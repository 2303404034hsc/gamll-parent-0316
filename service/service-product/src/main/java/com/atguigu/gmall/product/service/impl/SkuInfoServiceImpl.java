package com.atguigu.gmall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.cache.GmallCache;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.SkuAttrValueMapper;
import com.atguigu.gmall.product.mapper.SkuImageMapper;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import com.atguigu.gmall.product.mapper.SkuSaleAttrValueMapper;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import springfox.documentation.spring.web.json.Json;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author ccc
 * @create 2020-08-20 10:46
 */
@Service
public class SkuInfoServiceImpl implements SkuInfoService {
    @Autowired
    SkuInfoMapper skuInfoMapper;

    @Autowired
    SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    SkuImageMapper skuImageMapper;

    @Autowired
    RedisTemplate redisTemplate;


    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {

        //skuInfo表的保存
        skuInfoMapper.insert(skuInfo);
        Long skuId = skuInfo.getId();
        Long spuId = skuInfo.getSpuId();
        //sku_attr_value的保存
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if (null != skuAttrValueList) {
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(skuId);
                skuAttrValueMapper.insert(skuAttrValue);
            }
        }

        //sku_sale_attr_value的保存
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        if (null != skuAttrValueList) {
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                skuSaleAttrValue.setSkuId(skuId);
                skuSaleAttrValue.setSpuId(spuId);
                skuSaleAttrValueMapper.insert(skuSaleAttrValue);
            }
        }

        //sku_image的保存
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        if (null != skuImageList) {
            for (SkuImage skuImage : skuImageList) {
                skuImage.setSkuId(skuId);
                skuImageMapper.insert(skuImage);
            }
        }

    }

    @Override
    public IPage<SkuInfo> getSkuInfoListByPage(Page<SkuInfo> pageParam) {
        IPage<SkuInfo> skuInfoIPage = skuInfoMapper.selectPage(pageParam, null);
        return skuInfoIPage;
    }

    @Override
    public void onSale(String skuId) {
        QueryWrapper<SkuInfo> skuInfoQueryWrapper = new QueryWrapper<>();
        skuInfoQueryWrapper.eq("id", skuId);
        SkuInfo skuInfo = skuInfoMapper.selectOne(skuInfoQueryWrapper);
        skuInfo.setIsSale(1);
        skuInfoMapper.updateById(skuInfo);
    }

    @Override
    public void cancelSale(String skuId) {
        QueryWrapper<SkuInfo> skuInfoQueryWrapper = new QueryWrapper<>();
        skuInfoQueryWrapper.eq("id", skuId);
        SkuInfo skuInfo = skuInfoMapper.selectOne(skuInfoQueryWrapper);
        skuInfo.setIsSale(0);
        skuInfoMapper.updateById(skuInfo);
    }

    //获取商品详情
    @GmallCache //该注解是标注需要进行aop处理的缓存方法
    @Override
    public SkuInfo getSkuInfo(String skuId) {

        SkuInfo skuInfo = new SkuInfo();

        // 查询db
        skuInfo = getSkuInfoFromDb(skuId);

        return skuInfo;
    }

    //获取商品详情
    public SkuInfo getSkuInfoForCache(String skuId) {


        System.out.println(Thread.currentThread().getName() + "申请详情");

        SkuInfo skuInfo = null;

        //查询缓存
        String skuStrFromCache = (String) redisTemplate.opsForValue().get("sku:" + skuId + ":info");
        String lockId = UUID.randomUUID().toString();

        if (StringUtils.isBlank(skuStrFromCache)) {


            //分布式缓存锁
            //redis里面的 setnx 只有当键不存在时才会返回成功
            Boolean lock = redisTemplate.opsForValue().setIfAbsent("sku:" + skuId + ":lock", lockId, 100, TimeUnit.SECONDS);
            if (lock) {

                //查询db
                skuInfo = getSkuInfoFromDb(skuId);

                System.out.println(Thread.currentThread().getName() + "拿到分布式锁");

                //同步缓存
                if (null != skuInfo) {
                    redisTemplate.opsForValue().set("sku:" + skuId + ":info", JSON.toJSONString(skuInfo));
                } else {
                    //在缓存添加一个空值 10秒后过期
                    redisTemplate.opsForValue().set("sku:" + skuId + ":info", JSON.toJSONString(new SkuInfo()), 10, TimeUnit.SECONDS);
                }
                //自己线程操作完毕 归还分布式锁
                //删除本线程的锁 解决方案一 按照 redis的v值作为锁的删除id
//                String lockIdFromCache = (String) redisTemplate.opsForValue().get("sku:" + skuId + ":lock");
//                if (StringUtils.isNotBlank(lockIdFromCache) && lockIdFromCache.equals(lockId)) {
//                    redisTemplate.delete("sku:" + skuId + ":lock");
//                }

                //使用LUA脚本删除锁 解决方案二
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                // 设置lua脚本返回的数据类型
                DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
                // 设置lua脚本返回类型为Long
                redisScript.setResultType(Long.class);
                redisScript.setScriptText(script);
                //
                redisTemplate.execute(redisScript, Arrays.asList("sku:" + skuId + ":lock"), lockId);
                System.out.println(Thread.currentThread().getName() + "归还分布式锁");
            } else {
                // 自旋
                System.out.println(Thread.currentThread().getName() + "没有获得分布式锁，开始自旋");
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return getSkuInfo(skuId);
            }
        } else {
            //缓存中有，直接解析
            skuInfo = JSON.parseObject(skuStrFromCache, SkuInfo.class);
        }

        return skuInfo;
    }

    private SkuInfo getSkuInfoFromDb(String skuId) {
        //查询基本表信息
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        //封装图片信息
        QueryWrapper<SkuImage> skuImageWrapper = new QueryWrapper<>();
        skuImageWrapper.eq("sku_id", skuId);
        List<SkuImage> skuImages = skuImageMapper.selectList(skuImageWrapper);
        skuInfo.setSkuImageList(skuImages);
        return skuInfo;
    }

    @Override
    public BigDecimal getSkuPrice(String skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        return skuInfo.getPrice();
    }

    @Override
    public Map<String, String> getSkuValueIdsMap(Long spuId) {
        List<Map<String, Object>> resultMapList = skuSaleAttrValueMapper.selectSkuValueIdsMap(spuId);
        Map<String, String> valueIdsMap = new HashMap<>();
        for (Map<String, Object> map : resultMapList) {
            String skuId = map.get("sku_id").toString();
            String valueId = map.get("value_ids").toString();
            valueIdsMap.put(valueId, skuId);
        }
        return valueIdsMap;
    }

}
