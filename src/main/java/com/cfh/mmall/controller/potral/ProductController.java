package com.cfh.mmall.controller.potral;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cfh.mmall.common.ServerResponse;
import com.cfh.mmall.service.ProductService;
import com.cfh.mmall.vo.ProductDetailVo;
import com.github.pagehelper.PageInfo;

/**
 * 前台商品管理查询模块
 * @author Mr.Chen
 * date: 2018年7月15日 上午11:44:57
 */
@Controller
@RequestMapping("/product")
public class ProductController {
	Log logger = LogFactory.getLog(ProductController.class);

    @Autowired
    private ProductService productService;

    @RequestMapping(value="/detail",method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse<ProductDetailVo> detail(Integer productId){
        return productService.getProductDetail(productId);
    }

    @RequestMapping(value="/list",method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(value = "keyword",required = false)String keyword,
                                         @RequestParam(value = "categoryId",required = false)Integer categoryId,
                                         @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10") int pageSize,
                                         @RequestParam(value = "orderBy",defaultValue = "") String orderBy){
        logger.info("keyword:"+keyword);
    	return productService.getProductByKeywordCategory(keyword,categoryId,pageNum,pageSize,orderBy);
    }
}
