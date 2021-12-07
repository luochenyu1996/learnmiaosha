package com.chenyu.learnmiaosha.pojo.dao;

public class UserPasswordDO {

    private Integer id;


    private String encrptPassword;


    private Integer userId;


    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }


    public String getEncryptPassword() {
        return encrptPassword;
    }


    public void setEncryptPassword(String encryptPassword) {
        this.encrptPassword = encryptPassword == null ? null : encryptPassword.trim();
    }


    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}