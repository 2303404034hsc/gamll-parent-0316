package com.atguigu.gmall.all.Controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.client.ListFeignClient;
import com.atguigu.gmall.model.list.SearchParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * @author ccc
 * @create 2020-08-31 19:50
 */
@Controller
public class ListController {

    @Autowired
    ListFeignClient listFeignClient;

    //http://list.gmall.com:8300/list.html?category3Id=6
    @RequestMapping({"list.html","search.html"})
    public String list(SearchParam searchParam, Model model){

        //调用搜索服务，查询结果
        Result<Map> result = listFeignClient.list(searchParam);
        model.addAllAttributes(result.getData());
        return "list/index";
    }

}
