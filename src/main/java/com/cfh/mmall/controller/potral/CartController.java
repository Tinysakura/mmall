package com.cfh.mmall.controller.potral;

import static org.hamcrest.CoreMatchers.nullValue;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cfh.mmall.common.Const;
import com.cfh.mmall.common.ServerResponse;
import com.cfh.mmall.pojo.User;
import com.cfh.mmall.service.CartService;
import com.cfh.mmall.vo.CartVo;

/**
 * Created by geely
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @RequestMapping("/list")
    @ResponseBody
    public ServerResponse<CartVo> list(HttpSession session){
        return null;
    }

    @RequestMapping("/add")
    @ResponseBody
    public ServerResponse<CartVo> add(HttpSession session, Integer count, Integer productId){
    	User user = (User)session.getAttribute(Const.CURRENT_USER);
    	
    	if(user == null){
    		return ServerResponse.createErrorResponse(ServerResponse.ResponseCode.NEED_LOGING);
    	}else{
    		return cartService.add(user.getId(), productId, count);
    	}
    }

    @RequestMapping("/update")
    @ResponseBody
    public ServerResponse<CartVo> update(HttpSession session, Integer count, Integer productId){
    	return null;
    }

    @RequestMapping("/delete")
    @ResponseBody
    public ServerResponse<CartVo> deleteProduct(HttpSession session,String productIds){
    	return null;
    }


    @RequestMapping("/select_all")
    @ResponseBody
    public ServerResponse<CartVo> selectAll(HttpSession session){
    	Integer userId = ((User)session.getAttribute(Const.CURRENT_USER)).getId();
        return cartService.selectOrUnSelect(userId,null,Const.Cart.CHECKED);
    }

    @RequestMapping("/un_select_all")
    @ResponseBody
    public ServerResponse<CartVo> unSelectAll(HttpSession session){
    	Integer userId = ((User)session.getAttribute(Const.CURRENT_USER)).getId();
        return cartService.selectOrUnSelect(userId,null,Const.Cart.UN_CHECKED);
    }

    @RequestMapping("/select")
    @ResponseBody
    public ServerResponse<CartVo> select(HttpSession session,Integer productId){
    	Integer userId = ((User)session.getAttribute(Const.CURRENT_USER)).getId();
        return cartService.selectOrUnSelect(userId,productId,Const.Cart.CHECKED);
    }

    @RequestMapping("/un_select")
    @ResponseBody
    public ServerResponse<CartVo> unSelect(HttpSession session,Integer productId){
    	Integer userId = ((User)session.getAttribute(Const.CURRENT_USER)).getId();
        return cartService.selectOrUnSelect(userId,productId,Const.Cart.UN_CHECKED);
    }

    @RequestMapping("/get_cart_product_count")
    @ResponseBody
    public ServerResponse<Integer> getCartProductCount(HttpSession session){
    	return null;
    }
}
