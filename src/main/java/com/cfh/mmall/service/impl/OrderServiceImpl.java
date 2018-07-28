package com.cfh.mmall.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.cfh.mmall.common.Const;
import com.cfh.mmall.common.ServerResponse;
import com.cfh.mmall.dao.CartMapper;
import com.cfh.mmall.dao.OrderItemMapper;
import com.cfh.mmall.dao.OrderMapper;
import com.cfh.mmall.dao.PayInfoMapper;
import com.cfh.mmall.dao.ProductMapper;
import com.cfh.mmall.pojo.Order;
import com.cfh.mmall.pojo.OrderItem;
import com.cfh.mmall.pojo.PayInfo;
import com.cfh.mmall.service.OrderService;
import com.cfh.mmall.util.BigDecimalUtil;
import com.cfh.mmall.util.DateTimeUtil;
import com.cfh.mmall.util.FTPUtil;
import com.cfh.mmall.util.PropertiesUtil;
import com.cfh.mmall.vo.OrderVo;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Service
public class OrderServiceImpl implements OrderService{
    private static  AlipayTradeService tradeService;
    /**
     * 在静态块中初始化Alipayservice
     */
    static {

        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
    }
	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private OrderItemMapper orderItemMapper;
	@Autowired
	private CartMapper cartMapper;
	@Autowired
	private PayInfoMapper payInfoMapper;
	@Autowired
	private ProductMapper productMapper;
	
	private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

	/**
	 * 将生成的支付二维码上传到ftp文件服务器
	 */
	@Override
	public ServerResponse pay(Long orderNo, Integer userId, String path) {
		Map<String, String> resultMap = Maps.newHashMap();
		
		//判断订单是否存在
		Order order = orderMapper.selectByOrderIdUserId(userId, orderNo);
	    if(order == null){
	    	return ServerResponse.createErrorResponse("订单不存在");
	    }else{
	    	resultMap.put("order_no",order.getOrderNo().toString());
	    	// 组装支付宝sdk所需要的字段	    	
	    	// (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
	        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
	        String outTradeNo = order.getOrderNo().toString();
	        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
	        String subject = new StringBuilder().append("happymmall扫码支付,订单号:").append(outTradeNo).toString();
	        // (必填) 订单总金额，单位为元，不能超过1亿元
	        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
	        String totalAmount = order.getPayment().toString();
	        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
	        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
	        String undiscountableAmount = "0";
	        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
	        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
	        String sellerId = "";
	        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
	        String body = new StringBuilder().append("订单").append(outTradeNo).append("购买商品共").append(totalAmount).append("元").toString();
	        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
	        String operatorId = "test_operator_id";
	        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
	        String storeId = "test_store_id";
	        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
	        ExtendParams extendParams = new ExtendParams();
	        extendParams.setSysServiceProviderId("2088100200300400500");
	        // 支付超时，定义为120分钟
	        String timeoutExpress = "120m";
	        
	        // 商品明细列表，需填写购买商品详细信息，
	        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();

	        //查询订单对应的商品明细放入good列表中
	        List<OrderItem> orderItemList = orderItemMapper.getByOrderNoUserId(orderNo,userId);
	        for(OrderItem orderItem : orderItemList){
	            GoodsDetail goods = GoodsDetail.newInstance(orderItem.getProductId().toString(), orderItem.getProductName(),
	                    BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(),new Double(100).doubleValue()).longValue(),
	                    orderItem.getQuantity());
	            goodsDetailList.add(goods);
	        }

	        // 创建扫码支付请求builder，设置请求参数
	        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
	                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
	                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
	                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
	                .setTimeoutExpress(timeoutExpress)
	                //获取配置文件中的回调地址
	                .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
	                .setGoodsDetailList(goodsDetailList);

	        //根据响应状态调用不同的逻辑
	        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
	        switch (result.getTradeStatus()) {
	            case SUCCESS:
	                logger.info("支付宝预下单成功: )");

	                AlipayTradePrecreateResponse response = result.getResponse();
	                dumpResponse(response);

	                File folder = new File(path);
	                if(!folder.exists()){
	                    folder.setWritable(true);
	                    folder.mkdirs();
	                }

	                //需要修改为运行机器上的路径
	                logger.info("path:"+path);
	                String qrPath = String.format(path+"/"+"qr-%s.png",response.getOutTradeNo());
	                String qrFileName = String.format("qr-%s.png",response.getOutTradeNo());
	                //使用ZxingUtils根据response中的OrCode在指定路径下生成二维码图片
	                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);
	                //将图片上传到文件服务器
	                File targetFile = new File(path,qrFileName);
	                try {
	                    FTPUtil.uploadFile(Lists.newArrayList(targetFile));
	                    //将缓存在服务器upload文件夹下的二维码图片删除
	                    targetFile.delete();
	                } catch (IOException e) {
	                    logger.error("上传二维码异常",e);
	                }
	                logger.info("qrPath:" + qrPath);
	                String qrUrl = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFile.getName();
	                //将存放在文件服务器上的图片地址返回给前端展示
	                resultMap.put("qrUrl",qrUrl);
	                return ServerResponse.createSuccessResponse(resultMap);
	            case FAILED:
	                logger.error("支付宝预下单失败!!!");
	                return ServerResponse.createErrorResponse("支付宝预下单失败!!!");
	            case UNKNOWN:
	                logger.error("系统异常，预下单状态未知!!!");
	                return ServerResponse.createErrorResponse("系统异常，预下单状态未知!!!");
	            default:
	                logger.error("不支持的交易状态，交易返回异常!!!");
	                return ServerResponse.createErrorResponse("不支持的交易状态，交易返回异常!!!");
	        }

	    }
	}
	
    //简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            logger.info("body:" + response.getBody());
        }
    }

    @Transactional(propagation=Propagation.REQUIRED)
	@Override
	public ServerResponse aliCallback(Map<String, String> params) {
        Long orderNo = Long.parseLong(params.get("out_trade_no"));
        String tradeNo = params.get("trade_no");
        String tradeStatus = params.get("trade_status");
        Order order = orderMapper.selectByOrderNo(orderNo);
        //如果查询不到回调信息中的订单
        if(order == null){
            return ServerResponse.createErrorResponse("非快乐慕商城的订单,回调忽略");
        }
        //如果订单状态为已支付，说明是支付宝的重复调用
        if(order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()){
            return ServerResponse.createSuccessResponse("支付宝重复调用");
        }
        //如果发现支付宝的回调的支付状态为支付成功，则改变订单状态
        if(Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)){
            order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
            order.setStatus(Const.OrderStatusEnum.PAID.getCode());
            orderMapper.updateByPrimaryKeySelective(order);
            
            //减少库存
            List<OrderItem> items = orderItemMapper.getByOrderNoUserId(orderNo, null);
            for(OrderItem item : items){
            	productMapper.decreaseStock(item.getProductId(), item.getQuantity());
            }
        }

        //创建相应的支付记录
        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);
        payInfoMapper.insert(payInfo);

        return ServerResponse.createSuccessResponse("支付成功");
	}

    /**
     * 查询订单的状态
     */
	@Override
	public ServerResponse queryOrderPayStatus(Integer userId, Long orderNo) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 统计购物车中所有被勾选的商品生产订单
	 */
	@Override
	public ServerResponse createOrder(Integer userId, Integer shippingId) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 根据商品的状态取消或者拒绝取消订单
	 */
	@Override
	public ServerResponse<String> cancel(Integer userId, Long orderNo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServerResponse getOrderCartProduct(Integer userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServerResponse<PageInfo> getOrderList(Integer userId, int pageNum,
			int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServerResponse<PageInfo> manageList(int pageNum, int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServerResponse<OrderVo> manageDetail(Long orderNo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServerResponse<PageInfo> manageSearch(Long orderNo, int pageNum,
			int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServerResponse<String> manageSendGoods(Long orderNo) {
		// TODO Auto-generated method stub
		return null;
	}

}
