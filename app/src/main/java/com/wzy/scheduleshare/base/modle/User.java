package com.wzy.scheduleshare.base.modle;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * @ClassName User
 * @Author Wei Zhouye
 * @Date 2020/2/28
 * @Version 1.0
 */
public class User extends BmobUser {

    /*昵称*/
    private String nickname = "未设置";
    /*头像*/
    private String headIcon;
    /*性别*/
    private String sexy = "未设置";
    /*年龄*/
    private Integer age = 0;
    /*地区*/
    private String area = "未设置";
    /*用户当前位置*/
    private String address = "";
    /*地理坐标*/
    private BmobGeoPoint location;
    /*收藏的行程*/
    private BmobRelation collect_relation;


    public BmobRelation getCollect_relation() {
        return collect_relation;
    }

    public void setCollect_relation(BmobRelation collect_relation) {
        this.collect_relation = collect_relation;
    }

    public BmobGeoPoint getLocation() {
        return location;
    }

    public void setLocation(BmobGeoPoint location) {
        this.location = location;
    }


    public String getSexy() {
        return sexy;
    }

    public void setSexy(String sexy) {
        this.sexy = sexy;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


    public String getHeadIcon() {
        return headIcon;
    }

    public void setHeadIcon(String headIcon) {
        this.headIcon = headIcon;
    }

    public String getPassword() {
        return getPassword();
    }
}
