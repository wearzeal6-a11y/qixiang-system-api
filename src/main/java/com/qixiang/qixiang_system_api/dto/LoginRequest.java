package com.qixiang.qixiang_system_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 登录请求DTO
 * 用于接收前端传递的登录参数
 */
public class LoginRequest {
    
    /**
     * 机构代码
     */
    @NotBlank(message = "机构代码不能为空")
    @Size(max = 50, message = "机构代码长度不能超过50个字符")
    private String organizationCode;
    
    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(max = 50, message = "用户名长度不能超过50个字符")
    private String username;
    
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度必须在6-100个字符之间")
    private String password;
    
    // 默认构造函数
    public LoginRequest() {}
    
    // 带参构造函数
    public LoginRequest(String organizationCode, String username, String password) {
        this.organizationCode = organizationCode;
        this.username = username;
        this.password = password;
    }
    
    // Getter和Setter方法
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
    
    // toString方法
    @Override
    public String toString() {
        return "LoginRequest{" +
                "organizationCode='" + organizationCode + '\'' +
                ", username='" + username + '\'' +
                ", password='[PROTECTED]'" +
                '}';
    }
    
    // equals方法
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        LoginRequest that = (LoginRequest) o;
        
        if (!organizationCode.equals(that.organizationCode)) return false;
        if (!username.equals(that.username)) return false;
        return password.equals(that.password);
    }
    
    // hashCode方法
    @Override
    public int hashCode() {
        int result = organizationCode.hashCode();
        result = 31 * result + username.hashCode();
        result = 31 * result + password.hashCode();
        return result;
    }
}
