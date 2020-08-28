package com.atguigu.gmall.product.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.product.mapper.BaseCategory1Mapper;
import com.atguigu.gmall.product.mapper.BaseCategory2Mapper;
import com.atguigu.gmall.product.mapper.BaseCategory3Mapper;
import com.atguigu.gmall.product.mapper.BaseCategoryViewMapper;
import com.atguigu.gmall.product.service.BaseCategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ccc
 * @create 2020-08-19 9:55
 */
@Service
public class BaseCategoryServiceImpl implements BaseCategoryService {

    @Autowired
    BaseCategory1Mapper baseCategory1Mapper;

    @Autowired
    BaseCategory2Mapper baseCategory2Mapper;

    @Autowired
    BaseCategory3Mapper baseCategory3Mapper;

    @Autowired
    BaseCategoryViewMapper baseCategoryViewMapper;

    @Override
    public List<BaseCategory1> getCategory1() {
        return baseCategory1Mapper.selectList(null);
    }

    @Override
    public List<BaseCategory2> getCategory2(String category1Id) {
        QueryWrapper<BaseCategory2> baseCategory2Wrapper = new QueryWrapper<>();
        baseCategory2Wrapper.eq("category1_id", category1Id);
        List<BaseCategory2> baseCategory2s = baseCategory2Mapper.selectList(baseCategory2Wrapper);
        return baseCategory2s;
    }

    @Override
    public List<BaseCategory3> getCategory3(String category2Id) {

        QueryWrapper<BaseCategory3> baseCategory3Wrapper = new QueryWrapper<>();
        baseCategory3Wrapper.eq("category2_id", category2Id);
        List<BaseCategory3> baseCategory3s = baseCategory3Mapper.selectList(baseCategory3Wrapper);
        return baseCategory3s;
    }

    @Override
    public BaseCategoryView getCategoryView(Long category3Id) {
        BaseCategoryView baseCategoryView = baseCategoryViewMapper.selectById(category3Id);
        return baseCategoryView;
    }

    @Override
    public List<JSONObject> getBaseCategoryList() {

        //查询catagoryView
        List<BaseCategoryView> baseCategoryViews = baseCategoryViewMapper.selectList(null);

        //将catagoryView转为json集合，（节点属性相同
        Map<Long, List<BaseCategoryView>> collectCategory1 = baseCategoryViews.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
        List<JSONObject> jsonObjects1 = new ArrayList<>();//最终返回的东西
        for (Map.Entry<Long, List<BaseCategoryView>> listEntry1 : collectCategory1.entrySet()) {
            //封装一级分类的Id和名字
            Long category1Id = listEntry1.getKey();
            String category1Name = listEntry1.getValue().get(0).getCategory1Name();

            JSONObject jsonObject1 = new JSONObject(); //定义装一级分类部分的东西
            jsonObject1.put("categoryName",category1Name);
            jsonObject1.put("categoryId",category1Id);

            //封装1级分类的子集(二级分类
            List<JSONObject> jsonObjects2 = new ArrayList<>();//定义最终返回的二级分类的东西
            Map<Long, List<BaseCategoryView>> collectCategory2 = baseCategoryViews.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
            for (Map.Entry<Long, List<BaseCategoryView>> listEntry2 : collectCategory2.entrySet()) {
                Long category2Id = listEntry2.getKey();
                String category2Name = listEntry2.getValue().get(0).getCategory2Name();

                JSONObject jsonObject2 = new JSONObject();//定义装二级分类部分的东西
                jsonObject2.put("categoryName",category2Name);
                jsonObject2.put("categoryId",category2Id);

                //封装2级分类的子集(三级分类
                List<BaseCategoryView> listEntry3 = listEntry2.getValue();
                //定义最终返回的三级分类的东西
                List<JSONObject> jsonObjects3 =  listEntry3.stream().map(entry3->{
                    JSONObject jsonObject3 = new JSONObject();//定义装三级分类部分的东西
                    Long category3Id = entry3.getCategory3Id();
                    String category3Name = entry3.getCategory3Name();
                    jsonObject3.put("categoryName",category3Name);
                    jsonObject3.put("categoryId",category3Id);
                    return jsonObject3;
                }).collect(Collectors.toList());//集合三级分类元素
                jsonObject2.put("categoryChild",jsonObjects3);//集合二级分类元素
                jsonObjects2.add(jsonObject2);//装好二级分类和三级分类

            }
            jsonObject1.put("categoryChild",jsonObjects2);//将装好二级分类和三级分类的东西变为categoryChild
            jsonObjects1.add(jsonObject1);//放入最终返回的jsonObjects1中
        }
        return jsonObjects1;
    }
}
