package com.ming.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ming.apiCommon.model.entity.UserInterfaceInfo;
import com.ming.web.model.vo.AddInterfaceCountVO;
import com.ming.web.model.vo.PageResult;
import com.ming.web.model.vo.QueryInfoVO;
import com.ming.web.model.vo.UserInterfaceVO;

/**
* @author 86135
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service
* @createDate 2023-06-25 14:37:03
*/
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {

    /**
     * 校验接口
     * @param userInterfaceInfo
     * @param add
     */
    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean isAdd);

    /**
     * 调用接口统计
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean invokeCount(long interfaceInfoId, long userId);

    /**
     * 查询登录用户的接口列表
     * @param queryInfoVO 查询条件
     * @return            查询结果
     */
    public PageResult<UserInterfaceVO> pageUserInterface(QueryInfoVO queryInfoVO);

    /**
     * 添加当前登录用户的接口调用次数
     * @param addInterfaceCountVO
     */
    void addInterfaceCount(AddInterfaceCountVO addInterfaceCountVO);
}
