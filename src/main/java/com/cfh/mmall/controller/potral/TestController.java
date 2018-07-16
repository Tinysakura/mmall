package com.cfh.mmall.controller.potral;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cfh.mmall.pojo.Product;
import com.cfh.mmall.service.TestService;

@RestController
public class TestController {
	@Autowired
	private TestService testService;

	@RequestMapping("/test")
	public Product test(){
		return testService.test(26);
	}
}
