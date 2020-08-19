package com.atguigu.gmall.product.Controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author ccc
 * @create 2020-08-19 19:19
 */
@RestController
@RequestMapping("admin/product")
@CrossOrigin
public class TrademarkApiController {

    @Autowired
    BaseTrademarkService trademarkService;

    //http://api.gmall.com/admin/product/baseTrademark/getTrademarkList
    //获取品牌属性
    @RequestMapping("baseTrademark/getTrademarkList")
    public Result getTrademarkList() {

        List<BaseTrademark> baseTrademarks =  trademarkService.getTrademarkList();
        return Result.ok(baseTrademarks);
    }
}
