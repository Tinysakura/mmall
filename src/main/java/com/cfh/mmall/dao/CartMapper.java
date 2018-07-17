package com.cfh.mmall.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cfh.mmall.pojo.Cart;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);
    
    Cart selectByUserIdProductId(@Param("userId") Integer userId,@Param("productId") Integer productId);

	List<Cart> selectCartByUserId(Integer userId);

	int selectCartProductCheckedStatusByUserId(Integer userId);
	
	void selectOrUnselect(@Param("userId") Integer userId,@Param("productId") Integer productId,@Param("checked") Integer checked);
}