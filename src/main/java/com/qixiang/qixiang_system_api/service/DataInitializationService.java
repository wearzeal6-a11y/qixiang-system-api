package com.qixiang.qixiang_system_api.service;

import com.qixiang.qixiang_system_api.entity.User;
import com.qixiang.qixiang_system_api.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 数据初始化服务
 * 负责在应用启动时初始化测试数据，并提供数据验证功能
 */
@Service
@Transactional
public class DataInitializationService implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializationService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * 应用启动时执行数据初始化
     */
    @Override
    public void run(String... args) throws Exception {
        logger.info("开始执行数据初始化...");
        
        // 验证并初始化测试用户
        initializeTestUsers();
        
        // 验证数据完整性
        validateDataIntegrity();
        
        logger.info("数据初始化完成");
    }
    
    /**
     * 初始化测试用户数据
     */
    private void initializeTestUsers() {
        logger.info("开始初始化测试用户...");
        
        // 定义测试用户列表
        List<TestUser> testUsers = Arrays.asList(
            new TestUser("TEST001", "admin", "password", "ADMIN", "管理员用户"),
            new TestUser("TEST001", "user1", "password", "USER", "普通用户1"),
            new TestUser("TEST002", "manager", "password", "MANAGER", "经理用户"),
            new TestUser("TEST001", "testuser", "password", "USER", "测试用户"),
            new TestUser("TEST001", "disabled", "password", "USER", "禁用用户")
        );
        
        for (TestUser testUser : testUsers) {
            try {
                // 检查用户是否已存在
                if (!userRepository.existsByOrganizationCodeAndUsername(
                        testUser.organizationCode, testUser.username)) {
                    
                    // 创建新用户
                    User user = new User();
                    user.setOrganizationCode(testUser.organizationCode);
                    user.setUsername(testUser.username);
                    user.setPassword(passwordEncoder.encode(testUser.password));
                    user.setRole(testUser.role);
                    user.setStatus("disabled".equals(testUser.username) ? 0 : 1);
                    user.setCreatedAt(LocalDateTime.now());
                    user.setUpdatedAt(LocalDateTime.now());
                    
                    userRepository.save(user);
                    logger.info("创建测试用户: {}@{} ({})", 
                               testUser.username, testUser.organizationCode, testUser.description);
                } else {
                    logger.debug("用户已存在，跳过: {}@{}", testUser.username, testUser.organizationCode);
                }
            } catch (Exception e) {
                logger.error("创建测试用户失败: {}@{}, error: {}", 
                           testUser.username, testUser.organizationCode, e.getMessage(), e);
            }
        }
    }
    
    /**
     * 验证数据完整性
     */
    private void validateDataIntegrity() {
        logger.info("开始验证数据完整性...");
        
        try {
            // 统计用户总数
            long totalUsers = userRepository.count();
            logger.info("数据库中用户总数: {}", totalUsers);
            
            // 统计各机构用户数量
            List<String> organizations = Arrays.asList("TEST001", "TEST002");
            for (String orgCode : organizations) {
                long orgUserCount = userRepository.countByOrganizationCode(orgCode);
                long activeUserCount = userRepository.countActiveUsersByOrganizationCode(orgCode);
                logger.info("机构 {} - 总用户数: {}, 启用用户数: {}", orgCode, orgUserCount, activeUserCount);
            }
            
            // 验证关键用户是否存在
            validateKeyUsers();
            
        } catch (Exception e) {
            logger.error("数据完整性验证失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 验证关键用户是否存在
     */
    private void validateKeyUsers() {
        // 验证admin用户
        if (!userRepository.existsByOrganizationCodeAndUsername("TEST001", "admin")) {
            logger.warn("关键用户缺失: admin@TEST001");
        } else {
            User admin = userRepository.findByOrganizationCodeAndUsername("TEST001", "admin").orElse(null);
            if (admin != null) {
                logger.info("关键用户验证通过: admin@TEST001 (状态: {}, 角色: {})", 
                           admin.getStatus(), admin.getRole());
            }
        }
    }
    
    /**
     * 重置测试数据
     */
    public void resetTestData() {
        logger.info("开始重置测试数据...");
        
        try {
            // 删除所有测试用户
            List<String> testOrgs = Arrays.asList("TEST001", "TEST002");
            for (String orgCode : testOrgs) {
                List<User> users = userRepository.findByOrganizationCode(orgCode);
                userRepository.deleteAll(users);
                logger.info("删除机构 {} 的 {} 个用户", orgCode, users.size());
            }
            
            // 重新初始化
            initializeTestUsers();
            validateDataIntegrity();
            
            logger.info("测试数据重置完成");
            
        } catch (Exception e) {
            logger.error("重置测试数据失败: {}", e.getMessage(), e);
            throw new RuntimeException("重置测试数据失败", e);
        }
    }
    
    /**
     * 获取数据统计信息
     */
    public DataStatistics getDataStatistics() {
        DataStatistics stats = new DataStatistics();
        stats.totalUsers = userRepository.count();
        stats.activeUsers = userRepository.countActiveUsersByOrganizationCode("TEST001") + 
                           userRepository.countActiveUsersByOrganizationCode("TEST002");
        
        // 统计各角色用户数量
        stats.adminCount = userRepository.findByRole("ADMIN").size();
        stats.userCount = userRepository.findByRole("USER").size();
        stats.managerCount = userRepository.findByRole("MANAGER").size();
        
        return stats;
    }
    
    /**
     * 测试用户定义
     */
    private static class TestUser {
        String organizationCode;
        String username;
        String password;
        String role;
        String description;
        
        TestUser(String organizationCode, String username, String password, String role, String description) {
            this.organizationCode = organizationCode;
            this.username = username;
            this.password = password;
            this.role = role;
            this.description = description;
        }
    }
    
    /**
     * 数据统计信息
     */
    public static class DataStatistics {
        public long totalUsers;
        public long activeUsers;
        public int adminCount;
        public int userCount;
        public int managerCount;
        
        @Override
        public String toString() {
            return String.format("DataStatistics{总用户数=%d, 启用用户数=%d, 管理员=%d, 普通用户=%d, 经理=%d}", 
                               totalUsers, activeUsers, adminCount, userCount, managerCount);
        }
    }
}
