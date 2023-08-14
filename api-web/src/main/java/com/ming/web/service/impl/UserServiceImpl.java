package com.ming.web.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ming.apiCommon.model.entity.User;
import com.ming.apiCommon.model.enums.ResultCodeEnum;
import com.ming.apiCommon.utils.JwtUtil;
import com.ming.web.constant.MQPrefixConst;
import com.ming.apiCommon.constant.RedisPrefixConst;
import com.ming.web.exception.BusinessException;
import com.ming.web.mapper.InUserRoleMapper;
import com.ming.web.mapper.RoleMapper;
import com.ming.web.mapper.UserMapper;
import com.ming.web.model.dto.email.EmailSendDTO;
import com.ming.web.model.dto.user.UserRegisterRequest;
import com.ming.web.model.entity.InUserRole;
import com.ming.web.model.enums.RoleEnum;
import com.ming.web.model.enums.UploadPathEnum;
import com.ming.web.model.vo.LoginUserInfoVO;
import com.ming.web.model.vo.UserVO;
import com.ming.apiCommon.dubbo.RedisService;
import com.ming.web.service.UserService;
import com.ming.web.strategy.context.UploadStrategyContext;
import com.ming.web.utils.*;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

import static com.ming.web.constant.CommonConstant.SALT;
import static com.ming.apiCommon.constant.RedisPrefixConst.API_REGISTER_CODE;

/**
* @author 86135
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2023-06-25 14:51:54
*/
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private InUserRoleMapper inUserRoleMapper;
    @Resource
    private RoleMapper roleMapper;

    @Resource
    private RedisService redisService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private UploadStrategyContext uploadStrategyContext;


    private static final Integer USER_ACCOUNT_MIN_LEN = 4;
    private static final Integer PASSWORD_MIN_LEN = 6;
    /**
     * 登录有效时间，默认3个小时
     */
    private static final Integer LOGIN_VALID_TIME = 60 * 60 * 3;

    /**
     * 注册验证码有效时间
     */
    private final int CODE_TIME = 15;

    // 用户注册
    @Override
    @Transactional(rollbackFor=Exception.class)
    public long userRegister(UserRegisterRequest userRegisterRequest) {
        checkoutRegister(userRegisterRequest);
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String email = userRegisterRequest.getEmail();
        // TODO 理解下面为什么要加锁
        synchronized (userAccount.intern()) {
            // 账户不能重复
            long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                    .eq(User::getUserAccount, userAccount)
            );
            if (count > 0) {
                throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = EncryptionUtils.md5SaleEncryptString(userPassword);
            // 3. 随机生成分配分配 accessKey, secretKey
            String accessKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(5));
            String secretKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(8));
            // 4. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setEmail(email);
            user.setAccessKey(accessKey);
            user.setSecretKey(secretKey);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            // 给用户分配角色，默认是普通用户
            InUserRole inUserRole = InUserRole.builder()
                    .userId(user.getId().intValue())
                    .roleId(RoleEnum.USER.getRoleId())
                    .build();
            inUserRoleMapper.insert(inUserRole);
            return user.getId();
        }
    }

    // 发送邮件
    @Override
    public void sendRegisterEmail(String email) {
        // 校验格式
        if (!CommonUtils.checkEmail(email)){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "邮箱格式错误");
        }
        // 邮箱不能重复
        Long userCount = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email)
        );
        if (userCount > 0){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "邮箱已经注册");
        }
        synchronized (email.intern()){
            // 获取令牌桶，一个邮箱60秒只能发送一次
            TokenBucket tokenBucket = TokenBucket.getEmailTokenBucket(email);
            // 判断令牌是否足够
            if (!tokenBucket.tryConsume()) {
                throw new BusinessException(ResultCodeEnum.FORBIDDEN_ERROR, "请求频繁");
            }
            // 设置邮件内容
            // 生成随机数
            String randomCode = CommonUtils.getRandomCode();
            String content = "注册验证码是" + randomCode + "有效期为"+CODE_TIME+"分钟，如果不是你发送的请无视";
            EmailSendDTO emailSendDTO = EmailSendDTO.builder()
                    .email(email)
                    .subject("OpenApi接口平台注册")
                    .content(content)
                    .build();
            // 发送到rabbitMq消费者中
            rabbitTemplate.convertAndSend(MQPrefixConst.EMAIL_EXCHANGE, "", emailSendDTO);
            // 将随即数存入redis
            redisService.set(API_REGISTER_CODE+email, randomCode, 60 * CODE_TIME);
        }
    }

    // 用户登录
    @Override
    public LoginUserInfoVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < USER_ACCOUNT_MIN_LEN) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < PASSWORD_MIN_LEN) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "密码错误");
        }
        // 2. 密码加密
        String encryptPassword = EncryptionUtils.md5SaleEncryptString(userPassword);
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("用户登录失败，用户帐户无法匹配用户密码");
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 脱敏
        UserVO userVO = BeanCopyUtils.copyObject(user, UserVO.class);
        // 获取用户权限列表
        List<String> roleList = userMapper.getRoleList(user.getId());
        // 登录成功，生成JWT存入redis,记录用户的登录态
        String token = JwtUtil.createJWT(JSONUtil.toJsonStr(userVO), LOGIN_VALID_TIME * 1000L);
        // 3. 记录用户的登录态
        redisService.set(RedisPrefixConst.LOGIN_USER_ID + userVO.getId(), token, LOGIN_VALID_TIME); // 存3个小时token
        // request.getSession().setAttribute(USER_LOGIN_STATE, userVO);
        return LoginUserInfoVO.builder()
                .user(userVO)
                .token(token)
                .roleList(roleList)
                .build();
    }

    // 获取当前登录用户
    @Override
    public UserVO getLoginUserVO() {
        // 先判断是否已登录
        String token = TokenUtils.getToken();
        UserVO userVO = getUserVOByToken(token);
        if (userVO == null){
            throw new BusinessException(ResultCodeEnum.NOT_LOGIN_ERROR);
        }
        return userVO;
    }

    @Override
    public User getLoginUser() {
        String token = TokenUtils.getToken();
        UserVO userVO = getUserVOByToken(token);
        return this.getById(userVO.getId());
    }

    /**
     * 用户注销
     *
     */
    @Override
    public boolean userLogout(String token) {
        if (StringUtils.isBlank(token)) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        UserVO userVO = getUserVOByToken(token);
        // 从redis中清除用户
        redisService.del(RedisPrefixConst.LOGIN_USER_ID + userVO.getId());
        return true;
    }

    // 通过用户id获取权限列表
    @Override
    public List<String> getRoleListById(Long userId) {
        return userMapper.getRoleList(userId);
    }

    /**
     * 通过token获取登录用户
     */
    private UserVO getUserVOByToken(String token){
        if (StringUtils.isBlank(token)) {
            throw new BusinessException(ResultCodeEnum.NOT_LOGIN_ERROR);
        }
        UserVO userVO = null;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            System.out.println(claims.getSubject());
            userVO = JSONUtil.toBean(claims.getSubject(), UserVO.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"token非法");
        }
        return userVO;
    }

    /**
     * 校验注册用户信息是否正确
     */
    private void checkoutRegister(UserRegisterRequest userRegisterRequest){
        // 提取注册信息
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String email = userRegisterRequest.getEmail();
        String code = userRegisterRequest.getCode();
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < PASSWORD_MIN_LEN || checkPassword.length() < PASSWORD_MIN_LEN) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "两次输入的密码不一致");
        }
        // 校验邮箱验证码是否正确
        Object codeObj = redisService.get(API_REGISTER_CODE + email);
        String getCode = null;
        if (codeObj!=null){
            getCode = redisService.get(API_REGISTER_CODE + email).toString();
        }else {
            throw new BusinessException(ResultCodeEnum.FORBIDDEN_ERROR, "未发送验证码");
        }
        if (!code.equals(getCode)){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "验证码错误");
        }
        // 邮箱不能重复
        Long userCount = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email)
        );
        if (userCount > 0){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "邮箱已经注册");
        }
    }

    // 用户头像上传
    @Override
    public String headUpload(MultipartFile multipartFile) {
        User loginUser = getLoginUser();
        String getPath = uploadStrategyContext.executeUploadStrategyMap(multipartFile, UploadPathEnum.AVATAR.getPath());
        // 更新用户头像信息
        loginUser.setUserAvatar(getPath);
        this.updateById(loginUser);
        return getPath;
    }
}




