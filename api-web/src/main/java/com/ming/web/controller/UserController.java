package com.ming.web.controller;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.ming.apiCommon.model.entity.User;
import com.ming.web.common.DeleteRequest;
import com.ming.apiCommon.model.enums.ResultCodeEnum;
import com.ming.web.constant.CommonConstant;
import com.ming.web.exception.BusinessException;
import com.ming.web.model.dto.user.*;
import com.ming.apiCommon.model.vo.ResponseResult;
import com.ming.web.model.vo.AkSkVO;
import com.ming.web.model.vo.LoginUserInfoVO;
import com.ming.web.model.vo.PasswordRequest;
import com.ming.web.model.vo.UserVO;
import com.ming.web.service.UserService;
import com.ming.web.utils.EncryptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static com.ming.web.constant.CommonConstant.SALT;

/**
 * 用户接口
 *
 * @author yupi
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    // region 登录相关

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public ResponseResult<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String email = userRegisterRequest.getEmail();
        String code = userRegisterRequest.getCode();
        // 只要用户名、密码、确认密码有问题，直接报错
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, email, code)) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        long result = userService.userRegister(userRegisterRequest);
        return ResponseResult.ok(result);
    }

    /**
     * 发送注册邮件
     */
    @GetMapping("/registerEmail")
    private ResponseResult<String> sendRegisterEmail(String email){
        userService.sendRegisterEmail(email);
        return ResponseResult.ok("发送成功");
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public ResponseResult<LoginUserInfoVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        // 对参数进行校验
        if (userLoginRequest == null) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        LoginUserInfoVO loginUserInfoVO = userService.userLogin(userAccount, userPassword, request);
        return ResponseResult.ok(loginUserInfoVO);
    }

    /**
     * 用户注销
     */
    @PostMapping("/logout")
    public ResponseResult<Boolean> userLogout(HttpServletRequest request) {
        String token = request.getHeader(CommonConstant.TOKEN_HEADER);
        if (StringUtils.isBlank(token)) {
            return ResponseResult.ok(true);
        }
        boolean result = userService.userLogout(token);
        return ResponseResult.ok(result);
    }

    /**
     * 获取当前登录用户
     *
     * @return
     */
    @GetMapping("/get/login")
    public ResponseResult<UserVO> getLoginUser() {
        UserVO userVO = userService.getLoginUserVO();
        return ResponseResult.ok(userVO);
    }

    /**
     * 通过密码获取用户的ak和sk
     */
    @GetMapping("/getAkSk")
    public ResponseResult<AkSkVO> getAkSk(String password){
        User loginUser = userService.getLoginUser();
        // 校验密码是否正确
        String encryptPassword = EncryptionUtils.md5SaleEncryptString(password);
        if (!encryptPassword.equals(loginUser.getUserPassword())){
            throw new BusinessException(ResultCodeEnum.NO_AUTH_ERROR, "密码错误");
        }
        AkSkVO akSkVO = AkSkVO.builder()
                .accessKey(loginUser.getAccessKey())
                .secretKey(loginUser.getSecretKey())
                .build();
        return ResponseResult.ok(akSkVO);
    }

    /**
     * 重置用户的ak和sk
     */
    @PostMapping("/resetAkSk")
    public ResponseResult<?> resetAkSk(@RequestBody PasswordRequest passwordRequest){
        User loginUser = userService.getLoginUser();
        // 校验密码是否正确
        String encryptPassword = EncryptionUtils.md5SaleEncryptString(passwordRequest.getPassword());
        if (!encryptPassword.equals(loginUser.getUserPassword())){
            throw new BusinessException(ResultCodeEnum.NO_AUTH_ERROR, "密码错误");
        }
        // 3. 随机生成分配分配 accessKey, secretKey
        String accessKey = DigestUtil.md5Hex(SALT + loginUser.getUserAccount() + RandomUtil.randomNumbers(5));
        String secretKey = DigestUtil.md5Hex(SALT + loginUser.getUserAccount() + RandomUtil.randomNumbers(8));
        User nowUser = User.builder()
                .id(loginUser.getId())
                .accessKey(accessKey)
                .secretKey(secretKey)
                .build();
        userService.updateById(nowUser);
        // 重新生成ak和sk
        return ResponseResult.ok();
    }

    /**
     * 用户头像上传
     */
    @PostMapping("/avatar")
    public ResponseResult<String> headFileUpload(@RequestBody MultipartFile multipartFile){
        return ResponseResult.ok(userService.headUpload(multipartFile));
    }

    // region 增删改查

    /**
     * 创建用户
     *
     * @param userAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public ResponseResult<Long> addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request) {
        if (userAddRequest == null) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        boolean result = userService.save(user);
        if (!result) {
            throw new BusinessException(ResultCodeEnum.OPERATION_ERROR);
        }
        return ResponseResult.ok(user.getId());
    }

    /**
     * 删除用户
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public ResponseResult<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        boolean b = userService.removeById(deleteRequest.getId());
        return ResponseResult.ok(b);
    }

    /**
     * 更新用户
     *
     * @param userUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public ResponseResult<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        return ResponseResult.ok(result);
    }

    /**
     * 根据 id 获取用户
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    public ResponseResult<UserVO> getUserById(int id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return ResponseResult.ok(userVO);
    }

    /**
     * 获取用户列表
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list")
    public ResponseResult<List<UserVO>> listUser(UserQueryRequest userQueryRequest, HttpServletRequest request) {
        User userQuery = new User();
        if (userQueryRequest != null) {
            BeanUtils.copyProperties(userQueryRequest, userQuery);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>(userQuery);
        List<User> userList = userService.list(queryWrapper);
        List<UserVO> userVOList = userList.stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        return ResponseResult.ok(userVOList);
    }

    /**
     * 分页获取用户列表
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public ResponseResult<Page<UserVO>> listUserByPage(UserQueryRequest userQueryRequest, HttpServletRequest request) {
        long current = 1;
        long size = 10;
        User userQuery = new User();
        if (userQueryRequest != null) {
            BeanUtils.copyProperties(userQueryRequest, userQuery);
            current = userQueryRequest.getCurrent();
            size = userQueryRequest.getPageSize();
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>(userQuery);
        Page<User> userPage = userService.page(new Page<>(current, size), queryWrapper);
        Page<UserVO> userVOPage = new PageDTO<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        List<UserVO> userVOList = userPage.getRecords().stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        userVOPage.setRecords(userVOList);
        return ResponseResult.ok(userVOPage);
    }

    // endregion
}
