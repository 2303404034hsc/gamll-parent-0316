package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.mapper.CartInfoMapper;
import com.atguigu.gmall.cart.service.CartInfoService;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.product.client.ProductFeignClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ccc
 * @create 2020-09-07 13:32
 */
@Service
public class CartInfoServiceImpl implements CartInfoService {

    @Autowired
    CartInfoMapper cartInfoMapper;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ProductFeignClient productFeignClient;

    @Override
    public void addCart(CartInfo cartInfo) {

        // 检索当前数据库数据是否添加过
        Long skuId = cartInfo.getSkuId();
        String userId = cartInfo.getUserId();

        QueryWrapper<CartInfo> cartInfoWrapper = new QueryWrapper<>();
        cartInfoWrapper.eq("sku_id", skuId);
        cartInfoWrapper.eq("user_id", userId);

        CartInfo cartInfoAdd = cartInfoMapper.selectOne(cartInfoWrapper);
        //如果没添加过 就进行数据库的插入
        if (null == cartInfoAdd) {
            cartInfoMapper.insert(cartInfo);
            //购物车数据结构hset
            //key -> user:skuId:cart,Field skuId,value = carInfo
            redisTemplate.boundHashOps("user:" + cartInfo.getUserId() + ":cart").put(cartInfo.getSkuId() + "", cartInfo);

        } else {
            //更新数量
            Integer skuNum = cartInfo.getSkuNum();
            Integer skuNumAdd = cartInfoAdd.getSkuNum();

            cartInfoAdd.setSkuNum(skuNum + skuNumAdd);
            // skuPrice是单独查询的
            cartInfoAdd.setSkuPrice(cartInfo.getSkuPrice());

            cartInfoAdd.setCartPrice(cartInfoAdd.getSkuPrice().multiply(new BigDecimal(cartInfoAdd.getSkuNum())));

            //同步缓存
            cartInfoMapper.updateById(cartInfoAdd);
            redisTemplate.boundHashOps("user:" + cartInfo.getUserId() + ":cart").put(cartInfo.getSkuId() + "", cartInfoAdd);
        }
    }

    @Override
    public List<CartInfo> cartList(String userId) {

        List<CartInfo> cartInfos = new ArrayList<>();
        cartInfos = (List<CartInfo>) redisTemplate.opsForHash().values("user:" + userId + ":cart");

        //缓存查不到数据
        if(null == cartInfos||cartInfos.size()==0){
            cartInfos = syncCartCache(userId);
        }

        return cartInfos;
    }

    private List<CartInfo> syncCartCache(String userId) {
        List<CartInfo> cartInfos;//查询db
        QueryWrapper<CartInfo> cartInfoWrapper = new QueryWrapper<>();
        cartInfoWrapper.eq("user_id",userId);
        cartInfos = cartInfoMapper.selectList(cartInfoWrapper);

        if(null != cartInfos||cartInfos.size()>0){
            //同步缓存
            Map<String,Object> map = new HashMap<>();
            for (CartInfo cartInfo : cartInfos) {
                //加入商品价格
                BigDecimal skuPrice = productFeignClient.getSkuPrice(cartInfo.getSkuId() + "");
                cartInfo.setSkuPrice(skuPrice);
                map.put(cartInfo.getSkuId()+"",cartInfo);
            }
            redisTemplate.boundHashOps("user:"+userId+":cart").putAll(map);
        }
        return cartInfos;
    }

    @Override
    public void checkCart(CartInfo cartInfo) {

        //查询数据库
        QueryWrapper<CartInfo> cartInfoWrapper = new QueryWrapper<>();
        cartInfoWrapper.eq("user_id",cartInfo.getUserId());
        cartInfoWrapper.eq("sku_id",cartInfo.getSkuId());

        cartInfoMapper.update(cartInfo,cartInfoWrapper);

        //同步缓存
        syncCartCache(cartInfo.getUserId());
    }

    @Override
    public List<CartInfo> getCartCheckedList(String userId) {

        QueryWrapper<CartInfo> cartInfoWrapper = new QueryWrapper<>();
        cartInfoWrapper.eq("user_id",userId);
        List<CartInfo> cartInfos = cartInfoMapper.selectList(cartInfoWrapper);

        return cartInfos;
    }

    @Override
    public void removeCartCheckedList(String userId) {
        QueryWrapper<CartInfo> cartInfoWrapper = new QueryWrapper<>();
        cartInfoWrapper.eq("user_id",userId);
        cartInfoWrapper.eq("is_checked",1);
        cartInfoMapper.delete(cartInfoWrapper);
    }
}
