package com.cfh.mmall.service;

import com.cfh.mmall.common.ServerResponse;
import com.cfh.mmall.pojo.User;

public interface UserService {
	public ServerResponse<User> loging(String userName,String password);
	
	public ServerResponse<String> rigist(User user);
	
	public ServerResponse<String> queryQuestion(String userName);
	
	public ServerResponse<String> answerQuestion(String username,String answer);
	
	public ServerResponse<String> modifyPasswordWithoutOld(String username,String newPassword,String token);
}
