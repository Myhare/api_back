package com.ming.web.service.dubbo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ming.apiCommon.dubbo.InnerInterfaceInfoService;
import com.ming.apiCommon.model.entity.InterfaceInfo;
import com.ming.apiCommon.model.enums.ResultCodeEnum;
import com.ming.web.exception.BusinessException;
import com.ming.web.mapper.InterfaceInfoMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    /**
     * TODO 这里接口有可能重复，改成使用请求头将接口id传递过来
     * 获取接口信息
     * @param path  接口路径
     * @param method 接口请求方法
     */
    @Override
    public InterfaceInfo getInterfaceInfo(String path, String method) {
        if (StringUtils.isAnyBlank(path, method)) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        return interfaceInfoMapper.selectOne(new LambdaQueryWrapper<InterfaceInfo>()
                .eq(InterfaceInfo::getUrl, path)
                .eq(InterfaceInfo::getMethod, method)
        );
    }

    // 通过id获取接口信息
    @Override
    public InterfaceInfo getInterfaceById(Long interfaceId) {
        return interfaceInfoMapper.selectById(interfaceId);
    }

    // 通过id列表查看接口列表
    @Override
    public List<InterfaceInfo> listInterfaceByIdList(List<Long> idList) {
        // 判空
        if (idList.size() < 1){
            return new ArrayList<>();
        }
        return interfaceInfoMapper.selectList(new LambdaQueryWrapper<InterfaceInfo>()
                .in(InterfaceInfo::getId, idList)
        );
    }
}
