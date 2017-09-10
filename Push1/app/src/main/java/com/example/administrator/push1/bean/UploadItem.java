package com.example.administrator.push1.bean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/7/12.
 */

public class UploadItem {
    private String Userid;
    private String password;

    public void setUserid(String userid) {
        Userid = userid;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setFiles(ArrayList<String> files) {
        this.files = files;
    }

    public String getUserid() {

        return Userid;
    }

    public String getPassword() {
        return password;
    }

    public String getDetail() {
        return detail;
    }

    public ArrayList<String> getFiles() {
        return files;
    }

    private String detail;
    private ArrayList<String> files;


}
