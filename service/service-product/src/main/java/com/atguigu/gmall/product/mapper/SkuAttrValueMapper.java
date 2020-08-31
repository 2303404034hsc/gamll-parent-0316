package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.model.product.SkuAttrValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ccc
 * @create 2020-08-20 15:13
 */
@Mapper
public interface SkuAttrValueMapper extends BaseMapper<SkuAttrValue> {
    List<BaseAttrInfo> selectBaseAttrInfoListBySkuId(@Param("skuId") String skuId);
}
