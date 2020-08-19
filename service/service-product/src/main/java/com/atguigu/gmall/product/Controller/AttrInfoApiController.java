package com.atguigu.gmall.product.Controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.product.service.AttrInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    //http://api.gmall.com/admin/product/attrInfoList/{category1Id}/{category2Id}/{category3Id}
    //根据分类id获取平台属性
    @RequestMapping("attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result attrInfoList(@PathVariable("category1Id") String category1Id,
                               @PathVariable("category2Id") String category2Id,
                               @PathVariable("category3Id") String category3Id) {

        List<BaseAttrInfo> baseAttrInfos =  attrInfoService.attrInfoList(category1Id,category2Id,category3Id);
        return Result.ok(baseAttrInfos);
    }
}
