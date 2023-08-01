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
     * 查询登录用户的所有接口信息
     * @param queryInfoVO 返回接口信息
     */
    List<UserInterfaceVO> pageAllUserInterface(@Param("queryInfo") QueryInfoVO queryInfoVO, @Param("userId") Long userId);

    /**
     * 查询登录用户已拥有的接口信息
     * @param queryInfoVO 返回接口信息
     */
    List<UserInterfaceVO> pageHaveUserInterface(@Param("queryInfo") QueryInfoVO queryInfoVO, @Param("userId") Long userId);

    /**
     * 查询登录用户调用次数为空的接口
     * @param queryInfoVO 返回接口信息
     */
    List<UserInterfaceVO> pageNoHaveUserInterface(@Param("queryInfo") QueryInfoVO queryInfoVO, @Param("userId") Long userId);

    /**
     * 查询用户一共有多少接口
     * @param queryInfoVO
     * @param userId
     * @return
     */
    Integer countAllUserInterface(@Param("queryInfo") QueryInfoVO queryInfoVO, @Param("userId") Long userId);

    /**
     * 查询用户一共还有多少接口可以调用
     * @param queryInfoVO
     * @param userId
     * @return
     */
    Integer countHaveUserInterface(@Param("queryInfo") QueryInfoVO queryInfoVO, @Param("userId") Long userId);

    /**
     * 查询用户调用完次数的接口有多少个
     * @param queryInfoVO
     * @param userId
     * @return
     */
    Integer countNoHaveUserInterface(@Param("queryInfo") QueryInfoVO queryInfoVO, @Param("userId") Long userId);

}




