package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.service.CartInfoService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author ccc
 * @create 2020-09-04 21:21
 */
@RestController
@RequestMapping("api/cart")
public class CartApiController {

    @Autowired
    CartInfoService cartInfoService;

    @Autowired
    ProductFeignClient productFeignClient;

    @RequestMapping("addCart")
    void addCart(@RequestBody CartInfo cartInfo,HttpServletRequest request){

        String userId = "";
        //获取网关验证获得的userId
        userId = request.getHeader("userTempId");
        //如果真正的userId有值，就覆盖
        if(StringUtils.isNotBlank(request.getHeader("userId"))){
            userId = request.getHeader("userId");
        }
        //封装sku信息
        SkuInfo skuInfo = productFeignClient.getSkuInfo(cartInfo.getSkuId() + "");


        cartInfo.setUserId(userId);
        cartInfo.setIsChecked(1);
        cartInfo.setSkuPrice(skuInfo.getPrice());
        cartInfo.setCartPrice(cartInfo.getSkuPrice().multiply(new BigDecimal(cartInfo.getSkuNum())));
        cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
        cartInfo.setSkuName(skuInfo.getSkuName());
        cartInfoService.addCart(cartInfo);
    }

    @RequestMapping("cartTest")
    public String cartTest(HttpServletRequest request){

        String userId = request.getHeader("userId");

        return userId;
    }


    //cartList  http://api.gmall.com/api/cart/cartList
    //查询购物车列表
    @RequestMapping("cartList")
    public Result cartList(HttpServletRequest request){
        String userId = "";
        //获取网关验证获得的userId
        //首先获取临时userId,
        userId = request.getHeader("userTempId");
        //如果真正的userId有值，就覆盖
        if(StringUtils.isNotBlank(request.getHeader("userId"))){
            userId = request.getHeader("userId");
        }

        //获取cartList
        List<CartInfo> cartInfos = cartInfoService.cartList(userId);

        return Result.ok(cartInfos);
    }
    //购物车选中
    @RequestMapping("checkCart/{skuId}/{isChecked}")
    public void cartList(@PathVariable("skuId") Long skuId,@PathVariable("isChecked") Integer ischecked,HttpServletRequest request){
        String userId = "";
        //获取网关验证获得的userId
        //首先获取临时userId,
        userId = request.getHeader("userTempId");
        //如果真正的userId有值，就覆盖
        if(StringUtils.isNotBlank(request.getHeader("userId"))){
            userId = request.getHeader("userId");
        }
        CartInfo cartInfo = new CartInfo();
        cartInfo.setIsChecked(ischecked);
        cartInfo.setSkuId(skuId);
        cartInfo.setUserId(userId);
        cartInfoService.checkCart(cartInfo);
    }
}
