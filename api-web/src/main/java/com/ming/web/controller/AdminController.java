package com.ming.web.controller;

import com.ming.apiCommon.model.vo.ResponseResult;
import com.ming.web.model.vo.BackStatisticsDTO;
import com.ming.web.service.ApiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class AdminController {

    @Resource
    private ApiService apiService;

    // 获取后台统计信息
    @GetMapping("/admin/website/config")
    public ResponseResult<BackStatisticsDTO> getBlogStatisticalInfo(){
        return ResponseResult.ok(apiService.getBackStatistics());
    }

}
