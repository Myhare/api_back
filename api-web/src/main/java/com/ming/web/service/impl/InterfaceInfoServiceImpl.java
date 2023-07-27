package com.ming.web.service.impl;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ming.apiCommon.model.entity.InterfaceInfo;
import com.ming.apiCommon.model.entity.User;
import com.ming.apiCommon.model.enums.ResultCodeEnum;
import com.ming.openApiClientSdk.client.OpenApiClient;
import com.ming.web.exception.BusinessException;
import com.ming.web.mapper.InterfaceInfoMapper;
import com.ming.web.model.vo.*;
import com.ming.web.service.InterfaceInfoService;
import com.ming.web.service.UserService;
import com.ming.web.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Objects;

/**
* @author 86135
* @description 针对表【interface_info(接口信息)】的数据库操作Service实现
* @createDate 2023-06-25 14:51:54
*/
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
    implements InterfaceInfoService {

    @Resource
    private UserService userService;

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    @Override
    public InterfaceInfoVO getInterfaceInfoVOById(Integer id) {
        if (id <= 0){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = interfaceInfoMapper.selectById(id);
        if (Objects.isNull(interfaceInfo)){
            throw new BusinessException(ResultCodeEnum.NOT_FOUND_ERROR);
        }
        // 将JSON数组转换成数组封装
        List<RequestExplainVO> requestExplainVOList = JSONUtil.toList(interfaceInfo.getRequestExplain(), RequestExplainVO.class);
        List<ResponseExplainVO> responseExplainVOList = JSONUtil.toList(interfaceInfo.getResponseExplain(), ResponseExplainVO.class);
        InterfaceInfoVO interfaceInfoVO = BeanCopyUtils.copyObject(interfaceInfo, InterfaceInfoVO.class);
        // TODO 将host变成当前服务器的地址，原地址不能发送出去
        interfaceInfoVO.setRequestExplain(requestExplainVOList);
        interfaceInfoVO.setResponseExplain(responseExplainVOList);
        return interfaceInfoVO;
    }

    @Override
    public Integer delInterfaceById(Integer id) {
        return interfaceInfoMapper.deleteById(id);
    }

    // 接口响应校验
    @Override
    public boolean responseDetection(HttpResponse httpResponse) {
        if (Objects.isNull(httpResponse)){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR, "请求错误,请求结果为空");
        }
        return httpResponse.getStatus() == HttpStatus.HTTP_OK;
    }

    // 检查接口是否可以正常调用
    @Override
    public void checkInterfaceInvoke(InterfaceInfo interfaceInfo) {
        User loginUser = userService.getLoginUser();
        OpenApiClient openApiClient = new OpenApiClient(loginUser.getAccessKey(), loginUser.getSecretKey());
        HttpResponse response = null;
        try {
            response = openApiClient.invokeInterface(interfaceInfo.getUrl(),
                    interfaceInfo.getRequestParams(), interfaceInfo.getMethod());
        } catch (Exception e) {
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR, "请求接口失败");
        }
        if (!responseDetection(response)){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR, "接口验证失败，接口请求出现问题");
        }
    }
}




