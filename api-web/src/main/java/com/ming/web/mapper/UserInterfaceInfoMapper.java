package com.ming.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ming.apiCommon.model.entity.UserInterfaceInfo;
import com.ming.web.model.vo.QueryInfoVO;
import com.ming.web.model.vo.UserInterfaceVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @author 86135
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Mapper
* @createDate 2023-06-25 14:37:03
* @Entity com.ming.web.pojo.UserInterfaceInfo
*/
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {

    /**
     * 查询登录用户的接口信息
     * @param queryInfoVO 返回接口信息
     */
    List<UserInterfaceVO> pageUserInterface(@Param("queryInfo") QueryInfoVO queryInfoVO, @Param("userId") Long userId);


    /**
     * 查询用户一共有多少接口
     * @param queryInfoVO
     * @param userId
     * @return
     */
    Integer countUserInterface(@Param("queryInfo") QueryInfoVO queryInfoVO, @Param("userId") Long userId);
}




