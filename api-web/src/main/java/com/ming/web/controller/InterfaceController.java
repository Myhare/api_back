package com.ming.web.controller;

import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ming.apiCommon.model.entity.InterfaceInfo;
import com.ming.apiCommon.model.entity.User;
import com.ming.openApiClientSdk.client.OpenApiClient;
import com.ming.web.annotation.AuthCheck;
import com.ming.web.common.IdRequest;
import com.ming.apiCommon.model.enums.ResultCodeEnum;
import com.ming.web.exception.BusinessException;
import com.ming.web.model.dto.interfaceinfo.InterfaceInfoInvokeRequest;
import com.ming.web.model.enums.InterfaceInfoStatusEnum;
import com.ming.web.model.vo.*;
import com.ming.apiCommon.model.vo.ResponseResult;
import com.ming.web.service.InterfaceInfoService;
import com.ming.web.service.UserService;
import com.ming.web.utils.BeanCopyUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/interfaceInfo")
public class InterfaceController {

    @Resource
    private InterfaceInfoService interfaceInfoService;
    @Resource
    private UserService userService;

    @Resource
    private OpenApiClient openApiClient;

    // @Value("${openapi.client.access-key}")
    // private String accessKey;
    //
    // @Value("${openapi.client.secret-key}")
    // private String secretKey;

    /**
     * 用户调用接口
     * @return
     */
    @PostMapping("/invoke")
    public ResponseResult<Object> invokeInterface(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest){
        // 校验参数
        if (interfaceInfoInvokeRequest == null || interfaceInfoInvokeRequest.getId() == null || interfaceInfoInvokeRequest.getId() < 0){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        Long id = interfaceInfoInvokeRequest.getId();
        String params = interfaceInfoInvokeRequest.getUserRequestParams();
        // 通过id查询接口信息
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        if (interfaceInfo == null){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        // 判断接口是否上线
        if (interfaceInfo.getStatus().equals(InterfaceInfoStatusEnum.OFFLINE.getValue())){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "接口未开启");
        }
        // 从接口信息中获取接口请求地址
        String url = interfaceInfo.getUrl();
        String method = interfaceInfo.getMethod();
        User loginUser = userService.getLoginUser();
        OpenApiClient openApiClient = new OpenApiClient(loginUser.getAccessKey(), loginUser.getSecretKey());
        HttpResponse response = null;
        try {
            response = openApiClient.invokeInterface(url, params, method);
        } catch (Exception e) {
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR, "请求接口失败");
        }
        if (!interfaceInfoService.responseDetection(response)){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR, response.body());
        }
        return ResponseResult.ok(JSONUtil.formatJsonStr(response.body()));
    }

    /**
     * 获取接口商城列表
     */
    @GetMapping("/shop/list")
    public ResponseResult<PageResult<ShopInterfaceInfoVO>> listShopInterface(@Valid QueryInfoVO queryInfoVO){
        LambdaQueryWrapper<InterfaceInfo> lambdaQueryWrapper = new LambdaQueryWrapper<InterfaceInfo>()
                .like(InterfaceInfo::getName, queryInfoVO.getKeyword())
                .or()
                .like(InterfaceInfo::getDescription, queryInfoVO.getKeyword());
        long count = interfaceInfoService.count(lambdaQueryWrapper);
        Page<InterfaceInfo> page = new Page<>(queryInfoVO.getCurrent(), queryInfoVO.getPageSize());
        List<InterfaceInfo> interfaceInfoList = interfaceInfoService.page(page, lambdaQueryWrapper).getRecords();
        List<ShopInterfaceInfoVO> shopInterfaceList = BeanCopyUtils.copyList(interfaceInfoList, ShopInterfaceInfoVO.class);
        return ResponseResult.ok(new PageResult<ShopInterfaceInfoVO>(shopInterfaceList, (int) count));
    }

    /**
     * 通过id获取接口详细信息
     */
    @GetMapping("/getInvokeInterface")
    public ResponseResult<InterfaceInfoVO> getInvokeInterfaceById(Integer id){
        return ResponseResult.ok(interfaceInfoService.getInterfaceInfoVOById(id));
    }

    // 管理员获取接口真实信息
    @GetMapping("/adminGetInvokeInterface")
    public ResponseResult<InterfaceInfoVO> adminGetInvokeInterface(Integer id){
        return ResponseResult.ok(interfaceInfoService.adminGetInterfaceInfoVOById(id));
    }

    /**
     * 获取接口列表
     */
    @AuthCheck(anyRole = {"admin", "test"})
    @GetMapping("/list")
    public ResponseResult<List<InterfaceInfoVO>> listInterface(){
        List<InterfaceInfo> list = interfaceInfoService.list();
        return ResponseResult.ok(BeanCopyUtils.copyList(list, InterfaceInfoVO.class));
    }

    /**
     * 上线接口
     */
    @AuthCheck(mustRole = "admin")
    @PostMapping("/online")
    public ResponseResult<Boolean> onlineInterface(@RequestBody IdRequest idRequest,
                                                   HttpServletRequest request){
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        long id = idRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ResultCodeEnum.NOT_FOUND_ERROR);
        }
        // 判断该接口是否可以调用
        interfaceInfoService.checkInterfaceInvoke(oldInterfaceInfo);

        // 修改接口
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.ONLINE.getValue());
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResponseResult.ok(result);
    }

    /**
     * 下线接口
     */
    @AuthCheck(mustRole = "admin")
    @PostMapping("/offline")
    public ResponseResult<Boolean> offlineInterface(@RequestBody IdRequest idRequest,
                                                    HttpServletRequest request){
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        Long id = idRequest.getId();
        // 判断接口是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null){
            throw new BusinessException(ResultCodeEnum.NOT_FOUND_ERROR);
        }
        // 修改数据库
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.OFFLINE.getValue());

        boolean flag = interfaceInfoService.updateById(interfaceInfo);
        return ResponseResult.ok(flag);
    }

    /**
     * 添加接口
     */
    @AuthCheck(mustRole = "admin")
    @PostMapping("/addOrUpdate")
    public ResponseResult<Boolean> insertInterface(@RequestBody InterfaceInfoUpdateVO interfaceInfoUpdateVO){
        // System.out.println("成功进入insertInterface方法");
        // System.out.println(interfaceInfoAddVO);
        InterfaceInfo interfaceInfo = BeanCopyUtils.copyObject(interfaceInfoUpdateVO, InterfaceInfo.class);
        List<RequestExplainVO> requestExplain = interfaceInfoUpdateVO.getRequestExplain();
        List<ResponseExplainVO> responseExplain = interfaceInfoUpdateVO.getResponseExplain();
        interfaceInfo.setRequestExplain(JSONUtil.toJsonStr(requestExplain));
        interfaceInfo.setResponseExplain(JSONUtil.toJsonStr(responseExplain));
        // 保存创建人id
        interfaceInfo.setUserId(userService.getLoginUserVO().getId());
        // 检测一下这个接口是否可以正常使用
        interfaceInfoService.saveOrUpdate(interfaceInfo);
        // System.out.println(interfaceInfo);
        return ResponseResult.ok();
    }

    /**
     * id删除接口
     */
    @AuthCheck(mustRole = "admin")
    @PostMapping("/delete")
    public ResponseResult<?> deleteInterfaceById(@RequestBody IdRequest idRequest){
        interfaceInfoService.delInterfaceById(Math.toIntExact(idRequest.getId()));
        return ResponseResult.ok();
    }

}
