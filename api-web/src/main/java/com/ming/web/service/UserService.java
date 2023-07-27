package com.ming.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ming.apiCommon.model.entity.User;
import com.ming.web.model.dto.user.UserRegisterRequest;
import com.ming.web.model.vo.LoginUserInfoVO;
import com.ming.web.model.vo.UserVO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author ming
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2023-06-25 14:37:03
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userRegisterRequest 注册信息
     * @return 新用户 id
     */
    long userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 发送邮件
     */
    void sendRegisterEmail(String email);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    LoginUserInfoVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 通过userid获取权限列表
     */
    List<String> getRoleListById(Long userId);

    /**
     * 获取当前登录用户
     *
     * @return
     */
    UserVO getLoginUserVO();

    /**
     * 获取用户登录密钥
     * @return
     */
    User getLoginUser();

    /**
     * 用户注销
     *
     * @param token
     * @return
     */
    boolean userLogout(String token);

    /**
     * 用户头像上传
     * @return 访问路径
     */
    String headUpload(MultipartFile multipartFile);
}
