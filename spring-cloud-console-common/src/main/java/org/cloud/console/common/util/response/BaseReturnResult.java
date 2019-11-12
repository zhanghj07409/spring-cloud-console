package org.cloud.console.common.util.response;

import java.io.Serializable;


/**
 * 功能说明: 返回页面结果对象<br>
 * 系统版本: 1.0 <br>
 * 开发人员: huajun.zhang
 * 开发时间: 2019/3/26<br>
 * <br>
 */
public class BaseReturnResult implements Serializable{

    private static final long serialVersionUID = -1L;

    private boolean success;
    //返回信息码
    private String code;
    //返回信息
    private String msg;

    private Object data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
