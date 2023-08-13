package com.ming.web;

import com.ming.web.mapper.InterfaceChargingMapper;
import com.ming.web.model.entity.InterfaceCharging;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@SpringBootTest
public class WebApplicationTest {

    @Resource
    private InterfaceChargingMapper interfaceChargingMapper;

    /**
     * 测试mp的乐观锁插件
     */
    @Test
    public void testVersion() throws IOException {
        // List<InterfaceCharging> interfaceChargings = interfaceChargingMapper.selectList(null);
        // System.out.println(interfaceChargings);
        InterfaceCharging interfaceCharging1 = interfaceChargingMapper.selectById(3);
        Long version1 = interfaceCharging1.getVersion();
        interfaceCharging1.setAvailablePieces("101");
        interfaceCharging1.setVersion(version1);
        int i = interfaceChargingMapper.updateById(interfaceCharging1);
        System.out.println("第一条数据更新成功了"+i+"条数据");

        InterfaceCharging interfaceCharging2 = interfaceChargingMapper.selectById(3);
        interfaceCharging2.setAvailablePieces("999");
        // 这里假设是另外一个线程获取的version1(老版本)
        interfaceCharging2.setVersion(version1);
        int j = interfaceChargingMapper.updateById(interfaceCharging2);
        System.out.println("第二条数据更新成功了"+j+"条数据");
    }

}
