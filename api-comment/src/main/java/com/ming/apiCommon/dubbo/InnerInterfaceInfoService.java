package com.ming.apiCommon.dubbo;

import com.ming.apiCommon.model.entity.InterfaceInfo;

import java.util.List;

/**
 * 接口请求暴露信息
 */
public interface InnerInterfaceInfoService {

    /**
     * 通过url和method获取接口信息
     */
    InterfaceInfo getInterfaceInfo(String path, String method);

    /**
     * 通过id获取接口信息
     */
    InterfaceInfo getInterfaceById(Long interfaceId);

    /**
     * 通过id列表查看接口列表
     */
    List<InterfaceInfo> listInterfaceByIdList(List<Long> idList);
}
