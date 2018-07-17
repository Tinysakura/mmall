package com.cfh.mmall.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cfh.mmall.pojo.Product;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

	List<Product> selectList();
	
	//减少库存
	void decreaseStock(@Param("productId") Integer productId,@Param("decreaseAmount") Integer decreaseAmounnt);

	//根据关键字与分类id查询商品集
    List<Product> selectByNameAndCategoryIds(@Param("productName")String productName,@Param("categoryIdList")List<Integer> categoryIdList);
}