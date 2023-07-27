package com.ming.apiCommon.dubbo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ming.apiCommon.model.entity.User;
import com.ming.apiCommon.model.entity.UserInterfaceInfo;

public interface InnerUserInterfaceService {

    /**
     * 查询用户是否有当前接口的调用次数
     * @param userId 查询的用户id
     * @param interfaceInfoId 接口id
     */
    UserInterfaceInfo getUserInterface(Long userId, Long interfaceInfoId);

    /**
     * 接口调用统计
     * @param interfaceInfoId 接口id
     * @param userId  用户id
     */
    void invokeCount(long interfaceInfoId, long userId);

}
