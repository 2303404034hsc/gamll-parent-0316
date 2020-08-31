package com.atguigu.gmall.list.controller;


import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author ccc
 * @create 2020-08-29 14:25
 */
@Controller
public class TestController {

    @Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;

//    @Autowired
//    ElasticsearchRepository elasticsearchRepository;

    @Autowired
    RestHighLevelClient restHighLevelClient;


    @RequestMapping("/")
    public String test() {

        System.out.println("elasticsearchRepository = " + elasticsearchRestTemplate);
        System.out.println("restHighLevelClient = " + restHighLevelClient);

        return "hello";
    }

}
