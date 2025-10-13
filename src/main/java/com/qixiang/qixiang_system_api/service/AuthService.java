package com.qixiang.qixiang_system_api.service;

import com.qixiang.qixiang_system_api.dto.LoginRequest;
import com.qixiang.qixiang_system_api.dto.LoginResponse;
import com.qixiang.qixiang_system_api.entity.User;
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
    private PasswordEncoder passwordEncoder;
    
    /**
     * 用户登录验证
     * @param loginRequest 登录请求
     * @return 登录响应，包含JWT Token
     * @throws AuthenticationException 认证异常
     */
    public LoginResponse login(LoginRequest loginRequest) {
        logger.info("用户登录尝试: organizationCode={}, username={}", 
                   loginRequest.getOrganizationCode(), loginRequest.getUsername());
        
        try {
            // 1. 查询用户
            User user = userRepository.findByOrganizationCodeAndUsername(
                    loginRequest.getOrganizationCode(), 
                    loginRequest.getUsername()
            ).orElseThrow(() -> {
                logger.warn("用户不存在: organizationCode={}, username={}", 
                           loginRequest.getOrganizationCode(), loginRequest.getUsername());
                return new AuthenticationException("用户名或密码错误");
            });
            
            // 2. 验证用户状态
            validateUserStatus(user);
            
            // 3. 验证密码
            verifyPassword(loginRequest.getPassword(), user.getPassword());
            
            // 4. 生成JWT Token
            String token = JwtUtil.generateToken(user.getUsername());
            
            // 5. 更新最后登录时间
            updateLastLoginTime(user);
            
            // 6. 构建响应
            LoginResponse response = new LoginResponse(
                    token,
                    user.getId(),
                    user.getUsername(),
                    user.getOrganizationCode(),
                    user.getRole()
            );
            
            logger.info("用户登录成功: organizationCode={}, username={}", 
                       loginRequest.getOrganizationCode(), loginRequest.getUsername());
            
            return response;
            
        } catch (AuthenticationException e) {
            logger.error("用户登录失败: organizationCode={}, username={}, error={}", 
                        loginRequest.getOrganizationCode(), loginRequest.getUsername(), e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("登录过程中发生异常: organizationCode={}, username={}, error={}", 
                        loginRequest.getOrganizationCode(), loginRequest.getUsername(), e.getMessage(), e);
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
