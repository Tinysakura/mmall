package com.cfh.mmall.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cfh.mmall.dao.ProductMapper;
import com.cfh.mmall.pojo.Product;
import com.cfh.mmall.service.TestService;

@Service
public class TestServiceImpl implements TestService{
	@Autowired
	private ProductMapper productMapper;

	@Override
	public Product test(Integer id) {
		return productMapper.selectByPrimaryKey(id);
	}
	
	

}
