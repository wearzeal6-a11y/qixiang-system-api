package com.qixiang.qixiang_system_api.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 系统管理员用户实体类
 * 映射到 users 数据库表
 */
@Entity
@Table(name = "users", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"organization_code", "username"}),
       indexes = {
           @Index(name = "idx_organization_code", columnList = "organization_code"),
           @Index(name = "idx_username", columnList = "username"),
           @Index(name = "idx_role", columnList = "role"),
           @Index(name = "idx_status", columnList = "status")
       })
@EntityListeners(AuditingEntityListener.class)
public class User {
    
    /**
     * 主键ID，自动增长
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 机构代码，用于区分不同组织
     */
    @Column(name = "organization_code", nullable = false, length = 50)
    private String organizationCode;
    
    /**
     * 用户名，登录账号
     */
    @Column(name = "username", nullable = false, length = 50)
    private String username;
    
    /**
     * 密码，使用BCrypt算法加密存储
     */
    @Column(name = "password", nullable = false)
    private String password;
    
    /**
     * 用户角色，默认为ADMIN
     */
    @Column(name = "role", nullable = false, length = 20)
    private String role = "ADMIN";
    
    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * 最后登录时间
     */
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
    
    /**
     * 账号状态：1-启用，0-禁用
     */
    @Column(name = "status")
    private Integer status = 1;
    
    // 默认构造函数
    public User() {}
    
    // 带参构造函数
    public User(String organizationCode, String username, String password, String role) {
        this.organizationCode = organizationCode;
        this.username = username;
        this.password = password;
        this.role = role;
    }
    
    // Getter和Setter方法
    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }
    
    public String getOrganizationCode() { 
        return organizationCode; 
    }
    
    public void setOrganizationCode(String organizationCode) { 
        this.organizationCode = organizationCode; 
    }
    
    public String getUsername() { 
        return username; 
    }
    
    public void setUsername(String username) { 
        this.username = username; 
    }
    
    public String getPassword() { 
        return password; 
    }
    
    public void setPassword(String password) { 
        this.password = password; 
    }
    
    public String getRole() { 
        return role; 
    }
    
    public void setRole(String role) { 
        this.role = role; 
    }
    
    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }
    
    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = createdAt; 
    }
    
    public LocalDateTime getUpdatedAt() { 
        return updatedAt; 
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) { 
        this.updatedAt = updatedAt; 
    }
    
    public LocalDateTime getLastLoginAt() { 
        return lastLoginAt; 
    }
    
    public void setLastLoginAt(LocalDateTime lastLoginAt) { 
        this.lastLoginAt = lastLoginAt; 
    }
    
    public Integer getStatus() { 
        return status; 
    }
    
    public void setStatus(Integer status) { 
        this.status = status; 
    }
    
    // toString方法
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", organizationCode='" + organizationCode + '\'' +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", createdAt=" + createdAt +
                ", status=" + status +
                '}';
    }
    
    // equals方法
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        User user = (User) o;
        return id != null && id.equals(user.id);
    }
    
    // hashCode方法
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
