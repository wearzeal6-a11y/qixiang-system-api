package com.qixiang.qixiang_system_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 登录请求DTO
 * 用于接收前端传递的登录参数
 * 支持管理员和参赛单位两种角色认证
 */
public class LoginRequest {
    
    /**
     * 机构代码
     */
    @NotBlank(message = "机构代码不能为空")
    @Size(max = 50, message = "机构代码长度不能超过50个字符")
    private String organizationCode;
    
    /**
     * 用户名/参赛单位ID
     * 当authType为ADMIN时，存储用户名
     * 当authType为TEAM时，存储参赛单位ID（String类型）
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
    
    /**
     * 认证类型
     * SUPER_ADMIN - 系统管理员认证（全局权限）
     * ORG_ADMIN - 使用单位认证（机构管理员权限）
     * TEAM - 参赛单位认证
     */
    @NotBlank(message = "认证类型不能为空")
    @Pattern(regexp = "^(SUPER_ADMIN|ORG_ADMIN|TEAM)$", message = "认证类型只能是SUPER_ADMIN、ORG_ADMIN或TEAM")
    private String authType;
    
    // 默认构造函数
    public LoginRequest() {}
    
    // 带参构造函数
    public LoginRequest(String organizationCode, String username, String password, String authType) {
        this.organizationCode = organizationCode;
        this.username = username;
        this.password = password;
        this.authType = authType;
    }
    
    // 管理员登录构造函数
    public static LoginRequest adminLogin(String organizationCode, String username, String password) {
        return new LoginRequest(organizationCode, username, password, "ADMIN");
    }
    
    // 参赛单位登录构造函数
    public static LoginRequest teamLogin(String organizationCode, Long teamId, String password) {
        return new LoginRequest(organizationCode, teamId.toString(), password, "TEAM");
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
    
    /**
     * 获取认证类型
     * @return 认证类型
     */
    public String getAuthType() {
        return authType;
    }
    
    /**
     * 设置认证类型
     * @param authType 认证类型
     */
    public void setAuthType(String authType) {
        this.authType = authType;
    }
    
    /**
     * 是否为系统管理员认证
     * @return true如果是系统管理员认证
     */
    public boolean isSuperAdminAuth() {
        return "SUPER_ADMIN".equals(authType);
    }
    
    /**
     * 是否为使用单位认证（机构管理员）
     * @return true如果是使用单位认证
     */
    public boolean isOrgAdminAuth() {
        return "ORG_ADMIN".equals(authType);
    }
    
    /**
     * 是否为参赛单位认证
     * @return true如果是参赛单位认证
     */
    public boolean isTeamAuth() {
        return "TEAM".equals(authType);
    }
    
    /**
     * 是否为管理员认证（包括系统管理员和使用单位）
     * @return true如果是管理员认证
     */
    public boolean isAdminAuth() {
        return isSuperAdminAuth() || isOrgAdminAuth();
    }
    
    /**
     * 获取参赛单位ID（仅在TEAM认证时有效）
     * @return 参赛单位ID
     * @throws NumberFormatException 如果username不是有效的数字
     */
    public Long getTeamId() {
        if (isTeamAuth()) {
            return Long.parseLong(username);
        }
        throw new IllegalStateException("当前不是参赛单位认证类型");
    }
    
    // toString方法
    @Override
    public String toString() {
        return "LoginRequest{" +
                "organizationCode='" + organizationCode + '\'' +
                ", username='" + username + '\'' +
                ", password='[PROTECTED]'" +
                ", authType='" + authType + '\'' +
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
        if (!password.equals(that.password)) return false;
        return authType.equals(that.authType);
    }
    
    // hashCode方法
    @Override
    public int hashCode() {
        int result = organizationCode.hashCode();
        result = 31 * result + username.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + authType.hashCode();
        return result;
    }
}
