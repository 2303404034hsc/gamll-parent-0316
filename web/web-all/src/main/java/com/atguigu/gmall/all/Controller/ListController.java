package com.atguigu.gmall.all.Controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.client.ListFeignClient;
import com.atguigu.gmall.model.list.SearchParam;
import org.apache.commons.lang3.StringUtils;
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
    @RequestMapping({"list.html", "search.html"})
    public String list(SearchParam searchParam, Model model) {

        //调用搜索服务，查询结果
        Result<Map> result = listFeignClient.list(searchParam);
        model.addAllAttributes(result.getData());
        model.addAttribute("searchParam", searchParam);
        model.addAttribute("urlParam", getUrlParam(searchParam));
        return "list/index";
    }

    private String getUrlParam(SearchParam searchParam) {

        //拿取searchParam需要的参数
        Long category3Id = searchParam.getCategory3Id();
        String keyword = searchParam.getKeyword();
        String trademark = searchParam.getTrademark();
        String[] props = searchParam.getProps();

        StringBuffer urlParam = new StringBuffer();

        urlParam.append("list.html");

        if (null != category3Id && category3Id > 0) {
            urlParam.append("category3Id=" + category3Id);
        }

        if (StringUtils.isNotBlank(keyword)) {
            urlParam.append("keyword=" + keyword);
        }

        if (StringUtils.isNotBlank(trademark)) {
            urlParam.append("trademark=" + trademark);
        }

        if (null != props && props.length > 0) {
            for (String prop :props){
                urlParam.append("props=" + props);
            }
        }

        return urlParam.toString();
    }

}
