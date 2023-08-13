package com.ming.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ming.apiCommon.model.entity.InterfaceInfo;
import com.ming.apiCommon.model.entity.User;
import com.ming.apiCommon.model.entity.UserInterfaceInfo;
import com.ming.web.mapper.InterfaceInfoMapper;
import com.ming.web.mapper.UserInterfaceInfoMapper;
import com.ming.web.mapper.UserMapper;
import com.ming.web.model.dto.InvokeCountDTO;
import com.ming.web.model.dto.statistics.DataInvokeStatisticsDTO;
import com.ming.web.model.dto.statistics.InterfaceInvokeStatisticsDTO;
import com.ming.web.model.dto.statistics.UserInvokeDTO;
import com.ming.web.model.vo.BackStatisticsDTO;
import com.ming.web.service.ApiService;
import com.ming.web.service.InvokeCountService;
import com.ming.web.service.UserInterfaceInfoService;
import com.ming.web.utils.BeanCopyUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ApiServiceImpl implements ApiService {

    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;
    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;
    @Resource
    private UserMapper userMapper;

    @Resource
    private InvokeCountService invokeCountService;

    @Override
    public BackStatisticsDTO getBackStatistics() {
        // 获取用户访问来源
        // 获取最近一周接口调用次数
        List<DataInvokeStatisticsDTO> dataInvokeStatisticsList = invokeCountService.listInvokeCountDTO();

        // 接口调用总数量统计
        List<UserInterfaceInfo> userInterfaceInfoList = userInterfaceInfoMapper.listTopInvokeInterfaceInfo(7);
        // 接口调用排行id的Map
        Map<Long, List<UserInterfaceInfo>> invokeMap = userInterfaceInfoList.stream()
                .collect(Collectors.groupingBy(UserInterfaceInfo::getInterfaceInfoId));
        // 查询调用次数为前几的接口详细信息
        List<InterfaceInfo> interfaceInfoList = interfaceInfoMapper.selectList(new LambdaQueryWrapper<InterfaceInfo>()
                .in(InterfaceInfo::getId, invokeMap.keySet())
        );
        List<InterfaceInvokeStatisticsDTO> invokeStatisticsList = interfaceInfoList.stream().map(interfaceInfo -> {
            return new InterfaceInvokeStatisticsDTO(interfaceInfo.getName(),
                    invokeMap.get(interfaceInfo.getId()).get(0).getTotalNum());
        }).collect(Collectors.toList());

        // 调用接口前5的用户数量
        List<UserInterfaceInfo> userInvokeCountList = userInterfaceInfoMapper.listTopUserInvokeCount(5);
        Map<Long, List<UserInterfaceInfo>> userIdInterfaceMap = userInvokeCountList.stream()
                .collect(Collectors.groupingBy(UserInterfaceInfo::getUserId));
        // 查询用户详细信息
        List<User> userList = userMapper.selectList(new LambdaQueryWrapper<User>()
                .in(User::getId, userIdInterfaceMap.keySet())
        );
        // 封装用户详细信息到结果集中
        List<UserInvokeDTO> userInvokeList = userList.stream().map(user -> {
            return new UserInvokeDTO(user.getUserName(), userIdInterfaceMap.get(user.getId()).get(0).getTotalNum());
        }).collect(Collectors.toList());

        return BackStatisticsDTO.builder()
                .interfaceInvokeStatisticsList(invokeStatisticsList) // 总接口调用次数
                .userInvokeList(userInvokeList)  // 用户调用接口前几的数量
                .invokeCountStatisticsList(dataInvokeStatisticsList)
                .build();
    }
}
