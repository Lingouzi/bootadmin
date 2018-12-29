package com.ly.bootadmin.utils;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * 组装返回数据，一般的格式为：{code:0,msg:"",data:{}}
 * code标识数据请求是否成功，app或者其他端做出对应的反馈
 * msg：一些重要的返回信息，或者是错误信息。
 * data：返回的各类数据，可以为空。
 *
 * @author linyun
 * @date 2018/11/6 18:18
 */
@Data
@Builder
public class JsonResp {
    /**
     * 返回状态
     */
    private int code;
    private String msg;
    /**
     * 返回数据
     */
    private Object data;

    private JsonResp(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static Object success(Object object) {
        return new JsonResp(0, "success", object);
    }

    public static Object success(String msg, Object object) {
        return new JsonResp(0, msg, object);
    }

    public static Object success(String msg) {
        return new JsonResp(0, StringUtils.isEmpty(msg) ? "success" : msg, null);
    }

    public static Object success() {
        return new JsonResp(0, "success", null);
    }

    public static Object fail(String err) {
        return new JsonResp(1, StringUtils.isEmpty(err) ? "error" : err, null);
    }

    public static Object fail(Object object) {
        return new JsonResp(1, "error", object);
    }

    public static Object fail(String error, Object object) {
        return new JsonResp(1, error, object);
    }

    public static Object fail() {
        return new JsonResp(1, "error", null);
    }

}
