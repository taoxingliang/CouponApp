package com.example.administrator.push1.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/7/9.
 */

public class UserInfo implements Serializable {
    private String tag;
    private String account;
    private String password;

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTag() {

        return tag;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    public UserInfo() {

    }
}
