package com.example.security.auth;

import com.example.security.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class PrincipalDetails implements UserDetails {

    private User user;

    public PrincipalDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });
        return collect;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    //계정 만료 여부
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //계정이 잠겼는지 여부
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //계정의 비밀번호가 오래 변경되지 않았을 경우
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //휴면 계정일 때
    //현재 시간 - 로그인 시간이 1년 초과일 떄 -> False
    @Override
    public boolean isEnabled() {
        return true;
    }
}
