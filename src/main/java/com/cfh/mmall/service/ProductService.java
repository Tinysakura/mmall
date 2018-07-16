package com.cfh.mmall.service;

import com.cfh.mmall.common.ServerResponse;
import com.cfh.mmall.pojo.Product;
import com.cfh.mmall.vo.ProductDetailVo;
import com.github.pagehelper.PageInfo;

/**
 * Created by geely
 */
public interface ProductService {

    ServerResponse<String> saveOrUpdateProduct(Product product);

    ServerResponse<String> setSaleStatus(Integer productId,Integer status);

    ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductList(int pageNum, int pageSize);

    ServerResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum,int pageSize);

    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy);
}
