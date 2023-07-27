package com.ming.web.service.dubbo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ming.apiCommon.dubbo.InnerUserService;
import com.ming.apiCommon.model.entity.User;
import com.ming.web.service.UserService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnerUserServiceImpl implements InnerUserService {

    @Resource
    private UserService userService;

    /**
     * 通过ak查询用户
     */
    @Override
    public User getUserByAk(String accessKey){
        return userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getAccessKey, accessKey)
        );
    }
}
