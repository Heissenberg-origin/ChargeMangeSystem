package org.example.common;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Result 类用于统一 API 响应的结构。
 * 包含响应状态码、消息和数据。
 */
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    // 响应状态码
    private String code;
    // 响应信息
    private String message;
    // 响应数据
    private Object data;

    /**
     * 返回成功的响应，没有数据。
     * @return Result 对象
     */
    public static Result success(){
        Result result = new Result();
        result.setCode("200");
        result.setMessage("请求成功");
        return result;
    }

    /**
     * 返回成功的响应，包含数据。
     * @param data 响应数据
     * @return Result 对象
     */
    public static Result success(Object data){
        Result result = new Result();
        result.setCode("200");
        result.setData(data);
        result.setMessage("请求成功");
        return result;
    }

    /**
     * 返回错误的响应，包含错误信息。
     * @param message 错误信息
     * @return Result 对象
     */
    public static Result error(String message){
        Result result = new Result();
        result.setCode("500");
        result.setMessage(message);
        return result;
    }

    /**
     * 返回错误的响应，包含状态码和错误信息。
     * @param code 状态码
     * @param message 错误信息
     * @return Result 对象
     */
    public static Result error(String code,String message){
        Result result = new Result();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    // Getter 和 Setter 方法

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}