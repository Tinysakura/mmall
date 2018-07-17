package com.cfh.mmall.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cfh.mmall.common.ServerResponse;
import com.cfh.mmall.dao.ShippingMapper;
import com.cfh.mmall.pojo.Shipping;
import com.cfh.mmall.service.ShippingService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;

@Service
public class ShippingServiceImpl implements ShippingService{
	@Autowired
	private ShippingMapper shippingMapper;

	@Override
	public ServerResponse<Shipping> add(Integer userId, Shipping shipping) {
		shipping.setUserId(userId);
		
		shippingMapper.insert(shipping);
		
		return ServerResponse.createSuccessResponse(shipping);
	}

	@Override
	public ServerResponse<String> del(Integer userId, Integer shippingId) {
		int effetct = shippingMapper.deleteByUserIdShippingId(userId, shippingId);
		if(effetct > 0){
			return ServerResponse.createSuccessResponse("删除成功");
		}else{
			return ServerResponse.createErrorResponse("删除失败");
		}
	}

	@Override
	public ServerResponse<Shipping> update(Integer userId, Shipping shipping) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServerResponse<Shipping> select(Integer userId, Integer shippingId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServerResponse<PageInfo> list(Integer userId, int pageNum,
			int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		List<Shipping> shippings = shippingMapper.selectByUserId(userId);
		
		PageInfo pageInfo = new PageInfo<>(shippings);
		
		return ServerResponse.createSuccessResponse(pageInfo);
	}
	
	
}
