package com.test.security;

import com.test.utils.utils.MD5;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component//将该类交给spring容器进行管理
public class myPasswordEncoder implements PasswordEncoder {

    public myPasswordEncoder(){
        this(-1);
    }
    public myPasswordEncoder(int strLendth){

    }

//进行md5加密
    @Override
    public String encode(CharSequence charSequence) {
        return MD5.encrypt(charSequence.toString());
    }
//进行密码比对
    @Override
    public boolean matches(CharSequence charSequence, String encodedPassword) {
        return encodedPassword.equals(MD5.encrypt(charSequence.toString()));
    }
}
