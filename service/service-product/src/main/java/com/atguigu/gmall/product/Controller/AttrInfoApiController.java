package com.atguigu.gmall.product.Controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.product.service.AttrInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ccc
 * @create 2020-08-19 11:18
 */
@RestController
@RequestMapping("admin/product")
@CrossOrigin
public class AttrInfoApiController {

    @Autowired
    AttrInfoService attrInfoService;


    //根据分类id获取平台属性
    @RequestMapping("attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result attrInfoList(@PathVariable("category1Id") String category1Id, @PathVariable("category2Id") String category2Id, @PathVariable("category3Id") String category3Id) {
        List<BaseAttrInfo> baseAttrInfos = attrInfoService.attrInfoList(category1Id, category2Id, category3Id);
        return Result.ok(baseAttrInfos);
    }

    //http://localhost:8080/admin/product/saveAttrInfo 保存或修改
    //保存或修改平台属性
    @RequestMapping("saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo) {

        return Result.ok();
    }

    //查询平台属性
    //http://localhost:8080/admin/product/getAttrValueList/7
    @RequestMapping("getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable("attrId") String attrId) {

        List<BaseAttrValue> baseAttrValues = attrInfoService.getAttrValueList(attrId);
        return Result.ok(baseAttrValues);
    }

    //删除平台属性值
    //url: '/admin/product/deleteAttrInfo/' + attrValueId,
    @DeleteMapping("deleteAttrInfo/{attrValueId}")
    public Result deleteAttrInfo(@PathVariable("attrValueId") Long attrValueId) {

        attrInfoService.deleteAttrInfo(attrValueId);

        return Result.ok();
    }

}
