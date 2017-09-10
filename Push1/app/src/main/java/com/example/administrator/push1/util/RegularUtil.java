package com.example.administrator.push1.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/7/9.
 */

public class RegularUtil {

    public static boolean isPhoneNumber(String phoneNum) {

        String regExp = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(phoneNum);
        return m.matches();
    }

    public static boolean isPasswordValid(String password) {
        if (password == null) {
            return false;
        }
        if (password.length() < 6) {
            return false;
        }

        return true;
    }

}
