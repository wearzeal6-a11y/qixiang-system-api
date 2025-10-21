package com.qixiang.qixiang_system_api.controller;

import com.qixiang.qixiang_system_api.dto.LoginRequest;
import com.qixiang.qixiang_system_api.dto.LoginResponse;
import com.qixiang.qixiang_system_api.entity.Team;
import com.qixiang.qixiang_system_api.service.AuthService;
import com.qixiang.qixiang_system_api.service.TeamService;
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
    
    @Autowired
    private TeamService teamService;
    
    /**
     * 统一登录接口（支持管理员和参赛单位）
     * @param loginRequest 登录请求参数
     * @return JWT Token响应
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        logger.info("收到登录请求: organizationCode={}, username={}, authType={}", 
                   loginRequest.getOrganizationCode(), loginRequest.getUsername(), loginRequest.getAuthType());
        
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
                "organizationCode", loginResponse.getOrganizationCode() != null ? loginResponse.getOrganizationCode() : "",
                "role", loginResponse.getRole()
            ));
            
            logger.info("登录成功: organizationCode={}, username={}, authType={}, role={}", 
                       loginRequest.getOrganizationCode(), loginRequest.getUsername(), 
                       loginRequest.getAuthType(), loginResponse.getRole());
            
            return ResponseEntity.ok(response);
            
        } catch (AuthService.AuthenticationException e) {
            logger.warn("登录失败: organizationCode={}, username={}, authType={}, error={}", 
                       loginRequest.getOrganizationCode(), loginRequest.getUsername(), 
                       loginRequest.getAuthType(), e.getMessage());
            
            return createErrorResponse(HttpStatus.UNAUTHORIZED, e.getMessage());
            
        } catch (Exception e) {
            logger.error("登录过程中发生系统异常: organizationCode={}, username={}, authType={}, error={}", 
                        loginRequest.getOrganizationCode(), loginRequest.getUsername(), 
                        loginRequest.getAuthType(), e.getMessage(), e);
            
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
     * 简单测试POST请求
     * @return 测试结果
     */
    @PostMapping("/test-post")
    public ResponseEntity<?> testPost() {
        logger.info("收到简单POST请求测试");
        Map<String, Object> response = new HashMap<>();
        response.put("message", "POST请求成功");
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    /**
     * 测试LoginRequest反序列化
     * @param loginRequest 登录请求对象
     * @return 测试结果
     */
    @PostMapping("/test-login-request")
    public ResponseEntity<?> testLoginRequest(@RequestBody LoginRequest loginRequest) {
        logger.info("收到LoginRequest测试: {}", loginRequest);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "LoginRequest反序列化成功");
        response.put("loginRequest", loginRequest);
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    /**
     * 直接测试参赛单位登录（绕过AuthService）
     * @param teamId 参赛单位ID
     * @return 登录结果
     */
    @PostMapping("/direct-team-login/{teamId}")
    public ResponseEntity<?> directTeamLogin(@PathVariable Long teamId) {
        logger.info("直接测试参赛单位登录: teamId={}", teamId);
        
        try {
            // 直接在Controller中处理参赛单位登录
            Team team = teamService.getTeamById(teamId);
            logger.info("查询到参赛单位: {}", team.getName());
            
            // 验证状态
            if (!"ACTIVE".equals(team.getStatus())) {
                return createErrorResponse(HttpStatus.UNAUTHORIZED, "参赛单位状态不活跃");
            }
            
            // 验证密码
            String testPassword = "admin123";
            boolean passwordMatch = authService.validatePassword(testPassword, team.getPassword());
            if (!passwordMatch) {
                return createErrorResponse(HttpStatus.UNAUTHORIZED, "密码错误");
            }
            
            // 生成Token
            String token = com.qixiang.qixiang_system_api.util.JwtUtil.generateToken(teamId.toString(), "TEAM", null);
            
            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("tokenType", "Bearer");
            response.put("expiresAt", LocalDateTime.now().plusHours(24));
            response.put("user", Map.of(
                "id", teamId,
                "username", team.getName(),
                "organizationCode", "TEST001",
                "role", "TEAM"
            ));
            
            logger.info("直接参赛单位登录成功: teamId={}, teamName={}", teamId, team.getName());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("直接参赛单位登录失败: teamId={}, error={}", teamId, e.getMessage(), e);
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "登录失败: " + e.getMessage());
        }
    }

    /**
     * 简单测试参数接收
     * @param organizationCode 机构代码
     * @param username 用户名
     * @param password 密码
     * @param authType 认证类型
     * @return 测试结果
     */
    @GetMapping("/test-params")
    public ResponseEntity<?> testParams(
            @RequestParam String organizationCode,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String authType) {
        
        logger.info("收到参数测试: organizationCode={}, username={}, password={}, authType={}", 
                   organizationCode, username, password, authType);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "参数接收成功");
        response.put("params", Map.of(
            "organizationCode", organizationCode,
            "username", username,
            "password", password,
            "authType", authType
        ));
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 通过GET请求测试参赛单位登录（模拟POST参数）
     * @param organizationCode 机构代码
     * @param username 用户名
     * @param password 密码
     * @param authType 认证类型
     * @return 登录结果
     */
    @GetMapping("/test-login-get")
    public ResponseEntity<?> testLoginGet(
            @RequestParam String organizationCode,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String authType) {
        
        logger.info("收到GET测试登录请求: organizationCode={}, username={}, authType={}", 
                   organizationCode, username, authType);
        
        Map<String, Object> debugInfo = new HashMap<>();
        
        try {
            // 步骤1: 创建LoginRequest对象
            logger.info("步骤1: 创建LoginRequest对象");
            LoginRequest loginRequest = new LoginRequest(organizationCode, username, password, authType);
            debugInfo.put("step1", "✅ LoginRequest创建成功");
            debugInfo.put("loginRequest", loginRequest.toString());
            
            // 步骤2: 验证认证类型
            logger.info("步骤2: 验证认证类型");
            boolean isTeamAuth = loginRequest.isTeamAuth();
            debugInfo.put("step2", isTeamAuth ? "✅ 认证类型为TEAM" : "❌ 认证类型不是TEAM");
            
            if (isTeamAuth) {
                // 步骤3: 解析teamId
                logger.info("步骤3: 解析teamId");
                try {
                    Long teamId = loginRequest.getTeamId();
                    debugInfo.put("step3", "✅ teamId解析成功: " + teamId);
                    
                    // 步骤4: 查询参赛单位
                    logger.info("步骤4: 查询参赛单位");
                    try {
                        Team team = teamService.getTeamById(teamId);
                        debugInfo.put("step4", "✅ 参赛单位查询成功: " + team.getName());
                        debugInfo.put("teamInfo", Map.of(
                            "id", team.getId(),
                            "name", team.getName(),
                            "status", team.getStatus(),
                            "hasPassword", team.getPassword() != null && !team.getPassword().isEmpty()
                        ));
                        
                        // 步骤5: 验证密码
                        logger.info("步骤5: 验证密码");
                        boolean passwordMatch = authService.validatePassword(password, team.getPassword());
                        debugInfo.put("step5", passwordMatch ? "✅ 密码验证成功" : "❌ 密码验证失败");
                        debugInfo.put("passwordTest", Map.of(
                            "inputPassword", password,
                            "inputLength", password.length(),
                            "storedPassword", team.getPassword(),
                            "storedLength", team.getPassword() != null ? team.getPassword().length() : 0
                        ));
                        
                    } catch (Exception e) {
                        debugInfo.put("step4", "❌ 参赛单位查询失败: " + e.getMessage());
                        throw e;
                    }
                    
                } catch (NumberFormatException e) {
                    debugInfo.put("step3", "❌ teamId解析失败: " + e.getMessage());
                    throw e;
                }
            }
            
            // 步骤6: 调用认证服务处理登录
            logger.info("步骤6: 调用认证服务处理登录");
            LoginResponse loginResponse = authService.login(loginRequest);
            debugInfo.put("step6", "✅ 认证服务登录成功");
            
            // 构建成功响应
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "GET测试登录成功");
            response.put("debugInfo", debugInfo);
            response.put("token", loginResponse.getToken());
            response.put("tokenType", loginResponse.getTokenType());
            response.put("expiresAt", loginResponse.getExpiresAt() != null ? loginResponse.getExpiresAt().toString() : "");
            response.put("user", Map.of(
                "id", loginResponse.getUserId(),
                "username", loginResponse.getUsername(),
                "organizationCode", loginResponse.getOrganizationCode() != null ? loginResponse.getOrganizationCode() : "",
                "role", loginResponse.getRole()
            ));
            
            logger.info("GET测试登录成功: organizationCode={}, username={}, role={}", 
                       organizationCode, username, loginResponse.getRole());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("GET测试登录失败: organizationCode={}, username={}, error={}", 
                        organizationCode, username, e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "GET测试登录失败: " + e.getMessage());
            response.put("debugInfo", debugInfo);
            response.put("errorDetails", e.getClass().getSimpleName() + ": " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 直接测试AuthService teamLogin方法
     * @param teamId 参赛单位ID
     * @return 测试结果
     */
    @GetMapping("/test-auth-service-team/{teamId}")
    public ResponseEntity<?> testAuthServiceTeam(@PathVariable Long teamId) {
        logger.info("直接测试AuthService teamLogin: teamId={}", teamId);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            LoginRequest loginRequest = new LoginRequest("TEST001", teamId.toString(), "admin123", "TEAM");
            logger.info("创建LoginRequest: {}", loginRequest);
            
            LoginResponse loginResponse = authService.login(loginRequest);
            logger.info("AuthService teamLogin成功");
            
            result.put("success", true);
            result.put("message", "AuthService teamLogin成功");
            result.put("loginResponse", Map.of(
                "token", loginResponse.getToken() != null ? "TOKEN_OK" : "TOKEN_NULL",
                "tokenType", loginResponse.getTokenType(),
                "expiresAt", loginResponse.getExpiresAt() != null ? loginResponse.getExpiresAt().toString() : "EXPIRES_AT_NULL",
                "userId", loginResponse.getUserId(),
                "username", loginResponse.getUsername(),
                "organizationCode", loginResponse.getOrganizationCode(),
                "role", loginResponse.getRole()
            ));
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("AuthService teamLogin失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "AuthService teamLogin失败: " + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 测试参赛单位登录步骤
     * @param teamId 参赛单位ID
     * @return 测试结果
     */
    @GetMapping("/test-team-login/{teamId}")
    public ResponseEntity<?> testTeamLoginSteps(@PathVariable Long teamId) {
        logger.info("测试参赛单位登录步骤: teamId={}", teamId);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 步骤1: 查询参赛单位
            logger.info("步骤1: 查询参赛单位");
            Team team = teamService.getTeamById(teamId);
            result.put("step1", "✅ 参赛单位查询成功: " + team.getName());
            
            // 步骤2: 验证状态
            logger.info("步骤2: 验证参赛单位状态");
            if (!"ACTIVE".equals(team.getStatus())) {
                result.put("step2", "❌ 参赛单位状态不活跃: " + team.getStatus());
                return ResponseEntity.ok(result);
            }
            result.put("step2", "✅ 参赛单位状态验证通过");
            
            // 步骤3: 测试密码验证
            logger.info("步骤3: 测试密码验证");
            String testPassword = "admin123";
            boolean passwordMatch = authService.validatePassword(testPassword, team.getPassword());
            result.put("step3", passwordMatch ? "✅ 密码验证成功" : "❌ 密码验证失败");
            
            // 步骤4: 测试JWT生成
            logger.info("步骤4: 测试JWT生成");
            try {
                String token = com.qixiang.qixiang_system_api.util.JwtUtil.generateToken(teamId.toString(), "TEAM", null);
                result.put("step4", "✅ JWT生成成功: " + token.substring(0, Math.min(50, token.length())) + "...");
            } catch (Exception e) {
                result.put("step4", "❌ JWT生成失败: " + e.getMessage());
            }
            
            // 步骤5: 测试完整的登录流程
            logger.info("步骤5: 测试完整的登录流程");
            try {
                LoginRequest loginRequest = new LoginRequest("TEST001", teamId.toString(), "admin123", "TEAM");
                LoginResponse loginResponse = authService.login(loginRequest);
                result.put("step5", "✅ 完整登录流程成功: " + loginResponse.getUsername());
            } catch (Exception e) {
                result.put("step5", "❌ 完整登录流程失败: " + e.getMessage());
                logger.error("完整登录流程异常: {}", e.getMessage(), e);
            }
            
            result.put("overall", "✅ 所有步骤测试完成");
            
        } catch (Exception e) {
            logger.error("测试过程中发生异常: {}", e.getMessage(), e);
            result.put("error", "❌ 测试失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
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
