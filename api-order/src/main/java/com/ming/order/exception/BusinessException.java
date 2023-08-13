package com.ming.order.exception;


import com.ming.apiCommon.model.enums.ResultCodeEnum;

/**
 * 自定义异常类
 *
 * @author yupi
 */
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

    public BusinessException(ResultCodeEnum resultCodeEnum, String message) {
        super(message);
        this.code = resultCodeEnum.getCode();
    }

    public int getCode() {
        return code;
    }

}
