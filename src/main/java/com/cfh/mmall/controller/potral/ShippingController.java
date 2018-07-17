package com.cfh.mmall.controller.potral;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cfh.mmall.common.Const;
import com.cfh.mmall.common.ServerResponse;
import com.cfh.mmall.pojo.Shipping;
import com.cfh.mmall.pojo.User;
import com.cfh.mmall.service.ShippingService;
import com.github.pagehelper.PageInfo;

@RestController
@RequestMapping("/shipping")
public class ShippingController {
    @Autowired
    private ShippingService shippingService;

    @RequestMapping(value="/add",method=RequestMethod.POST)
    public ServerResponse<Shipping> add(@RequestBody Shipping shipping,HttpSession session){
    	User user = (User)session.getAttribute(Const.CURRENT_USER);
    	
    	if(user == null){
    		return ServerResponse.createErrorResponse(ServerResponse.ResponseCode.NEED_LOGING);
    	}else{
    		shipping.setCreateTime(new Date());
    		return shippingService.add(user.getId(), shipping);
    	}
    }


    @RequestMapping("/del")
    @ResponseBody
    public ServerResponse<String> del(HttpSession session,Integer shippingId){
    	User user = (User)session.getAttribute(Const.CURRENT_USER);
    	
    	if(user == null){
    		return ServerResponse.createErrorResponse(ServerResponse.ResponseCode.NEED_LOGING);
    	}else{
    		return shippingService.del(user.getId(), shippingId);
    	}
    }

    @RequestMapping("/update")
    @ResponseBody
    public ServerResponse<Shipping> update(HttpSession session,Shipping shipping){
    	return null;
    }


    @RequestMapping("/select")
    @ResponseBody
    public ServerResponse<Shipping> select(HttpSession session,Integer shippingId){
    	return null;
    }


    @RequestMapping("/list")
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10")int pageSize,
                                         HttpSession session){
    	User user = (User)session.getAttribute(Const.CURRENT_USER);
    	
    	if(user == null){
    		return ServerResponse.createErrorResponse(ServerResponse.ResponseCode.NEED_LOGING);
    	}else{
    		return shippingService.list(user.getId(), pageNum, pageSize);
    	}
    }
}
