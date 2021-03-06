package com.cfh.mmall.controller.potral;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.cfh.mmall.common.Const;
import com.cfh.mmall.common.ServerResponse;
import com.cfh.mmall.pojo.User;
import com.cfh.mmall.service.OrderService;
import com.google.common.collect.Maps;

/**
 * Created by geely
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    private static  final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;


    @RequestMapping("/create")
    @ResponseBody
    public ServerResponse create(HttpSession session, Integer shippingId){
    	return null;
    }


    @RequestMapping("/cancel")
    @ResponseBody
    public ServerResponse cancel(HttpSession session, Long orderNo){
    	return null;
    }


    @RequestMapping("/get_order_cart_product")
    @ResponseBody
    public ServerResponse getOrderCartProduct(HttpSession session){
        return null;
    }



    @RequestMapping("/detail")
    @ResponseBody
    public ServerResponse detail(HttpSession session,Long orderNo){
    	return null;
    }

    @RequestMapping("/list")
    @ResponseBody
    public ServerResponse list(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
    	return null;
    }
    
    @RequestMapping("/pay")
    @ResponseBody
    public ServerResponse pay(HttpSession session, Long orderNo, HttpServletRequest request){
    	User user = (User)session.getAttribute(Const.CURRENT_USER);
    	
    	if(user == null){
    		return ServerResponse.createErrorResponse(ServerResponse.ResponseCode.NEED_LOGING);
    	}else{
    		//获取服务器上的临时文件夹路径
    		String path = request.getSession().getServletContext().getRealPath("upload");
    		return orderService.pay(orderNo, user.getId(), path);
    	}
    }

    @RequestMapping("/alipay_callback")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request){
        Map<String,String> params = Maps.newHashMap();

        Map requestParams = request.getParameterMap();
        logger.info(requestParams.toString());
        for(Iterator iter = requestParams.keySet().iterator();iter.hasNext();){
            String name = (String)iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            //将string[]的内容进行拼接
            for(int i = 0 ; i <values.length;i++){
                valueStr = (i == values.length -1)?valueStr + values[i]:valueStr + values[i]+",";
            }
            
            //logger.info(name+":"+valueStr);
            params.put(name,valueStr);
        }
        logger.info("支付宝回调,sign:{},trade_status:{},参数:{}",params.get("sign"),params.get("trade_status"),params.toString());

        //非常重要,验证回调的正确性,是不是支付宝发的.并且呢还要避免重复通知.
        //rsaCheckV2中以及移除了sign所以我们只需要自己移除sign_type
        params.remove("sign_type");
        try {
        	//指定加密的类型为RSA2
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());

            if(!alipayRSACheckedV2){
                return ServerResponse.createErrorResponse("非法请求,验证不通过,再恶意请求我就报警找网警了");
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝验证回调异常",e);
        }

        //验证其他数据，以及减少库存等收尾工作
        ServerResponse serverResponse = orderService.aliCallback(params);
        if(serverResponse.success()){
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }

    @RequestMapping("/query_order_pay_status")
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPayStatus(HttpSession session, Long orderNo){
    	return null;
    }
}
