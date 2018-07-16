package com.cfh.mmall.service;

import java.util.List;

import com.cfh.mmall.common.ServerResponse;
import com.cfh.mmall.pojo.Category;

public interface CategoryService {
    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);
    ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);
}
