package com.ming.web.controller;

import com.ming.apiCommon.model.vo.ResponseResult;
import com.ming.web.annotation.AuthCheck;
import com.ming.web.model.dto.charging.ChargingInfoDTO;
import com.ming.web.model.vo.ChangeChargingVO;
import com.ming.web.model.vo.PageResult;
import com.ming.web.model.vo.QueryInfoVO;
import com.ming.web.service.InterfaceChargingService;
import com.ming.web.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 调用价格控制器
*/
@RestController
@RequestMapping("/interfaceCharging")
public class InterfaceChargingController {

    @Resource
    private UserService userService;

    @Resource
    private InterfaceChargingService interfaceChargingService;

    /**
     * 分页查询接口计费
     */
    @AuthCheck(anyRole = {"admin", "test"})
    @GetMapping("/list")
    public ResponseResult<PageResult<ChargingInfoDTO>> list(QueryInfoVO queryInfoVO){
        return ResponseResult.ok(interfaceChargingService.listInterfaceCharging(queryInfoVO));
    }

    /**
     * 修改接口计费
     */
    @AuthCheck(mustRole = "admin")
    @PostMapping("/changeCharging")
    public ResponseResult<String> changeCharging(@Valid @RequestBody ChangeChargingVO changeChargingVO){
        interfaceChargingService.changeCharging(changeChargingVO);
        return ResponseResult.ok();
    }

    /**
     * 通过接口id获取接口信息
     */
    @GetMapping("/getByInterfaceId")
    public ResponseResult<ChargingInfoDTO> getByInterfaceId(Long interfaceId){
        return ResponseResult.ok(interfaceChargingService.getChargingInfoByInterfaceId(interfaceId));
    }
}




