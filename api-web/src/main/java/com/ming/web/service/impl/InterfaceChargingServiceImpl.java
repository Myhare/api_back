package com.ming.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ming.apiCommon.model.entity.InterfaceInfo;
import com.ming.apiCommon.model.entity.User;
import com.ming.apiCommon.model.enums.ResultCodeEnum;
import com.ming.web.exception.BusinessException;
import com.ming.web.mapper.InterfaceChargingMapper;
import com.ming.web.mapper.InterfaceInfoMapper;
import com.ming.web.model.dto.charging.ChargingInfoDTO;
import com.ming.web.model.entity.InterfaceCharging;
import com.ming.web.model.vo.ChangeChargingVO;
import com.ming.web.model.vo.PageResult;
import com.ming.web.model.vo.QueryInfoVO;
import com.ming.web.service.InterfaceChargingService;
import com.ming.web.service.UserService;
import com.ming.web.utils.EncryptionUtils;
import com.ming.web.utils.PageUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.ming.web.constant.CommonConstant.DOUBLE_NULL;
import static com.ming.web.constant.CommonConstant.INFINITE;

/**
 * 接口计费服务
 */
@Service
public class InterfaceChargingServiceImpl extends ServiceImpl<InterfaceChargingMapper, InterfaceCharging>
        implements InterfaceChargingService{


    @Resource
    private InterfaceChargingMapper interfaceChargingMapper;

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    @Resource
    private UserService userService;

    @Override
    public PageResult<ChargingInfoDTO> listInterfaceCharging(QueryInfoVO queryInfoVO) {
        Page<InterfaceInfo> page = new Page<>(PageUtils.getLimitCurrent(), PageUtils.getSize());
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoMapper.selectPage(page, new LambdaQueryWrapper<InterfaceInfo>()
                .like(StringUtils.hasLength(queryInfoVO.getKeyword()), InterfaceInfo::getName, queryInfoVO.getKeyword())
        );
        List<InterfaceInfo> interfaceInfoList = interfaceInfoPage.getRecords();
        List<Long> interfaceIdList = interfaceInfoList.stream()
                .map(InterfaceInfo::getId)
                .collect(Collectors.toList());
        // 通过接口id列表查询接口的计价
        List<InterfaceCharging> chargingList = interfaceChargingMapper.selectList(new LambdaQueryWrapper<InterfaceCharging>()
                .in(InterfaceCharging::getInterfaceId, interfaceIdList)
        );
        Map<Long, List<InterfaceCharging>> interfaceIdMap = chargingList.stream().collect(Collectors.groupingBy(InterfaceCharging::getInterfaceId));

        // 组合数据
        List<ChargingInfoDTO> chargingInfoList = interfaceInfoList.stream().map(interfaceInfo -> {
            return ChargingInfoDTO.builder()
                    .interfaceId(interfaceInfo.getId())
                    .name(interfaceInfo.getName())
                    .charging(interfaceIdMap.get(interfaceInfo.getId()) != null ? interfaceIdMap.get(interfaceInfo.getId()).get(0).getCharging():DOUBLE_NULL)
                    .status(interfaceInfo.getStatus())
                    .availablePieces(interfaceIdMap.get(interfaceInfo.getId()) != null ? interfaceIdMap.get(interfaceInfo.getId()).get(0).getAvailablePieces():INFINITE)
                    .createTime(interfaceInfo.getCreateTime())
                    .build();
        }).collect(Collectors.toList());

        return new PageResult<>(chargingInfoList, (int)interfaceInfoPage.getTotal());
    }

    // 通过接口id获取接口计费
    @Override
    public ChargingInfoDTO getChargingInfoByInterfaceId(Long interfaceId) {
        InterfaceInfo interfaceInfo = interfaceInfoMapper.selectById(interfaceId);
        InterfaceCharging interfaceCharging = interfaceChargingMapper.selectOne(new LambdaQueryWrapper<InterfaceCharging>()
                .eq(InterfaceCharging::getInterfaceId, interfaceId)
        );
        if (interfaceCharging == null){
            // 说明这个接口没有计费规则
            return ChargingInfoDTO.builder()
                    .interfaceId(interfaceId)
                    .name(interfaceInfo.getName())
                    .charging(DOUBLE_NULL)
                    .status(interfaceInfo.getStatus())
                    .availablePieces(INFINITE)
                    .build();
        }else {
            return ChargingInfoDTO.builder()
                    .interfaceId(interfaceId)
                    .name(interfaceInfo.getName())
                    .charging(interfaceCharging.getCharging())
                    .status(interfaceInfo.getStatus())
                    .availablePieces(interfaceCharging.getAvailablePieces())
                    .build();
        }
    }

    // 修改计费
    @Override
    public void changeCharging(ChangeChargingVO changeChargingVO) {
        if (changeChargingVO.getInterfaceId() == null){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "接口id不能为空");
        }
        if (changeChargingVO.getChangeCharging() == null){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "接口定价不能为空");
        }
        // 判断定价是不是证书
        User loginUser = userService.getLoginUser();
        if (!loginUser.getUserPassword().equals(EncryptionUtils.md5SaleEncryptString(changeChargingVO.getPassword()))){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "密码错误");
        }
        // 修改定价
        InterfaceCharging interfaceCharging = interfaceChargingMapper.selectOne(new LambdaQueryWrapper<InterfaceCharging>()
                .eq(InterfaceCharging::getInterfaceId, changeChargingVO.getInterfaceId())
        );
        if (Objects.isNull(interfaceCharging)){
            // 创建一个新的计费规则
            interfaceCharging = InterfaceCharging.builder()
                    .userId(loginUser.getId())
                    .interfaceId(changeChargingVO.getInterfaceId())
                    .charging(changeChargingVO.getChangeCharging())
                    .availablePieces(changeChargingVO.getAvailablePieces())
                    .build();
            interfaceChargingMapper.insert(interfaceCharging);
        }else {
            // 修改计费规则
            interfaceCharging.setCharging(changeChargingVO.getChangeCharging());
            interfaceCharging.setAvailablePieces(changeChargingVO.getAvailablePieces());
            int i = interfaceChargingMapper.updateById(interfaceCharging);
            if (i < 1){
                throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR, "系统繁忙");
            }
        }
    }
}




