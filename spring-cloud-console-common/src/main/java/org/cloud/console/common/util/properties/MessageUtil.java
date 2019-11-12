package org.cloud.console.common.util.properties;

import org.cloud.console.common.util.response.BaseReturnResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 功能说明: 消息处理<br>
 * 系统版本: 1.0 <br>
 * 开发人员: huajun.zhang
 * 开发时间: 2019/3/26<br>
 * <br>
 */
public class MessageUtil {

    private final static  Logger logger = LoggerFactory.getLogger(MessageUtil.class);

    /**
     * 返回成功
     *
     * @return BaseReturnResult
     */
    public static BaseReturnResult Success() {
        BaseReturnResult baseReturnResult = new BaseReturnResult();
        baseReturnResult.setSuccess(true);
        baseReturnResult.setCode("PUB0001");
        baseReturnResult.setMsg("操作成功！");
        return baseReturnResult;
    }

    /**
     * 返回成功,返回数据
     *
     * @return BaseReturnResult
     */
    public static BaseReturnResult Success(Object data) {
        BaseReturnResult baseReturnResult = new BaseReturnResult();
        baseReturnResult.setSuccess(true);
        baseReturnResult.setCode("PUB0001");
        baseReturnResult.setMsg("操作成功！");
        baseReturnResult.setData(data);
        return baseReturnResult;
    }

    /**
     * 返回成功
     *
     * @return BaseReturnResult
     */
    public static BaseReturnResult Success(String code, String msg) {
        BaseReturnResult baseReturnResult = new BaseReturnResult();
        baseReturnResult.setSuccess(true);
        baseReturnResult.setCode(code);
        baseReturnResult.setMsg(msg);
        return baseReturnResult;
    }

    /**
     * 返回异常
     *
     * @return BaseReturnResult
     */
    public static BaseReturnResult Exception(Exception ex) {
        BaseReturnResult baseReturnResult = new BaseReturnResult();
        baseReturnResult.setSuccess(false);
        String msg = "";
        msg +=ex.getMessage();
        baseReturnResult.setMsg(msg.replaceAll("\\\"", "\\\'").replaceAll("\n", ""));
        //日志记录
        logger.error(baseReturnResult.getMsg(),ex);
        return baseReturnResult;
    }

}
