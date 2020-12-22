package com.test.filter;

import com.test.security.tokenManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class tokenAuthFilter extends BasicAuthenticationFilter {
    private tokenManager tokenManager;
    private RedisTemplate redisTemplate;
    public tokenAuthFilter(AuthenticationManager authenticationManager,tokenManager tokenManager,RedisTemplate redisTemplate) {
        super(authenticationManager);
        this.redisTemplate = redisTemplate;
        this.tokenManager = tokenManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        //获取当前认证成功用户权限信息。
        UsernamePasswordAuthenticationToken authRequst = getAuthentication(request);
        if(authRequst != null){
            SecurityContextHolder.getContext().setAuthentication(authRequst);
        }
        chain.doFilter(request,response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request){
        //从header获取token
        String token = request.getHeader("token");
        if(token != null){
            //从token获取username
            String username = tokenManager.getUserInfoFromToken(token);
            //从redis里获取对应的权限
           List<String> permissionValueList = (List<String>) redisTemplate.opsForValue().get(username);
           Collection<GrantedAuthority> authority = new ArrayList<>();
           for(String permission : permissionValueList){
               SimpleGrantedAuthority auth = new SimpleGrantedAuthority(permission);
               authority.add(auth);
           }
           return new UsernamePasswordAuthenticationToken(username,token,authority);
        }
        return null;
    }
}
