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
    private final String name;
    private final Collection<? extends GrantedAuthority> authorities;
    
    // (로그인 이후 요청 흐름) JWT 인증 필터에서 SecurityContext에 저장할 때 사용하는 최소 정보 생성자, DB 조회 없음
    /*
    	최초 로그인 시에만 비밀번호 검증이 필요하고,
		이후 요청은 JWT의 무결성(서명 검증) + 권한 정보로 처리
    */
    public CustomUserDetails(Integer userId, Collection<? extends GrantedAuthority> authorities) {
		this.userId = userId;
		this.email = null;
		this.password = null;
		this.name = null;
		this.authorities = authorities;
	}
    
    // 로그인 인증 시 UserDetailsService에서 DB 조회 후 생성하는 생성자
    /*
	    loadUserByUsername()
		    ↓
		CustomUserDetails 생성 (여기서 두 번째 생성자 사용)
		    ↓
		DaoAuthenticationProvider
		    ↓
		Authentication 생성
	*/
    public CustomUserDetails(Integer userId, String email, String password, String name, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.name = name;
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