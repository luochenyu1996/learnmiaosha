package com.chenyu.learnmiaosha.execption;

/**
 * 异常实现
 *
 *
 */
public class BusinessException extends  RuntimeException implements CommonError {

    private CommonError commonError;


    public BusinessException(CommonError commonError){
        super();
        this.commonError = commonError;
    }


    public BusinessException(CommonError commonError,String errMsg){
        super();
        this.commonError = commonError;
        this.commonError.setErrMsg(errMsg);
    }



    @Override
    public int getErrCode() {
        return this.commonError.getErrCode();
    }

    @Override
    public String getErrMsg() {
        return this.commonError.getErrMsg();
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.commonError.setErrMsg(errMsg);
        return this;
    }

    public CommonError getCommonError() {
        return commonError;
    }
}
