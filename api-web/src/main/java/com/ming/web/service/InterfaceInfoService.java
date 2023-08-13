package com.ming.web.service;

import cn.hutool.http.HttpResponse;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ming.apiCommon.model.entity.InterfaceInfo;
import com.ming.web.model.vo.*;

/**
* @author 86135
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2023-06-25 14:37:03
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    /**
     * 通过id查询具体服务的信息
     * @param id 服务id
     * @return   查询结果
     */
    public InterfaceInfoVO getInterfaceInfoVOById(Integer id);

    /**
     * 管理员获取具体服务信息
     * @param id 接口id
     * @return   查询结果
     */
    public InterfaceInfoVO adminGetInterfaceInfoVOById(Integer id);

    /**
     * 通过id删除接口
     */
    public Integer delInterfaceById(Integer id);

    /**
     * 接口响应校验
     * @param httpResponse 响应接口
     * @return 接口信息
     */
    public boolean responseDetection(HttpResponse httpResponse);

    /**
     * 检查接口是否可以正常调用
     * @param interfaceInfo
     */
    public void checkInterfaceInvoke(InterfaceInfo interfaceInfo);
}
