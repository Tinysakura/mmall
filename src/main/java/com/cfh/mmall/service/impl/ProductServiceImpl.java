package com.cfh.mmall.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cfh.mmall.common.Const;
import com.cfh.mmall.common.ServerResponse;
import com.cfh.mmall.dao.CategoryMapper;
import com.cfh.mmall.dao.ProductMapper;
import com.cfh.mmall.pojo.Category;
import com.cfh.mmall.pojo.Product;
import com.cfh.mmall.service.CategoryService;
import com.cfh.mmall.service.ProductService;
import com.cfh.mmall.util.DateTimeUtil;
import com.cfh.mmall.util.PropertiesUtil;
import com.cfh.mmall.vo.ProductDetailVo;
import com.cfh.mmall.vo.ProductListVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;

@Service
public class ProductServiceImpl implements ProductService{
	@Autowired
	private ProductMapper productMapper;
	@Autowired
	private CategoryMapper categoryMapper;
	@Autowired
	private CategoryService categoryService;

	@Override
	public ServerResponse<String> saveOrUpdateProduct(Product product) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServerResponse<String> setSaleStatus(Integer productId,
			Integer status) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServerResponse<PageInfo> getProductList(int pageNum, int pageSize) {
		//使用pageHelper插件设置要查询的页数和和每页的数量
        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList = productMapper.selectList();

        //对数据进行一个vo转换
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product productItem : productList){
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        
        //获取分页信息PageInfo
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);
        
        return ServerResponse.createSuccessResponse(pageResult);
	}

	@Override
	public ServerResponse<PageInfo> searchProduct(String productName,
			Integer productId, int pageNum, int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServerResponse<ProductDetailVo> getProductDetail(Integer productId) {
		if(productId == null){
			return ServerResponse.createErrorResponse("商品不存在");
		}
		
		Product product = productMapper.selectByPrimaryKey(productId);
		if(product == null){
			return ServerResponse.createErrorResponse("商品已下架或删除");
		}else if(!product.getStatus().equals(Const.ProductStatusEnum.ON_SALE.getCode())){
			return ServerResponse.createErrorResponse("商品已下架或删除");//判断商品当前的销售状态
		}
		
		//将product pojo包装成前台需要的vo
		ProductDetailVo productDetailVo = assembleProductDetailVo(product);
		return ServerResponse.createSuccessResponse(productDetailVo);
	}

	@Override
	public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,
			Integer categoryId, int pageNum, int pageSize, String orderBy) {
        //参数错误
		if(StringUtils.isBlank(keyword) && categoryId == null){
            return ServerResponse.createErrorResponse(ServerResponse.ResponseCode.ILLEGAL_ARGUMENT);
        };
        List<Integer> categoryIdList = new ArrayList<Integer>();

        if(categoryId != null){
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if(category == null && StringUtils.isBlank(keyword)){
                //没有该分类,并且还没有关键字,这个时候返回一个空的结果集,不报错
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVo> productListVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return ServerResponse.createSuccessResponse(pageInfo);
            }
            categoryIdList = categoryService.selectCategoryAndChildrenById(category.getId()).getData();
        }
        //拼接关键词
        if(StringUtils.isNotBlank(keyword)){
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }

        PageHelper.startPage(pageNum,pageSize);
        //排序处理
        if(StringUtils.isNotBlank(orderBy)){
        	//首先判断排序条件是否合法
            if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy) || 
            		Const.ProductListOrderBy.STOCK_ASC_DESC.contains(orderBy)){
                String[] orderByArray = orderBy.split("_");
                //对排序条件的格式进行转换，然后使用PageHelper提供的orderBy方法对sql进行预处理（给sql拼接上排序条件）
                PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
            }
        }
        List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword)?null:keyword,categoryIdList.size()==0?null:categoryIdList);

        //对返回的结果集进行一个vo转换
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product product : productList){
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }

        //使用pageInfo返回分页的详情
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createSuccessResponse(pageInfo);
	}	
	
	//将product_pojo向product_vo转换
	private ProductDetailVo assembleProductDetailVo(Product product){
		//基本属性
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());
        //图片服务器地址
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        //商品分类的详细信息
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category == null){
            productDetailVo.setParentCategoryId(0);//默认根节点
        }else{
            productDetailVo.setParentCategoryId(category.getParentId());
        }

        //对日期进行格式转换方便前台展示
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }
	
	//将product_pojo包装成productListVo的方法
    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());
        return productListVo;
    }
}
