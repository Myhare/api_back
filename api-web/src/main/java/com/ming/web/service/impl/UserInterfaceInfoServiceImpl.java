package com.ming.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ming.apiCommon.model.entity.User;
import com.ming.apiCommon.model.entity.UserInterfaceInfo;
import com.ming.apiCommon.model.enums.ResultCodeEnum;
import com.ming.openApiClientSdk.utils.SignUtils;
import com.ming.web.exception.BusinessException;
import com.ming.web.mapper.UserInterfaceInfoMapper;
import com.ming.web.model.enums.InterfaceCountStatusEnum;
import com.ming.web.model.vo.*;
import com.ming.web.service.UserInterfaceInfoService;
import com.ming.web.service.UserService;
import com.ming.web.utils.CommonUtils;
import com.ming.web.utils.EncryptionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static com.ming.web.model.enums.InterfaceCountStatusEnum.*;

/**
 * @author 86135
 * @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service实现
 * @createDate 2023-06-25 14:51:54
 */
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
        implements UserInterfaceInfoService {

    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Resource
    private UserService userService;

    // 校验接口
    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean isAdd) {
        if (userInterfaceInfo == null) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        // 创建时，所有参数必须非空
        if (isAdd) {
            if (userInterfaceInfo.getInterfaceInfoId() <= 0 || userInterfaceInfo.getUserId() <= 0) {
                throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "接口或用户不存在");
            }
        }
        if (userInterfaceInfo.getLeftNum() < 0) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "剩余次数不能小于 0");
        }
    }

    // 调用接口统计
    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        // 判断
        if (interfaceInfoId <= 0 || userId <= 0) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        // UpdateWrapper<UserInterfaceInfo> updateWrapper = new UpdateWrapper<>();
        // updateWrapper.eq("interfaceInfoId", interfaceInfoId);
        // updateWrapper.eq("userId", userId);
        // updateWrapper.setSql("leftNum = leftNum - 1, totalNum = totalNum + 1");
        return this.update(Objects.requireNonNull(new LambdaUpdateWrapper<UserInterfaceInfo>()
                .eq(UserInterfaceInfo::getInterfaceInfoId, interfaceInfoId)
                .eq(UserInterfaceInfo::getUserId, userId)
                .setSql("leftNum = leftNum - 1, totalNum = totalNum + 1"))
        );
        // return this.update(updateWrapper);
    }

    // 查询用户接口列表
    @Override
    public PageResult<UserInterfaceVO> pageUserInterface(QueryInfoVO queryInfoVO) {
        User loginUser = userService.getLoginUser();
        List<UserInterfaceVO> userInterfaceVOList = null;
        Integer interfaceCount = 0;
        // 查询一共有多少个接口
        switch (InterfaceCountStatusEnum.getEnumByText(queryInfoVO.getInterfaceCountStatus())) {
            case ALL:
                // 获取用户所有接口
                userInterfaceVOList = userInterfaceInfoMapper.pageAllUserInterface(queryInfoVO, loginUser.getId());
                interfaceCount = userInterfaceInfoMapper.countAllUserInterface(queryInfoVO, loginUser.getId());
                break;
            case HAVE:
                // 获取用户已拥有接口
                userInterfaceVOList = userInterfaceInfoMapper.pageHaveUserInterface(queryInfoVO, loginUser.getId());
                interfaceCount = userInterfaceInfoMapper.countHaveUserInterface(queryInfoVO, loginUser.getId());
                break;
            case NO_HAVE:
                // 获取用户已拥有接口
                userInterfaceVOList = userInterfaceInfoMapper.pageNoHaveUserInterface(queryInfoVO, loginUser.getId());
                interfaceCount = userInterfaceInfoMapper.countNoHaveUserInterface(queryInfoVO, loginUser.getId());
                break;
            default:
                break;
        }
        // 获取用户调用的接口列表

        return new PageResult<>(userInterfaceVOList, interfaceCount);
    }

    // 添加当前登录用户的接口调用次数
    @Override
    public void addInterfaceCount(AddInterfaceCountVO addInterfaceCountVO) {
        // 校验参数
        String addCountStr = addInterfaceCountVO.getAddCount();
        Long interfaceId = addInterfaceCountVO.getInterfaceId();
        Integer addCount = 0;
        try {
            addCount = Integer.parseInt(addCountStr);
        } catch (NumberFormatException e) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "添加次数需要为整数");
        }
        // 获取登录用户
        User loginUser = userService.getLoginUser();
        // 校验用户输入的密码和当前用户的密码是不是同一个
        if (!loginUser.getUserPassword().equals(EncryptionUtils.md5SaleEncryptString(addInterfaceCountVO.getUserPassword()))){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "密码错误");
        }
        // 查询当前用户是否有这个接口的调用次数
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoMapper.selectOne(new LambdaQueryWrapper<UserInterfaceInfo>()
                .eq(UserInterfaceInfo::getUserId, loginUser.getId())
                .eq(UserInterfaceInfo::getInterfaceInfoId, interfaceId)
        );
        if (Objects.isNull(userInterfaceInfo)){
            // 添加一个新的数据到数据库中
            userInterfaceInfo = UserInterfaceInfo.builder()
                    .userId(loginUser.getId())
                    .interfaceInfoId(interfaceId)
                    .leftNum(addCount)
                    .build();
            userInterfaceInfoMapper.insert(userInterfaceInfo);
        }else {
            // 在原来的数据上进行添加次数
            // 获取剩余调用次数
            Integer leftNum = userInterfaceInfo.getLeftNum();
            leftNum += addCount;
            userInterfaceInfo.setLeftNum(leftNum);
            userInterfaceInfoMapper.updateById(userInterfaceInfo);
        }
    }
}




