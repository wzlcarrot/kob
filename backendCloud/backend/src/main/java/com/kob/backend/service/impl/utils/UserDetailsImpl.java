package com.kob.backend.service.impl.utils;

import com.kob.backend.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    //用户账号是否过期，true是没有过期
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    //用户账号是否被锁定，true是没有被锁定
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    //用户凭证是否过期,true是没有过期
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    //用户是不是被启用了
    public boolean isEnabled() {
        return true;
    }
}
