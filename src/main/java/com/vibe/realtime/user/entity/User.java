package com.vibe.realtime.user.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
    
    // ------------------------------
    // 사용자와 롤 연결 (ManyToMany)
    // 필드에 @JoinTable 선언시, 현재 엔티티와 필드 타입의 엔티티를 연결
    // @Builder.Default -> 생성자에서 roles 항목을 null 대신 ArrayList 인스턴스를 생성하여 할당 
    // ------------------------------
    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER) // 로그인 시 바로 권한을 가져오기 위해 EAGER
    @JoinTable(
        name = "user_roles", // 연결 테이블 이름
        joinColumns = @JoinColumn(name = "user_id"), // user_roles의 외래키1, joinColumns → 현재 엔티티(User)의 PK 참조
        inverseJoinColumns = @JoinColumn(name = "role_id") // user_roles의 외래키2, inverseJoinColumns → 상대 엔티티(Role)의 PK 참조 
    )
    private List<Role> roles = new ArrayList<>();

}


