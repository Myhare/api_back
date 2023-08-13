package com.ming.web.service.dubbo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ming.apiCommon.dubbo.ApiBackendService;
import com.ming.apiCommon.model.entity.UserInterfaceInfo;
import com.ming.apiCommon.utils.CommonUtil;
import com.ming.web.mapper.UserInterfaceInfoMapper;
import com.ming.web.model.entity.InterfaceCharging;
import com.ming.web.service.InterfaceChargingService;
import com.ming.web.service.UserInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

import java.util.Objects;

import static com.ming.web.constant.CommonConstant.INFINITE;

/**
 * api接口远程调用服务集合
 */
@DubboService
public class InnerApiBackendServiceImpl implements ApiBackendService {
    @Resource
    private InterfaceChargingService interfaceChargingService;

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Override
    public boolean rollBackStock(Long interfaceId, Integer num) {
        InterfaceCharging interfaceCharging = interfaceChargingService.getOne(new LambdaQueryWrapper<InterfaceCharging>()
                .eq(InterfaceCharging::getInterfaceId, interfaceId)
        );
        // 判断库存是不是一个正常的数字,如果不是，说明库存设置成了无限制
        String availablePieces = interfaceCharging.getAvailablePieces();
        if (!CommonUtil.isInteger(availablePieces)){
            return true;
        }
        // 执行回滚库存
        return interfaceChargingService.update(interfaceCharging, new LambdaUpdateWrapper<InterfaceCharging>()
                .eq(InterfaceCharging::getInterfaceId, interfaceCharging.getInterfaceId())
                .setSql("availablePieces = availablePieces + " + num)
        );
    }

    // 通过接口id获取接口库存
    @Override
    public String getStockByInterfaceId(Long interfaceId) {
        InterfaceCharging interfaceCharging = interfaceChargingService.getOne(new LambdaQueryWrapper<InterfaceCharging>()
                .eq(InterfaceCharging::getInterfaceId, interfaceId)
        );
        if (interfaceCharging == null){
            return INFINITE;
        }
        return interfaceCharging.getAvailablePieces();
    }

    // 减少库存
    @Override
    public boolean reduceStock(Long interfaceId, Integer count) {
        InterfaceCharging interfaceCharging = interfaceChargingService.getOne(new LambdaQueryWrapper<InterfaceCharging>()
                .eq(InterfaceCharging::getInterfaceId, interfaceId)
        );
        // 说明接口调用次数已经被别人调用的次数不足了
        if (Integer.parseInt(interfaceCharging.getAvailablePieces()) - count < 0){
            return false;
        }
        return interfaceChargingService.update(interfaceCharging, new LambdaUpdateWrapper<InterfaceCharging>()
                .eq(InterfaceCharging::getInterfaceId, interfaceCharging.getInterfaceId())
                .setSql("availablePieces = availablePieces - " + count)
        );
    }

    // 添加接口调用
    @Override
    public boolean addInvokeCount(Long userId, Long interfaceId, Integer count) {
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.getOne(new LambdaQueryWrapper<UserInterfaceInfo>()
                .eq(UserInterfaceInfo::getUserId, userId)
                .eq(UserInterfaceInfo::getInterfaceInfoId, interfaceId)
        );
        if (Objects.isNull(userInterfaceInfo)){
            // 添加一个新的数据到数据库中
            userInterfaceInfo = UserInterfaceInfo.builder()
                    .userId(userId)
                    .interfaceInfoId(interfaceId)
                    .leftNum(count)
                    .build();
            int insertCount = userInterfaceInfoMapper.insert(userInterfaceInfo);
            return insertCount > 0;
        }else {
            // 在原来的数据上进行添加次数
            // 获取剩余调用次数
            int updateCount = userInterfaceInfoMapper.update(userInterfaceInfo, new LambdaUpdateWrapper<UserInterfaceInfo>()
                    .eq(UserInterfaceInfo::getId, userInterfaceInfo.getId())
                    .setSql("leftNum = leftNum + " + count)
            );
            return updateCount > 0;
        }
    }
}
