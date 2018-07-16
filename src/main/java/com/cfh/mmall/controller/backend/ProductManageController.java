package com.cfh.mmall.controller.backend;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.cfh.mmall.common.Const;
import com.cfh.mmall.common.ServerResponse;
import com.cfh.mmall.pojo.Product;
import com.cfh.mmall.pojo.User;
import com.cfh.mmall.service.FileService;
import com.cfh.mmall.service.ProductService;
import com.cfh.mmall.service.UserService;
import com.cfh.mmall.util.PropertiesUtil;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;

/**
 * Created by geely
 */

@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    private UserService iUserService;
    @Autowired
    private ProductService iProductService;
    @Autowired
    private FileService fileService;

    @RequestMapping("/save")
    @ResponseBody
    public ServerResponse<String> productSave(HttpSession session, Product product){
        return null;
    }

    @RequestMapping("/search")
    @ResponseBody
    public ServerResponse<PageInfo> productSearch(HttpSession session,String productName,Integer productId, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,@RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
    	return null;
    }

    @RequestMapping("/upload")
    @ResponseBody
    public ServerResponse<Map<String, String>> upload(HttpSession session,@RequestParam(value = "upload_file",required = false) MultipartFile file,HttpServletRequest request){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        //添加登录判断与权限判断
//        if(user == null){
//        	return ServerResponse.createErrorResponse(ServerResponse.ResponseCode.NEED_LOGING,"未登录");
//        }
        
        //相当于在项目的web-inf目录下创建了一个upload文件夹
        String path = request.getSession().getServletContext().getRealPath("upload");
        String targetFileName = fileService.upload(file,path);
        String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;

        //将上传到图片服务器的uri与url反馈
        Map<String,String> fileMap = Maps.newHashMap();
        fileMap.put("uri",targetFileName);
        fileMap.put("url",url);
        return ServerResponse.createSuccessResponse(fileMap);
    }

    /**
     * 富文本中文件的上传，与普通上传的区别是返回值的格式固定且需要修改响应头
     */
    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    public Map<String,Object> richtextImgUpload(HttpSession session, @RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> resultMap = Maps.newHashMap();
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            resultMap.put("success",false);
            resultMap.put("msg","请登录管理员");
            return resultMap;
        }
        //富文本中对于返回值有自己的要求,我们使用是simditor所以按照simditor的要求进行返回
//        {
//            "success": true/false,
//                "msg": "error message", # optional
//            "file_path": "[real file path]"
//        }
        String path = request.getSession().getServletContext().getRealPath("upload");
        String targetFileName = fileService.upload(file,path);
        if(StringUtils.isBlank(targetFileName)){
            resultMap.put("success",false);
            resultMap.put("msg","上传失败");
            return resultMap;
        }
        String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
        resultMap.put("success",true);
        resultMap.put("msg","上传成功");
        resultMap.put("file_path",url);
        response.addHeader("Access-Control-Allow-Headers","X-File-Name");
        return resultMap;
    }
}
