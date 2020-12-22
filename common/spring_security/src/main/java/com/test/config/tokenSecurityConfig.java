package com.test.config;

import com.test.filter.tokenAuthFilter;
import com.test.filter.tokenLoginFilter;
import com.test.security.myPasswordEncoder;
import com.test.security.tokenLogout;
import com.test.security.tokenManager;
import com.test.security.unAuthEntryPoints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class tokenSecurityConfig extends WebSecurityConfigurerAdapter {
    private tokenManager tokenManager;//获取用户信息，权限
    private RedisTemplate redisTemplate;//
    private myPasswordEncoder myPasswordEncoder;//这是对password进行加解密的类
    private UserDetailsService userDetailsService;

    @Autowired
    public tokenSecurityConfig(UserDetailsService userDetailsService,myPasswordEncoder myPasswordEncoder,RedisTemplate redisTemplate,
                               tokenManager tokenManager){
        this.myPasswordEncoder = myPasswordEncoder;
        this.redisTemplate = redisTemplate;
        this.tokenManager = tokenManager;
        this.userDetailsService = userDetailsService;

    }
    /**
     * 配置设置
     * @param http
     * @throws Exception
     */
    //设置退出的地址和token，redis操作地址
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.exceptionHandling()
                .authenticationEntryPoint(new unAuthEntryPoints())//没有权限访问
                .and().csrf().disable()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and().logout().logoutUrl("/admin/acl/index/logout")//退出路径
                .addLogoutHandler(new tokenLogout(tokenManager,redisTemplate)).and()
                .addFilter(new tokenLoginFilter(tokenManager, redisTemplate,authenticationManager() ))
                .addFilter(new tokenAuthFilter(authenticationManager(), tokenManager, redisTemplate)).httpBasic();
    }

    //调用UserDetailsService和密码处理
    @Override
    public void configure(AuthenticationManagerBuilder auth ) throws Exception{
        auth.userDetailsService(userDetailsService).passwordEncoder(myPasswordEncoder);
    }

    @Override
    public void configure(WebSecurity web){
        web.ignoring().antMatchers("/","/test/**","/api/**");//添加不需要拦截的路径,you can touch directly.
    }
}
