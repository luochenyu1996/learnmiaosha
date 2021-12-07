package com.chenyu.learnmiaosha.Validator;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 参数验证结果
 *
 * @author chen yu
 * @create 2021-12-05 12:19
 */
public class ValidationResult {
    /**
     * 校验结果是否有错
     */
    private boolean hasErrors = false;

    /**
     * 用来存放错误消息
     */
    private Map<String, String> errorMsgMap = new HashMap<>();


    public boolean isHasErrors() {
        return hasErrors;
    }

    public void setHasErrors(boolean hasErrors) {
        this.hasErrors = hasErrors;
    }

    public Map<String, String> getErrorMsgMap() {
        return errorMsgMap;
    }

    public void setErrorMsgMap(Map<String, String> errorMsgMap) {
        this.errorMsgMap = errorMsgMap;
    }


    //实现通用的通过格式化字符串信息获取错误结果的msg方法
    public String getErrMsg() {
        return StringUtils.join(errorMsgMap.values().toArray(), ",");
    }
}
