package com.ming.web.service;

import com.ming.web.model.dto.charging.ChargingInfoDTO;
import com.ming.web.model.entity.InterfaceCharging;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ming.web.model.vo.ChangeChargingVO;
import com.ming.web.model.vo.PageResult;
import com.ming.web.model.vo.QueryInfoVO;

/**
* @author 86135
* @description 针对表【interface_charging】的数据库操作Service
* @createDate 2023-08-08 14:37:31
*/
public interface InterfaceChargingService extends IService<InterfaceCharging> {

    /**
     * 分页获取接口计费信息
     */
    PageResult<ChargingInfoDTO> listInterfaceCharging(QueryInfoVO queryInfoVO);

    /**
     * 通过接口id获取接口信息
     */
    ChargingInfoDTO getChargingInfoByInterfaceId(Long interfaceId);

    /**
     * 修改库存
     * @param changeChargingVO
     */
    void changeCharging(ChangeChargingVO changeChargingVO);
}
