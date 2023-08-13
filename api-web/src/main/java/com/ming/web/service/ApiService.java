package com.ming.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ming.apiCommon.model.entity.User;
import com.ming.web.model.dto.user.UserRegisterRequest;
import com.ming.web.model.vo.BackStatisticsDTO;
import com.ming.web.model.vo.LoginUserInfoVO;
import com.ming.web.model.vo.UserVO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 后天处理数据服务
 */
public interface ApiService {

    /**
     * 获取后台统计数据
     */
    BackStatisticsDTO getBackStatistics();

}
