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
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${web.backend}")
    private String local;

    // 普通用户查询接口信息，需要隐藏原接口地址
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
        // 当前服务器的地址，原服务器的地址不能暴露出去
        interfaceInfoVO.setHost(local);
        // TODO 这里同意使用一个接口获取数据，前端用户只需要出入接口id和传入参数实现动态转发
        interfaceInfoVO.setUrl("/invoke");
        interfaceInfoVO.setMethod("POST");
        interfaceInfoVO.setRequestExplain(requestExplainVOList);
        interfaceInfoVO.setResponseExplain(responseExplainVOList);
        return interfaceInfoVO;
    }

    // 查询接口真实信息
    @Override
    public InterfaceInfoVO adminGetInterfaceInfoVOById(Integer id) {
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
        // 当前服务器的地址，原服务器的地址不能暴露出去
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




