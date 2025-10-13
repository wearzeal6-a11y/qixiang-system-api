package com.qixiang.qixiang_system_api.controller;

import com.qixiang.qixiang_system_api.dto.LoginRequest;
import com.qixiang.qixiang_system_api.dto.LoginResponse;
import com.qixiang.qixiang_system_api.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * 提供用户登录、Token验证等认证相关的HTTP接口
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private AuthService authService;
    
    /**
     * 用户登录接口
     * @param loginRequest 登录请求参数
     * @return JWT Token响应
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("收到登录请求: organizationCode={}, username={}", 
                   loginRequest.getOrganizationCode(), loginRequest.getUsername());
        
        try {
            // 调用认证服务处理登录
            LoginResponse loginResponse = authService.login(loginRequest);
            
            // 构建标准响应格式
            Map<String, Object> response = new HashMap<>();
            response.put("token", loginResponse.getToken());
            response.put("tokenType", loginResponse.getTokenType());
            response.put("expiresAt", loginResponse.getExpiresAt());
            response.put("user", Map.of(
                "id", loginResponse.getUserId(),
                "username", loginResponse.getUsername(),
                "organizationCode", loginResponse.getOrganizationCode(),
                "role", loginResponse.getRole()
            ));
            
            logger.info("用户登录成功: organizationCode={}, username={}", 
                       loginRequest.getOrganizationCode(), loginRequest.getUsername());
            
            return ResponseEntity.ok(response);
            
        } catch (AuthService.AuthenticationException e) {
            logger.warn("登录失败: organizationCode={}, username={}, error={}", 
                       loginRequest.getOrganizationCode(), loginRequest.getUsername(), e.getMessage());
            
            return createErrorResponse(HttpStatus.UNAUTHORIZED, e.getMessage());
            
        } catch (Exception e) {
            logger.error("登录过程中发生系统异常: organizationCode={}, username={}, error={}", 
                        loginRequest.getOrganizationCode(), loginRequest.getUsername(), e.getMessage(), e);
            
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "登录失败，请稍后重试");
        }
    }
    
    /**
     * 验证Token有效性接口
     * @param authorization Authorization头中的Token
     * @return 验证结果
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authorization) {
        logger.info("收到Token验证请求");
        
        try {
            // 提取Bearer Token
            String token = extractTokenFromAuthorization(authorization);
            if (token == null) {
                return createErrorResponse(HttpStatus.BAD_REQUEST, "Authorization头格式错误");
            }
            
            // 验证Token
            authService.validateToken(token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("message", "Token有效");
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (AuthService.AuthenticationException e) {
            logger.warn("Token验证失败: {}", e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("message", e.getMessage());
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            
        } catch (Exception e) {
            logger.error("Token验证过程中发生系统异常: {}", e.getMessage(), e);
            
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Token验证失败，请稍后重试");
        }
    }
    
    /**
     * 刷新Token接口
     * @param request 包含旧Token的请求体
     * @return 新Token
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        logger.info("收到Token刷新请求");
        
        try {
            String oldToken = request.get("token");
            if (oldToken == null || oldToken.trim().isEmpty()) {
                return createErrorResponse(HttpStatus.BAD_REQUEST, "Token不能为空");
            }
            
            // 刷新Token
            String newToken = authService.refreshToken(oldToken);
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", newToken);
            response.put("tokenType", "Bearer");
            response.put("timestamp", LocalDateTime.now());
            
            logger.info("Token刷新成功");
            
            return ResponseEntity.ok(response);
            
        } catch (AuthService.AuthenticationException e) {
            logger.warn("Token刷新失败: {}", e.getMessage());
            
            return createErrorResponse(HttpStatus.UNAUTHORIZED, e.getMessage());
            
        } catch (Exception e) {
            logger.error("Token刷新过程中发生系统异常: {}", e.getMessage(), e);
            
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Token刷新失败，请稍后重试");
        }
    }
    
    /**
     * 登出接口（可选实现）
     * @param authorization Authorization头中的Token
     * @return 登出结果
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        logger.info("收到登出请求");
        
        try {
            String token = extractTokenFromAuthorization(authorization);
            if (token != null) {
                // 这里可以实现Token黑名单机制
                logger.info("用户登出成功");
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "登出成功");
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("登出过程中发生异常: {}", e.getMessage(), e);
            
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "登出失败，请稍后重试");
        }
    }
    
    /**
     * 从Authorization头中提取Token
     * @param authorization Authorization头
     * @return Token字符串，如果格式错误返回null
     */
    private String extractTokenFromAuthorization(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        return authorization.substring(7);
    }
    
    /**
     * 创建错误响应
     * @param status HTTP状态码
     * @param message 错误消息
     * @return 错误响应实体
     */
    private ResponseEntity<Map<String, Object>> createErrorResponse(HttpStatus status, String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", true);
        errorResponse.put("message", message);
        errorResponse.put("status", status.value());
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("path", "/api/auth");
        
        return ResponseEntity.status(status).body(errorResponse);
    }
}
