package com.test.aclservice.service.impl;

import com.test.aclservice.entity.User;
import com.test.aclservice.service.PermissionService;
import com.test.aclservice.service.UserService;
import com.test.entities.securityUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("UserDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionService permissionService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.selectByUsername(username);
        if(user == null){
            throw new UsernameNotFoundException("用户不存在");
        }
        com.test.entities.User curUser = new com.test.entities.User();
        BeanUtils.copyProperties(user,curUser);
        List<String> permissionValueList = permissionService.selectPermissionValueByUserId(user.getId());
//        这里切记，要同时将user信息和user权限都添加都userservice中去。
        securityUser securityUser = new securityUser();
        securityUser.setCurrentUserInfo(curUser);
        securityUser.setPermissionValueList(permissionValueList);
        return securityUser;

    }
}
