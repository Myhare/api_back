package com.ming.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ming.apiCommon.model.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @author 86135
* @description 针对表【user(用户)】的数据库操作Mapper
* @createDate 2023-06-25 14:37:03
* @Entity com.ming.web.pojo.User
*/
public interface UserMapper extends BaseMapper<User> {

    /**
     * 通过id获取用户权限列表
     */
    List<String> getRoleList(@Param("id") Long id);

}




