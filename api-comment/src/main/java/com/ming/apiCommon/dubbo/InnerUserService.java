package com.ming.apiCommon.dubbo;

import com.ming.apiCommon.model.entity.User;

public interface InnerUserService {

    /**
     * 通过ak查询用户
     */
    User getUserByAk(String accessKey);

}
