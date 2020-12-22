package com.test.security;

import com.test.utils.utils.R;
import com.test.utils.utils.ResponseUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class tokenLogout implements LogoutHandler {
    //将redisTemplate以及tokenManager传入
    private tokenManager tokenManager;
    private RedisTemplate redisTemplate;

    public tokenLogout(tokenManager tokenManager, RedisTemplate redisTemplate) {
        this.tokenManager = tokenManager;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) {
/**
 * 1.从header里面获取token
 * 2.token不为空，删除token，再从redis里面删除token
 */
        String token = httpServletRequest.getHeader("token");
        if(token != null){
            //delete
            tokenManager.removeToken(token);
            //获取username
            String username = tokenManager.getUserInfoFromToken(token);
            redisTemplate.delete(username);
        }
        //返回状态信息，这里用到的是之前写的工具类的东西。
        ResponseUtil.out(httpServletResponse, R.ok());
    }
}
