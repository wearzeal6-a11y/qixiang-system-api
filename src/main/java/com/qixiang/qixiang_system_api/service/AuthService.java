package com.qixiang.qixiang_system_api.service;

import com.qixiang.qixiang_system_api.dto.LoginRequest;
import com.qixiang.qixiang_system_api.dto.LoginResponse;
import com.qixiang.qixiang_system_api.entity.Team;
import com.qixiang.qixiang_system_api.entity.User;
import com.qixiang.qixiang_system_api.repository.TeamRepository;
import com.qixiang.qixiang_system_api.repository.UserRepository;
import com.qixiang.qixiang_system_api.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 认证服务类
 * 负责处理用户登录验证和Token生成
 */
@Service
@Transactional
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TeamRepository teamRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * 统一登录验证
     * 支持管理员和参赛单位两种角色的认证
     * @param loginRequest 登录请求
     * @return 登录响应，包含JWT Token
     * @throws AuthenticationException 认证异常
     */
    public LoginResponse login(LoginRequest loginRequest) {
        logger.info("登录尝试: organizationCode={}, username={}, authType={}", 
                   loginRequest.getOrganizationCode(), loginRequest.getUsername(), loginRequest.getAuthType());
        
        try {
            if (loginRequest.isSuperAdminAuth()) {
                return superAdminLogin(loginRequest);
            } else if (loginRequest.isOrgAdminAuth()) {
                return orgAdminLogin(loginRequest);
            } else if (loginRequest.isTeamAuth()) {
                return teamLogin(loginRequest);
            } else {
                throw new AuthenticationException("不支持的认证类型");
            }
        } catch (AuthenticationException e) {
            logger.error("登录失败: organizationCode={}, username={}, authType={}, error={}", 
                        loginRequest.getOrganizationCode(), loginRequest.getUsername(), 
                        loginRequest.getAuthType(), e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("登录过程中发生异常: organizationCode={}, username={}, authType={}, error={}", 
                        loginRequest.getOrganizationCode(), loginRequest.getUsername(), 
                        loginRequest.getAuthType(), e.getMessage(), e);
            throw new AuthenticationException("登录失败，请稍后重试");
        }
    }
    
    /**
     * 系统管理员登录认证
     * @param loginRequest 登录请求
     * @return 登录响应
     * @throws AuthenticationException 认证异常
     */
    private LoginResponse superAdminLogin(LoginRequest loginRequest) {
        logger.info("系统管理员登录尝试: organizationCode={}, username={}", 
                   loginRequest.getOrganizationCode(), loginRequest.getUsername());
        
        // 1. 查询系统管理员用户（organizationCode可以为SYSTEM或任意值）
        User user = userRepository.findByOrganizationCodeAndUsername(
                loginRequest.getOrganizationCode(), 
                loginRequest.getUsername()
        ).orElseThrow(() -> {
            logger.warn("系统管理员用户不存在: organizationCode={}, username={}", 
                       loginRequest.getOrganizationCode(), loginRequest.getUsername());
            return new AuthenticationException("用户名或密码错误");
        });
        
        // 2. 验证用户角色
        if (!"SUPER_ADMIN".equals(user.getRole())) {
            logger.warn("用户不是系统管理员: userId={}, role={}", user.getId(), user.getRole());
            throw new AuthenticationException("权限不足");
        }
        
        // 3. 验证用户状态
        validateUserStatus(user);
        
        // 4. 验证密码
        verifyPassword(loginRequest.getPassword(), user.getPassword());
        
        // 5. 生成JWT Token（包含角色信息）
        String token = JwtUtil.generateToken(user.getUsername(), "SUPER_ADMIN", user.getOrganizationCode());
        
        // 6. 更新最后登录时间
        updateLastLoginTime(user);
        
        // 7. 构建响应
        LoginResponse response = new LoginResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getOrganizationCode(),
                "SUPER_ADMIN"
        );
        
        logger.info("系统管理员登录成功: organizationCode={}, username={}", 
                   loginRequest.getOrganizationCode(), loginRequest.getUsername());
        
        return response;
    }
    
    /**
     * 使用单位登录认证（机构管理员）
     * @param loginRequest 登录请求
     * @return 登录响应
     * @throws AuthenticationException 认证异常
     */
    private LoginResponse orgAdminLogin(LoginRequest loginRequest) {
        logger.info("使用单位登录尝试: organizationCode={}, username={}", 
                   loginRequest.getOrganizationCode(), loginRequest.getUsername());
        
        // 1. 查询使用单位用户
        User user = userRepository.findByOrganizationCodeAndUsername(
                loginRequest.getOrganizationCode(), 
                loginRequest.getUsername()
        ).orElseThrow(() -> {
            logger.warn("使用单位用户不存在: organizationCode={}, username={}", 
                       loginRequest.getOrganizationCode(), loginRequest.getUsername());
            return new AuthenticationException("用户名或密码错误");
        });
        
        // 2. 验证用户角色
        if (!"ORG_ADMIN".equals(user.getRole())) {
            logger.warn("用户不是使用单位管理员: userId={}, role={}", user.getId(), user.getRole());
            throw new AuthenticationException("权限不足");
        }
        
        // 3. 验证用户状态
        validateUserStatus(user);
        
        // 4. 验证密码
        verifyPassword(loginRequest.getPassword(), user.getPassword());
        
        // 5. 生成JWT Token（包含角色信息）
        String token = JwtUtil.generateToken(user.getUsername(), "ORG_ADMIN", user.getOrganizationCode());
        
        // 6. 更新最后登录时间
        updateLastLoginTime(user);
        
        // 7. 构建响应
        LoginResponse response = new LoginResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getOrganizationCode(),
                "ORG_ADMIN"
        );
        
        logger.info("使用单位登录成功: organizationCode={}, username={}", 
                   loginRequest.getOrganizationCode(), loginRequest.getUsername());
        
        return response;
    }
    
    /**
     * 参赛单位登录认证
     * @param loginRequest 登录请求
     * @return 登录响应
     * @throws AuthenticationException 认证异常
     */
    private LoginResponse teamLogin(LoginRequest loginRequest) {
        logger.info("参赛单位登录尝试: organizationCode={}, teamId={}", 
                   loginRequest.getOrganizationCode(), loginRequest.getUsername());
        
        try {
            // 步骤1: 解析teamId
            logger.info("步骤1: 解析teamId");
            Long teamId = loginRequest.getTeamId();
            logger.info("✅ 解析的teamId: {}", teamId);
            
            // 步骤2: 查询参赛单位
            logger.info("步骤2: 查询参赛单位");
            Team team = teamRepository.findById(teamId).orElseThrow(() -> {
                logger.warn("参赛单位不存在: teamId={}", teamId);
                return new AuthenticationException("参赛单位不存在或密码错误");
            });
            logger.info("✅ 找到参赛单位: id={}, name={}, status={}, orgCode={}", team.getId(), team.getName(), team.getStatus(), team.getOrgCode());
            
            // 步骤3: 验证参赛单位归属机构
            logger.info("步骤3: 验证参赛单位归属机构");
            if (team.getOrgCode() == null || !team.getOrgCode().equals(loginRequest.getOrganizationCode())) {
                logger.warn("参赛单位不属于指定机构: teamId={}, teamOrgCode={}, requestOrgCode={}", 
                           teamId, team.getOrgCode(), loginRequest.getOrganizationCode());
                throw new AuthenticationException("参赛单位不属于指定机构");
            }
            logger.info("✅ 参赛单位归属机构验证通过");
            
            // 步骤4: 验证参赛单位状态
            logger.info("步骤4: 验证参赛单位状态");
            validateTeamStatus(team);
            logger.info("✅ 参赛单位状态验证通过");
            
            // 步骤4: 验证密码
            logger.info("步骤4: 验证密码");
            logger.info("输入密码长度: {}", loginRequest.getPassword().length());
            logger.info("存储密码: {}", team.getPassword());
            
            boolean passwordMatch = passwordEncoder.matches(loginRequest.getPassword(), team.getPassword());
            logger.info("✅ 密码验证结果: {}", passwordMatch);
            
            if (!passwordMatch) {
                logger.warn("❌ 密码验证失败: teamId={}", teamId);
                throw new AuthenticationException("用户名或密码错误");
            }
            
            // 步骤5: 生成JWT Token
            logger.info("步骤5: 生成JWT Token");
            String token = JwtUtil.generateToken(teamId.toString(), "TEAM", null);
            logger.info("✅ JWT Token生成成功: {}", token.substring(0, Math.min(50, token.length())) + "...");
            
            // 步骤6: 构建响应
            logger.info("步骤6: 构建响应");
            LoginResponse response = new LoginResponse(
                    token,
                    team.getId(),
                    team.getName(),
                    team.getOrgCode(), // 返回参赛单位的机构代码
                    "TEAM"
            );
            logger.info("✅ 响应构建完成");
            
            logger.info("🎉 参赛单位登录成功: teamId={}, teamName={}", teamId, team.getName());
            
            return response;
            
        } catch (NumberFormatException e) {
            logger.error("❌ 参赛单位ID格式错误: username={}", loginRequest.getUsername());
            throw new AuthenticationException("参赛单位ID格式错误");
        } catch (AuthenticationException e) {
            logger.error("❌ 认证异常: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("❌ 参赛单位登录过程中发生未预期异常: teamId={}, error={}", 
                        loginRequest.getUsername(), e.getMessage(), e);
            throw new AuthenticationException("登录失败，请稍后重试");
        }
    }
    
    /**
     * 验证用户状态
     * @param user 用户实体
     * @throws AuthenticationException 用户状态异常
     */
    private void validateUserStatus(User user) {
        if (user.getStatus() == null || user.getStatus() != 1) {
            logger.warn("用户账号已被禁用: userId={}, username={}", user.getId(), user.getUsername());
            throw new AuthenticationException("账号已被禁用，请联系管理员");
        }
    }
    
    /**
     * 验证参赛单位状态
     * @param team 参赛单位实体
     * @throws AuthenticationException 参赛单位状态异常
     */
    private void validateTeamStatus(Team team) {
        if (!"ACTIVE".equals(team.getStatus())) {
            logger.warn("参赛单位已被禁用: teamId={}, teamName={}", team.getId(), team.getName());
            throw new AuthenticationException("参赛单位已被禁用，请联系管理员");
        }
    }
    
    /**
     * 验证密码
     * @param rawPassword 原始密码
     * @param encodedPassword 加密密码
     * @throws AuthenticationException 密码验证异常
     */
    private void verifyPassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            logger.warn("密码验证失败");
            throw new AuthenticationException("用户名或密码错误");
        }
    }
    
    /**
     * 更新用户最后登录时间
     * @param user 用户实体
     */
    private void updateLastLoginTime(User user) {
        try {
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);
            logger.debug("更新用户最后登录时间: userId={}", user.getId());
        } catch (Exception e) {
            logger.error("更新用户最后登录时间失败: userId={}, error={}", user.getId(), e.getMessage(), e);
            // 不影响登录流程，只记录日志
        }
    }
    
    /**
     * 验证Token有效性
     * @param token JWT Token
     * @return 用户信息
     * @throws AuthenticationException Token验证异常
     */
    public User validateToken(String token) {
        try {
            String username = JwtUtil.validateTokenAndGetUsername(token);
            if (username == null) {
                throw new AuthenticationException("Token无效或已过期");
            }
            
            // 这里可以根据需要查询用户信息并返回
            // 为了简化，这里只返回用户名
            User user = new User();
            user.setUsername(username);
            return user;
            
        } catch (Exception e) {
            logger.error("Token验证失败: error={}", e.getMessage());
            throw new AuthenticationException("Token无效或已过期");
        }
    }
    
    /**
     * 刷新Token
     * @param token 旧Token
     * @return 新Token
     * @throws AuthenticationException 刷新Token异常
     */
    public String refreshToken(String token) {
        try {
            String newToken = JwtUtil.refreshToken(token);
            if (newToken == null) {
                throw new AuthenticationException("Token刷新失败");
            }
            logger.info("Token刷新成功");
            return newToken;
        } catch (Exception e) {
            logger.error("Token刷新失败: error={}", e.getMessage());
            throw new AuthenticationException("Token刷新失败");
        }
    }
    
    /**
     * 验证密码
     * @param rawPassword 原始密码
     * @param encodedPassword 加密密码
     * @return 是否匹配
     */
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        try {
            return passwordEncoder.matches(rawPassword, encodedPassword);
        } catch (Exception e) {
            logger.error("密码验证失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 加密密码
     * @param rawPassword 原始密码
     * @return 加密后的密码
     */
    public String encodePassword(String rawPassword) {
        try {
            return passwordEncoder.encode(rawPassword);
        } catch (Exception e) {
            logger.error("密码加密失败: {}", e.getMessage());
            throw new AuthenticationException("密码加密失败");
        }
    }
    
    /**
     * 自定义认证异常
     */
    public static class AuthenticationException extends RuntimeException {
        public AuthenticationException(String message) {
            super(message);
        }
        
        public AuthenticationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
