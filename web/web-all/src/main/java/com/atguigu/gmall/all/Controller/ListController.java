package com.atguigu.gmall.all.Controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.client.ListFeignClient;
import com.atguigu.gmall.model.list.SearchParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
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
        //获取排序参数
        Map<String, String> ordermap = getOrderMap(searchParam);

        model.addAttribute("orderMap", ordermap);
        return "list/index";
    }

    private Map<String, String> getOrderMap(SearchParam searchParam) {
        Map<String,String> ordermap = new HashMap<>();
        String order = searchParam.getOrder();
        //选择用户的东西进行排序，如果没有 有默认值
        if(StringUtils.isNotBlank(order)){
            //2:asc    第一个是按照啥类型排序 1是热度 2是价格  第二个是降序还是升序
            String[] split = order.split(":");
            String type = split[0];
            String sort = split[1];
            ordermap.put("type",type);//1 是按照hotScore排序 2 是按照价格排序
            ordermap.put("sort",sort);// desc 降序 asc 升序
        }else {
            //默认按照热度降序
            ordermap.put("type","1");//1 是按照hotScore排序 2 是按照价格排序
            ordermap.put("sort","desc");// desc 降序 asc 升序
        }
        return ordermap;
    }

    private String getUrlParam(SearchParam searchParam) {

        //拿取searchParam需要的参数
        Long category3Id = searchParam.getCategory3Id();
        String keyword = searchParam.getKeyword();
        String trademark = searchParam.getTrademark();
        String[] props = searchParam.getProps();

        String urlParam = "list.html?";

        if (null != category3Id && category3Id > 0) {
            urlParam = urlParam + "category3Id=" + category3Id;
        }

        if (StringUtils.isNotBlank(keyword)) {
            urlParam = urlParam + "keyword=" + keyword;
        }

        if (StringUtils.isNotBlank(trademark)) {
            urlParam = urlParam + "&trademark=" + trademark;
        }

        if (null != props && props.length > 0) {
            for (String prop : props) {
                urlParam = urlParam + "&props=" + prop;
            }
        }

        return urlParam;
    }

}
