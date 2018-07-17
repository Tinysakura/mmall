package com.cfh.mmall.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cfh.mmall.common.Const;
import com.cfh.mmall.common.ServerResponse;
import com.cfh.mmall.dao.CartMapper;
import com.cfh.mmall.dao.ProductMapper;
import com.cfh.mmall.pojo.Cart;
import com.cfh.mmall.pojo.Product;
import com.cfh.mmall.service.CartService;
import com.cfh.mmall.util.BigDecimalUtil;
import com.cfh.mmall.util.PropertiesUtil;
import com.cfh.mmall.vo.CartProductVo;
import com.cfh.mmall.vo.CartVo;
import com.google.common.collect.Lists;

/**
 * @author Mr.Chen
 * date: 2018年7月16日 下午1:27:25
 */
@Service
public class CartServiceImpl implements CartService{
	@Autowired
	private ProductMapper productMapper;
	@Autowired
	private CartMapper cartMapper;

	@Override
	public ServerResponse<CartVo> add(Integer userId, Integer productId,
			Integer count) {
		//检查参数
		if(userId == null || productId == null){
			return ServerResponse.createErrorResponse(ServerResponse.ResponseCode.ILLEGAL_ARGUMENT);
		}
		
		Cart cart = cartMapper.selectByUserIdProductId(userId, productId);
		
		//若用户之前没有添加过该商品
		if(cart == null){
            Cart cartItem = new Cart();
            cartItem.setQuantity(count);
            //添加进购物车后默认为勾选状态
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            cartItem.setCreateTime(new Date());
            cartMapper.insert(cartItem);
		}else{
			//用户之前已经选中该商品，则增加购买数量
			cart.setQuantity(cart.getQuantity()+count);
			cart.setUpdateTime(new Date());
			cartMapper.updateByPrimaryKey(cart);
		}
		
		//返回更新后的购物车信息
		return list(userId);
	}

	@Override
	public ServerResponse<CartVo> update(Integer userId, Integer productId,
			Integer count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServerResponse<CartVo> deleteProduct(Integer userId,
			String productIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServerResponse<CartVo> list(Integer userId) {
		CartVo cartVo = getCartVoLimit(userId);
		
		return ServerResponse.createSuccessResponse(cartVo);
	}

	/**
	 * 全选
	 * 全反选
	 * 单选
	 * 单反选
	 */
	@Override
	public ServerResponse<CartVo> selectOrUnSelect(Integer userId,
			Integer productId, Integer checked) {
		cartMapper.selectOrUnselect(userId, productId, checked);
		
		return list(userId);
	}

	@Override
	public ServerResponse<Integer> getCartProductCount(Integer userId) {
		// TODO Auto-generated method stub
		return null;
	}

	//对购物车的一个封装方法
    private CartVo getCartVoLimit(Integer userId){
        CartVo cartVo = new CartVo();
        //查询用户的所有购物车
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();

        BigDecimal cartTotalPrice = new BigDecimal("0");

        if(CollectionUtils.isNotEmpty(cartList)){
            for(Cart cartItem : cartList){
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(userId);
                cartProductVo.setProductId(cartItem.getProductId());

                //根据productId查询购物车中商品对应的信息然后封装成VO
                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if(product != null){
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());
                    //判断库存
                    int buyLimitCount = 0;
                    if(product.getStock() >= cartItem.getQuantity()){
                        //库存充足的时候
                        buyLimitCount = cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else{
                    	//库存不足的情况下限制购物车中购买的数量为库存
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        //购物车中更新有效库存
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    //计算总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartProductVo.getQuantity()));
                    cartProductVo.setProductChecked(cartItem.getChecked());
                }

                if(cartItem.getChecked() == Const.Cart.CHECKED){
                    //如果已经勾选,增加到整个的购物车总价中
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                }
                
                //将封装好的vo添加到list中
                cartProductVoList.add(cartProductVo);
            }
        }
        
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        //设置购物车的全选状态
        cartVo.setAllChecked(this.getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        return cartVo;
    }
    
    private boolean getAllCheckedStatus(Integer userId){
        if(userId == null){
            return false;
        }
        return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
    }
}
