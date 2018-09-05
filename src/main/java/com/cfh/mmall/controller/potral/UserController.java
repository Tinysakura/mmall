package com.cfh.mmall.controller.potral;

import io.swagger.annotations.ApiOperation;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cfh.mmall.common.Const;
import com.cfh.mmall.common.ServerResponse;
import com.cfh.mmall.pojo.User;
import com.cfh.mmall.service.UserService;

/**
 * 前台用户接口
 * @author Mr.Chen
 * date: 2018年7月14日 下午2:45:11
 */
@RestController
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;
	
	/**
	 * 用户登录的接口
	 * @param session
	 * @return
	 */
	@ApiOperation(value="登录",notes="登录")
	@RequestMapping(value="/login",method=RequestMethod.POST)
	public ServerResponse<User> login(@RequestBody User user,HttpSession session){
		ServerResponse<User> response = userService.loging(user.getUsername(), user.getPassword());
		
		if(response.getStatus() == ServerResponse.ResponseCode.SUCCESS){
			session.setAttribute(Const.CURRENT_USER, response.getData());
		}
		
		return response;
	}
	
	/**
	 * 用户注册的接口
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/rigist",method=RequestMethod.POST)
	public ServerResponse<String> rigist(@RequestBody User user){
		return userService.rigist(user);
	}
	
	/**
	 * 当前用户下线的接口 
	 * @return
	 */
	@RequestMapping(value="/logout",method=RequestMethod.GET)
	public ServerResponse<String> logout(HttpSession session){
		return null;
	}
	
	/**
	 * 未登录情况下忘记密码
	 * @param userName
	 * @return
	 */
	@RequestMapping(value="/forget_password/{userName}",method=RequestMethod.GET)
	@ApiOperation(value="查询密保",notes="查询注册时的密保问题")
	public ServerResponse<String> forget(@PathVariable String userName){
		return userService.queryQuestion(userName);
	}
	
	/**
	 * 提交问题的验证答案
	 * 若通过验证返回值中将有一个token码用于防止横向越权
	 */
	@ApiOperation(value="验证",notes="验证密保问题")
	@RequestMapping(value="/answer_question",method=RequestMethod.POST)
	public ServerResponse<String> answer(@RequestBody User user){
		return userService.answerQuestion(user.getUsername(), user.getAnswer());
	}
	
	/**
	 * 忘记密码的情况下修改密码
	 * 为了防止横向越权需要一个token做验证
	 */
	@ApiOperation(value="修改密码",notes="在忘记密码的情况下修改密码")
	@RequestMapping(value="/modify_password",method=RequestMethod.POST)
	public ServerResponse<String> modifyPassword(String userName,String newPassword,String token){
		return userService.modifyPasswordWithoutOld(userName, newPassword, token);
	}
}
