package com.atguigu.gmall.product.Controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    //http://api.gmall.com/admin/product/baseTrademark/{page}/{limit}
    @RequestMapping("baseTrademark/{page}/{limit}")
    public Result getTrademarkListPage(@PathVariable("page") Long page, @PathVariable("limit") Long limit) {

        Page<BaseTrademark> pageParam = new Page<>(page,limit);

        IPage<BaseTrademark> baseTrademarkIPage = trademarkService.getTrademarkListPage(pageParam);

        return Result.ok(baseTrademarkIPage);
    }

    //http://api.gmall.com/admin/product/baseTrademark/get/{id}
    //通过id获取一个品牌
    @RequestMapping("baseTrademark/get/{id}")
    public Result getById(@PathVariable("id") String trademarkId) {
        BaseTrademark trademark=trademarkService.getById(trademarkId);
        return Result.ok(trademark);
    }


    //保存品牌
    //http://localhost:8080/admin/product/baseTrademark/save
    @RequestMapping("baseTrademark/save")
    public Result getById(@RequestBody BaseTrademark trademark) {
        trademarkService.save(trademark);
        return Result.ok().message("保存成功");
    }

    //修改品牌
    //http://localhost:8080/admin/product/baseTrademark/update
    @PutMapping("baseTrademark/update")
    public Result update(@RequestBody BaseTrademark trademark) {
        trademarkService.update(trademark);
        return Result.ok().message("修改成功");
    }

    //删除品牌
    //http://localhost:8080/admin/product/baseTrademark/remove/{id}
    @DeleteMapping("baseTrademark/remove/{trademarkId}")
    public Result removeById(@PathVariable Long trademarkId) {
        trademarkService.removeById(trademarkId);
        return Result.ok().message("删除成功");
    }


}
