package com.ming.web.service.dubbo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ming.apiCommon.dubbo.InnerUserInterfaceService;
import com.ming.apiCommon.model.entity.User;
import com.ming.apiCommon.model.entity.UserInterfaceInfo;
import com.ming.web.service.UserInterfaceInfoService;
import com.ming.web.service.UserService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * 注册进服务中的接口(用于其他服务远程调用)
 * 用户和接口相关服务
 */
@DubboService
public class InnerUserInterfaceServiceImpl implements InnerUserInterfaceService {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    // 查询用户id是否有当前接口
    @Override
    public UserInterfaceInfo getUserInterface(Long userId, Long interfaceInfoId) {
        return userInterfaceInfoService.getOne(new LambdaQueryWrapper<UserInterfaceInfo>()
                .eq(UserInterfaceInfo::getUserId, userId)
                .eq(UserInterfaceInfo::getInterfaceInfoId, interfaceInfoId)
        );
    }

    /**
     * 接口调用统计
     */
    @Override
    public void invokeCount(long interfaceInfoId, long userId){
        userInterfaceInfoService.invokeCount(interfaceInfoId, userId);
    }

}
