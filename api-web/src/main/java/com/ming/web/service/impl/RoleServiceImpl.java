package com.ming.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ming.web.model.entity.Role;
import com.ming.web.service.RoleService;
import com.ming.web.mapper.RoleMapper;
import org.springframework.stereotype.Service;

/**
* @author 86135
* @description 针对表【role】的数据库操作Service实现
* @createDate 2023-07-12 09:23:47
*/
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role>
    implements RoleService{

}




