package com.cfh.mmall.controller.backend;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cfh.mmall.common.ServerResponse;
import com.cfh.mmall.pojo.Category;
import com.cfh.mmall.service.CategoryService;

/**
 * Created by geely
 */
@RestController
@RequestMapping("manage/category")
public class CategoryManageController {
	@Autowired
	private CategoryService categoryService;
	
	//增加一个节点
    @RequestMapping("/add_category")
    public ServerResponse<String> addCategory(HttpSession session,String categoryName,@RequestParam(value = "parentId",defaultValue = "0") int parentId){
       return null;
    }

    //修改一个节点
    @RequestMapping("/set_category_name")
    @ResponseBody
    public ServerResponse<String> setCategoryName(HttpSession session,Integer categoryId,String categoryName){
       return null;
    }

    //获取该节点下所有孩子节点的信息，不递归
    @RequestMapping("/get_category/{categoryId}")
    @ResponseBody
    public ServerResponse<List<Category>> getChildrenParallelCategory(HttpSession session,@PathVariable Integer categoryId){
       return categoryService.getChildrenParallelCategory(categoryId);
    }

    //获取节点下的所有孩子节点的信息，递归
    @RequestMapping("/get_deep_category/{categoryId}")
    @ResponseBody
    public ServerResponse<List<Integer>> getCategoryAndDeepChildrenCategory(HttpSession session,@PathVariable Integer categoryId){
        return categoryService.selectCategoryAndChildrenById(categoryId);
    }

}
