package com.ming.apiCommon.dubbo;

public interface ApiBackendService {

    /**
     * 回滚库存
     * @param interfaceId 接口id
     * @param num         要回滚的库存数量
     */
    boolean rollBackStock(Long interfaceId, Integer num);

    /**
     * 通过接口id获取接口库存
     * @param interfaceId 接口id
     * @return            接口库存
     */
    String getStockByInterfaceId(Long interfaceId);


    /**
     * 扣除库存
     * @param interfaceId 接口id
     * @param count       扣除的数量
     */
    boolean reduceStock(Long interfaceId, Integer count);

    /**
     * 支付成功之后对用户添加调用次数
     */
    boolean addInvokeCount(Long userId, Long interfaceId, Integer count);
}
