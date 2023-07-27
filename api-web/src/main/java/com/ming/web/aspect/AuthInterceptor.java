package com.ming.web.aspect;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ming.apiCommon.model.enums.ResultCodeEnum;
import com.ming.web.annotation.AuthCheck;
import com.ming.web.exception.BusinessException;
import com.ming.web.model.vo.UserVO;
import com.ming.web.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限校验 AOP
 *
 * @author yupi
 */
@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;

    /**
     * 执行拦截
     *
     * @param joinPoint
     * @param authCheck
     * @return
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        // 获取需要一部分的权限
        List<String> anyRole = Arrays.stream(authCheck.anyRole()).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        // 获取一定需要的一个权限
        String mustRole = authCheck.mustRole();
        // 获取当前的request对象
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 当前登录用户
        UserVO userVO = userService.getLoginUserVO();
        // 角色列表
        List<String> roleList = userService.getRoleListById(userVO.getId());
        // 获取登录用户的所有角色
        // 拥有任意权限即通过
        if (CollectionUtils.isNotEmpty(anyRole)) {
            // 如果没有交集，说明一个权限都没有，直接抛出异常
            ArrayList<String> tempList = new ArrayList<String>(anyRole);
            tempList.retainAll(anyRole);  // tempList会删除不相交的集合
            if (tempList.isEmpty()){
                // tempList为空说明没有相交，一个权限都没有
                throw new BusinessException(ResultCodeEnum.NO_AUTH_ERROR);
            }
            // String userRole = user.getUserRole();
            // if (!anyRole.contains(userRole)) {
            //     throw new BusinessException(ResultCodeEnum.NO_AUTH_ERROR);
            // }
        }
        // 必须有所有权限才通过
        if (StringUtils.isNotBlank(mustRole)) {
            // 不存在这个权限，直接抛出异常
            if (!roleList.contains(mustRole)) {
                throw new BusinessException(ResultCodeEnum.NO_AUTH_ERROR);
            }
            // String userRole = user.getUserRole();
            // if (!mustRole.equals(userRole)) {
            //     throw new BusinessException(ResultCodeEnum.NO_AUTH_ERROR);
            // }
        }
        // 通过权限校验，放行
        return joinPoint.proceed();
    }
}

