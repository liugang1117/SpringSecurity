package com.test.security;

import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.security.Signature;
import java.util.Date;

@Component
public class tokenManager {
    //token存活时间
    private long tokenServived = 24*60*60*7;
    //token密钥
    private  String tokenSighKey = "123456";

    //1.生成token,使用jwt根据用户名生成token。
    public String createToken(String username){
        String token = Jwts.builder().setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis()+tokenServived))//存活时间=当前时间加上设置的tokenservived
                .signWith(SignatureAlgorithm.HS512,tokenSighKey).compressWith(CompressionCodecs.GZIP).compact();

        return token;
    }


    //2.根据token拿到用户信息
    public String getUserInfoFromToken(String token){
        String userinfo = Jwts.parser().setSigningKey(tokenSighKey).parseClaimsJws(token).getBody().getSubject();
        return userinfo;
    }

    //删除token
    public void removeToken(String token){ }//可以不写，客服端不携带token？
}
