package com.ming.web.controller;

import com.ming.apiCommon.model.vo.ResponseResult;
import com.ming.web.model.vo.AddInterfaceCountVO;
import com.ming.web.model.vo.PageResult;
import com.ming.web.model.vo.QueryInfoVO;
import com.ming.web.model.vo.UserInterfaceVO;
import com.ming.web.service.UserInterfaceInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 用户接口处理器
 */
@RestController
public class UserInterfaceController {

    @Autowired
    private UserInterfaceInfoService userInterfaceInfoService;

    // /**
    //  * 接口调用统计
    //  */
    // @PostMapping("/invokeCount")
    // public ResponseResult<?> invokeCount(long interfaceInfoId, long userId){
    //     userInterfaceInfoService.invokeCount(interfaceInfoId, userId);
    //     return ResponseResult.ok();
    // }

    /**
     * 查看当前登录用户的接口列表
     */
    @GetMapping("/userInterface/list")
    public ResponseResult<PageResult<UserInterfaceVO>> listLoginInterface(@Valid QueryInfoVO queryInfoVO){
        return ResponseResult.ok(userInterfaceInfoService.pageUserInterface(queryInfoVO));
    }

    /**
     * 添加登录用户某个接口的使用次数
     */
    @PostMapping("/userInterfaceAdd")
    public ResponseResult<?> userInterfaceAdd(@RequestBody AddInterfaceCountVO addInterfaceCountVO){
        userInterfaceInfoService.addInterfaceCount(addInterfaceCountVO);
        return ResponseResult.ok();
    }

}
