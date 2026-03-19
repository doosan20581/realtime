package com.vibe.realtime.auth.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;

@Getter
public class CustomUserDetails implements UserDetails {

	private static final long serialVersionUID = 1L;
	
	private final Integer userId;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Integer userId, String email, String password,
                             Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    // ------------------------------
    // Spring Security에서 사용하는 값
    // ------------------------------

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    // 비밀번호 (로그인 검증에 사용)
    @Override
    public String getPassword() {
        return password;
    }

    // username 대신 email 사용
    @Override
    public String getUsername() {
        return email;
    }

    // ------------------------------
    // 계정 상태 관련 (확장 가능)
    // ------------------------------

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}