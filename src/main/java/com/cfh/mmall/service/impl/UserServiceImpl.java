package com.cfh.mmall.service.impl;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cfh.mmall.common.ServerResponse;
import com.cfh.mmall.common.TokenCache;
import com.cfh.mmall.dao.UserMapper;
import com.cfh.mmall.pojo.User;
import com.cfh.mmall.service.UserService;
import com.cfh.mmall.util.MD5Util;

@Service
public class UserServiceImpl implements UserService{
	@Autowired
	private UserMapper userMapper;
    private static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Override
	public ServerResponse<User> loging(String userName, String password) {
		User user = userMapper.selectByUserName(userName);
		
		if(user == null){
			return ServerResponse.createErrorResponse("user unexsist");
		}
		
		if(user.getPassword().equals(MD5Util.MD5EncodeUtf8(password))){
			return ServerResponse.createSuccessResponse("login success", user);
		}else{
			return ServerResponse.createErrorResponse("password error");
		}
	}

	@Override
	public ServerResponse<String> rigist(User user) {
		if(userMapper.userNameExsist(user.getUsername()) > 0){
			return ServerResponse.createErrorResponse("user is exsist");
		}
		if(userMapper.emailExsist(user.getEmail()) > 0){
			return ServerResponse.createErrorResponse("email is exsist");
		}
		
		//对密码进行MD5加密
		String md5Password = MD5Util.MD5EncodeUtf8(user.getPassword());
		user.setPassword(md5Password);
		
		userMapper.insertSelective(user);
		
		user = userMapper.selectByUserName(user.getUsername());
		if(user == null){
			return ServerResponse.createErrorResponse("rigist failure");
		}else{
			return ServerResponse.createSuccessResponse("rigist success");
		}
	}

	@Override
	public ServerResponse<String> queryQuestion(String userName) {
		String question = userMapper.queryQuestion(userName);
		
		if(question == null){
			return ServerResponse.createErrorResponse("找不到该用户提出得问题");
		}else if(StringUtils.isBlank(question)){
			return ServerResponse.createErrorResponse("该用户并没有设置密保问题");
		}else{
			return ServerResponse.createSuccessResponse(question);
		}
	}

	@Override
	public ServerResponse<String> answerQuestion(String username, String answer) {
		String trueAnswer = userMapper.queryAnswer(username);
	
		if(StringUtils.isEmpty(trueAnswer)){
			return ServerResponse.createSuccessResponse("找不到密保问题的答案");
		}else if(!StringUtils.equals(trueAnswer, answer)){
			return ServerResponse.createErrorResponse("回答错误");
		}else{
			//随机生成一个token放入缓存，用于修改密码时的验证
			String token = UUID.randomUUID().toString();
			logger.info("token:"+token);
			TokenCache.setKey(TokenCache.TOKEN_PREFIX+username, token);
			return ServerResponse.createSuccessResponse("回答正确");
		}
	}

	@Override
	public ServerResponse<String> modifyPasswordWithoutOld(String userName,String newPassword,
			String token) {
		if(StringUtils.isBlank(userName)){
			return ServerResponse.createSuccessResponse("不合法的用户");
		}else if(StringUtils.isBlank(newPassword)){
			return ServerResponse.createSuccessResponse("不合法的密码");
		}else if(!TokenCache.getKey(TokenCache.TOKEN_PREFIX+userName).equals(token)){
			return ServerResponse.createSuccessResponse("对不起你没有修改权限");
		}else{
			userMapper.modifyPassword(userName, MD5Util.MD5EncodeUtf8(newPassword));
			return ServerResponse.createSuccessResponse("修改成功");
		}
	}

}
