package com.qixiang.qixiang_system_api.dto;

import java.time.LocalDateTime;

/**
 * 登录响应DTO
 * 用于返回登录成功后的信息
 */
public class LoginResponse {
    
    /**
     * JWT Token
     */
    private String token;
    
    /**
     * Token类型
     */
    private String tokenType = "Bearer";
    
    /**
     * Token过期时间
     */
    private LocalDateTime expiresAt;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 机构代码
     */
    private String organizationCode;
    
    /**
     * 用户角色
     */
    private String role;
    
    // 默认构造函数
    public LoginResponse() {}
    
    // 带参构造函数
    public LoginResponse(String token, Long userId, String username, String organizationCode, String role) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.organizationCode = organizationCode;
        this.role = role;
        // Token有效期24小时
        this.expiresAt = LocalDateTime.now().plusHours(24);
    }
    
    // Getter和Setter方法
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getTokenType() {
        return tokenType;
    }
    
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getOrganizationCode() {
        return organizationCode;
    }
    
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    // toString方法
    @Override
    public String toString() {
        return "LoginResponse{" +
                "token='" + token + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", expiresAt=" + expiresAt +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", organizationCode='" + organizationCode + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
