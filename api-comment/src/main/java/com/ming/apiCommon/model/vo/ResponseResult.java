package com.ming.apiCommon.model.vo;

import com.ming.apiCommon.model.enums.ResultCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 返回类
@Data
@NoArgsConstructor
@AllArgsConstructor
// @JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseResult<T> {
    /**
     * 返回状态
     */
    private Boolean flag;
    /**
     * 状态码
     */
    private Integer code;
    /**
     * 提示信息，如果有错误时，前端可以获取该字段进行提示
     */
    private String message;
    /**
     * 查询到的结果数据，
     */
    private T data;

    public ResponseResult(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResponseResult(Integer code, T data) {
        this.code = code;
        this.data = data;
    }

    public ResponseResult(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 因为是static方法，所以需要重新声明为泛型方法
     */
    public static <T> ResponseResult<T> ok(){
        return restResult(true, ResultCodeEnum.SUCCESS.getCode(),ResultCodeEnum.SUCCESS.getMessage(),null);
    }
    public static <T> ResponseResult<T> ok(T data){
        return restResult(true, ResultCodeEnum.SUCCESS.getCode(),ResultCodeEnum.SUCCESS.getMessage(),data);
    }
    public static <T> ResponseResult<T> ok(String message,T data){
        return restResult(true, ResultCodeEnum.SUCCESS.getCode(),message,data);
    }

    /**
     * 失败处理方法
     * @return
     */
    public static <T> ResponseResult<T> fail() {
        return restResult( false, ResultCodeEnum.OPERATION_ERROR.getMessage());
    }
    public static <T> ResponseResult<T> fail(String message) {
        return restResult( false, message);
    }
    public static <T> ResponseResult<T> fail(Integer code, String message) {
        return restResult( false,code,message,null);
    }
    public static <T> ResponseResult<T> fail(T data, String message) {
        return restResult(false,ResultCodeEnum.OPERATION_ERROR.getCode(), message, data);
    }

    private static <T> ResponseResult<T> restResult(Boolean flag, String message) {
        ResponseResult<T> apiResult = new ResponseResult<>();
        apiResult.setCode(flag ? ResultCodeEnum.SUCCESS.getCode() : ResultCodeEnum.OPERATION_ERROR.getCode());
        apiResult.setMessage(message);
        apiResult.setFlag(flag);
        return apiResult;
    }

    private static <T> ResponseResult<T> restResult(Boolean flag,Integer code, String message,T data) {
        ResponseResult<T> apiResult = new ResponseResult<>();
        apiResult.setFlag(flag);
        apiResult.setData(data);
        apiResult.setCode(code);
        apiResult.setMessage(message);
        return apiResult;
    }

}
