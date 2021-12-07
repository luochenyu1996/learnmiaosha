package com.chenyu.learnmiaosha.execption;

/**
 *
 * 通用方法
 *
 */
public interface CommonError {
    public int getErrCode();

    public String getErrMsg();

    public CommonError setErrMsg(String errMsg);


}
