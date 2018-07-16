package com.cfh.mmall.service.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cfh.mmall.common.ServerResponse;
import com.cfh.mmall.dao.CategoryMapper;
import com.cfh.mmall.pojo.Category;
import com.cfh.mmall.service.CategoryService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Service
public class CategoryServiceImpl implements CategoryService{
	@Autowired
	private CategoryMapper categoryMapper;
	
	private Log logger = LogFactory.getLog(CategoryServiceImpl.class);

	@Override
	public ServerResponse<List<Category>> getChildrenParallelCategory(
			Integer categoryId) {
		List<Category> categories = categoryMapper.queryChildCategory(categoryId);
		
		if(CollectionUtils.isEmpty(categories)){
			logger.info("该分类下没有子分类");
		}
		
		return ServerResponse.createSuccessResponse(categories);
	}

	@Override
	public ServerResponse<List<Integer>> selectCategoryAndChildrenById(
			Integer categoryId) {
		Set<Category> set = Sets.newHashSet();
		recursionQueryCategories(set, categoryId);
		
		List<Integer> list = Lists.newArrayList();
		for(Category category : set){
			list.add(category.getId());
		}
		
		return ServerResponse.createSuccessResponse(list);
	}
	
	/**
	 * 递归查找指定分类下的所有子分类
	 */
	public Set<Category> recursionQueryCategories(Set<Category> set,Integer categoryId){
		Category category = categoryMapper.selectByPrimaryKey(categoryId);
		
		if(category != null){
			set.add(category);
		}
		
		List<Category> childCategories = categoryMapper.queryChildCategory(categoryId);
		
		if(childCategories != null){
			for(Category child : childCategories){
				recursionQueryCategories(set, child.getId());
			}
		}
		
		return set;
	}

}
