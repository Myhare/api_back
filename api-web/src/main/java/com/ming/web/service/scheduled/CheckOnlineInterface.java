package com.ming.web.service.scheduled;

import cn.hutool.http.HttpResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ming.apiCommon.model.entity.InterfaceInfo;
import com.ming.apiCommon.model.entity.User;
import com.ming.openApiClientSdk.client.OpenApiClient;
import com.ming.web.constant.CommonConstant;
import com.ming.web.constant.MQPrefixConst;
import com.ming.web.model.dto.email.EmailSendDTO;
import com.ming.web.model.enums.InterfaceInfoStatusEnum;
import com.ming.web.service.InterfaceInfoService;
import com.ming.web.service.UserService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 对上线接口进行检查，判断接口是否正常运行
 */
@Service
public class CheckOnlineInterface {

    @Resource
    private UserService userService;

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Value("${web.front}")
    private String frontLocal;

    public void checkOnlineInterface(){
        List<InterfaceInfo> onlineinterfaceList = interfaceInfoService.list(new LambdaQueryWrapper<InterfaceInfo>()
                .eq(InterfaceInfo::getStatus, InterfaceInfoStatusEnum.ONLINE.getValue())
        );

        User adminUser = userService.getById(CommonConstant.ADMIN_ID);

        // 用来存调用失败的接口列表
        List<InterfaceInfo> failInterfaceList = new ArrayList<>();

        // 遍历上线接口列表，一个一个调用
        for (InterfaceInfo interfaceInfo : onlineinterfaceList) {
            // 这里直接使用系统管理员的密钥进行调用
            String url = interfaceInfo.getUrl();
            String params = interfaceInfo.getRequestParams();
            String method = interfaceInfo.getMethod();
            OpenApiClient openApiClient = new OpenApiClient(adminUser.getAccessKey(), adminUser.getSecretKey());
            HttpResponse response = openApiClient.invokeInterface(url, params, method);
            if (!interfaceInfoService.responseDetection(response)){
                failInterfaceList.add(interfaceInfo);
            }
        }
        if (failInterfaceList.size() > 0){
            // 说明有接口无法调用
            // 下线接口
            failInterfaceList.forEach(interfaceInfo -> interfaceInfo.setStatus(InterfaceInfoStatusEnum.OFFLINE.getValue()));
            // 对管理员进行邮件通知
            StringBuilder content = new StringBuilder("接口");
            // 拼接邮件通知字符串
            for (InterfaceInfo interfaceInfo : failInterfaceList) {
                content.append(interfaceInfo.getName());
                content.append("、");
            }
            // TODO 优化处理逻辑，现在不想动先这样
            content.deleteCharAt(content.length() - 1);
            content.append("无法调用，已经自动下线接口，请进入");
            content.append(frontLocal);
            content.append("进行处理");
            EmailSendDTO emailSendDTO = EmailSendDTO.builder()
                    .email(adminUser.getEmail())
                    .subject("OpenApi-接口无法正常调用")
                    .content(content.toString())
                    .build();
            rabbitTemplate.convertAndSend(MQPrefixConst.EMAIL_EXCHANGE, "", emailSendDTO);
        }
    }

}
