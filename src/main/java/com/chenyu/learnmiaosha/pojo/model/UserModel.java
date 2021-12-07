package com.chenyu.learnmiaosha.pojo.model;


import java.io.Serializable;

/**
 * 用户服务层 model
 *
 *
 */
public class UserModel implements Serializable{
    /**
     *  主键id
     *
     */
    private Integer id;

    /**
     * 用户姓名
     *
     */
    private String name;


    /**
     * 用户性别
     *
     */
    private Byte gender;

    /**
     * 用户年龄
     *
     *
     */
    private Integer age;

    /**
     * 用户电话
     *
     */
    private String telphone;

    /**
     *
     *
     */
    private String registerMode;

    /**
     *
     *
     */
    private String thirdPartyId;

    /**
     * 加密后的密码
     *
     */
    private String encrptPassword;

    public String getEncrptPassword() {
        return encrptPassword;
    }

    public void setEncrptPassword(String encrptPassword) {
        this.encrptPassword = encrptPassword;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Byte getGender() {
        return gender;
    }

    public void setGender(Byte gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getTelphone() {
        return telphone;
    }

    public void setTelphone(String telphone) {
        this.telphone = telphone;
    }

    public String getRegisterMode() {
        return registerMode;
    }

    public void setRegisterMode(String registerMode) {
        this.registerMode = registerMode;
    }

    public String getThirdPartyId() {
        return thirdPartyId;
    }

    public void setThirdPartyId(String thirdPartyId) {
        this.thirdPartyId = thirdPartyId;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gender=" + gender +
                ", age=" + age +
                ", telphone='" + telphone + '\'' +
                ", registerMode='" + registerMode + '\'' +
                ", thirdPartyId='" + thirdPartyId + '\'' +
                ", encrptPassword='" + encrptPassword + '\'' +
                '}';
    }
}
