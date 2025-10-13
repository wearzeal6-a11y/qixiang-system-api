package com.qixiang.qixiang_system_api.controller;

import com.qixiang.qixiang_system_api.entity.User;
import com.qixiang.qixiang_system_api.repository.UserRepository;
import com.qixiang.qixiang_system_api.service.DataInitializationService;
import com.qixiang.qixiang_system_api.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 数据管理控制器
 * 提供数据验证、测试数据管理等功能
 */
@RestController
@RequestMapping("/api/data")
@CrossOrigin(origins = "*")
public class DataManagementController {
    
    private static final Logger logger = LoggerFactory.getLogger(DataManagementController.class);
    
    @Autowired
    private DataInitializationService dataInitializationService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AuthService authService;
    
    /**
     * 获取数据统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getDataStatistics() {
        try {
            DataInitializationService.DataStatistics stats = dataInitializationService.getDataStatistics();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stats);
            response.put("message", "数据统计获取成功");
            
            logger.info("获取数据统计: {}", stats);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取数据统计失败: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取数据统计失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 重置测试数据
     */
    @PostMapping("/reset")
    public ResponseEntity<Map<String, Object>> resetTestData() {
        try {
            logger.info("开始重置测试数据...");
            dataInitializationService.resetTestData();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "测试数据重置成功");
            
            logger.info("测试数据重置完成");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("重置测试数据失败: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "重置测试数据失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 验证用户登录
     */
    @PostMapping("/verify-login")
    public ResponseEntity<Map<String, Object>> verifyLogin(@RequestBody Map<String, String> loginRequest) {
        try {
            String organizationCode = loginRequest.get("organizationCode");
            String username = loginRequest.get("username");
            String password = loginRequest.get("password");
            
            logger.info("验证用户登录: {}@{}", username, organizationCode);
            
            // 检查用户是否存在
            Optional<User> userOpt = userRepository.findByOrganizationCodeAndUsername(organizationCode, username);
            if (!userOpt.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "用户不存在");
                response.put("userExists", false);
                return ResponseEntity.ok(response);
            }
            
            User user = userOpt.get();
            
            // 检查用户状态
            boolean isActive = user.getStatus() != null && user.getStatus() == 1;
            
            // 验证密码
            boolean passwordValid = authService.validatePassword(password, user.getPassword());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", passwordValid && isActive);
            response.put("message", passwordValid && isActive ? "登录验证成功" : 
                        passwordValid ? "用户已被禁用" : "密码错误");
            response.put("userExists", true);
            response.put("isActive", isActive);
            response.put("passwordValid", passwordValid);
            response.put("userInfo", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "organizationCode", user.getOrganizationCode(),
                "role", user.getRole(),
                "status", user.getStatus(),
                "createdAt", user.getCreatedAt(),
                "lastLoginAt", user.getLastLoginAt()
            ));
            
            logger.info("用户登录验证结果: {}@{} - 成功: {}, 状态: {}, 密码: {}", 
                       username, organizationCode, passwordValid && isActive, isActive, passwordValid);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("验证用户登录失败: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "验证登录失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 获取所有用户列表
     */
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", users);
            response.put("count", users.size());
            response.put("message", "用户列表获取成功");
            
            logger.info("获取用户列表: 共 {} 个用户", users.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取用户列表失败: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取用户列表失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 根据机构代码获取用户列表
     */
    @GetMapping("/users/{organizationCode}")
    public ResponseEntity<Map<String, Object>> getUsersByOrganization(@PathVariable String organizationCode) {
        try {
            List<User> users = userRepository.findByOrganizationCode(organizationCode);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", users);
            response.put("count", users.size());
            response.put("organizationCode", organizationCode);
            response.put("message", "机构用户列表获取成功");
            
            logger.info("获取机构 {} 用户列表: 共 {} 个用户", organizationCode, users.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取机构用户列表失败: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取机构用户列表失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 创建测试用户
     */
    @PostMapping("/users")
    public ResponseEntity<Map<String, Object>> createTestUser(@RequestBody Map<String, Object> userData) {
        try {
            String organizationCode = (String) userData.get("organizationCode");
            String username = (String) userData.get("username");
            String password = (String) userData.get("password");
            String role = (String) userData.get("role");
            Integer status = (Integer) userData.get("status");
            
            // 检查用户是否已存在
            if (userRepository.existsByOrganizationCodeAndUsername(organizationCode, username)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "用户已存在");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 创建用户
            User user = new User();
            user.setOrganizationCode(organizationCode);
            user.setUsername(username);
            user.setPassword(authService.encodePassword(password));
            user.setRole(role);
            user.setStatus(status != null ? status : 1);
            
            User savedUser = userRepository.save(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", savedUser);
            response.put("message", "测试用户创建成功");
            
            logger.info("创建测试用户成功: {}@{}", username, organizationCode);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("创建测试用户失败: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "创建测试用户失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "数据管理服务运行正常");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}
