package com.ming.apiCommon.dubbo;

import com.ming.apiCommon.model.entity.InterfaceInfo;

/**
 * 接口请求暴露信息
 */
public interface InnerInterfaceInfoService {

    /**
     * 通过url和method获取接口信息
     */
    InterfaceInfo getInterfaceInfo(String path, String method);

}
