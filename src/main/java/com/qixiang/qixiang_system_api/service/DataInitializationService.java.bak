package com.qixiang.qixiang_system_api.service;

import com.qixiang.qixiang_system_api.entity.Group;
import com.qixiang.qixiang_system_api.entity.SportsMeet;
import com.qixiang.qixiang_system_api.entity.Team;
import com.qixiang.qixiang_system_api.entity.User;
import com.qixiang.qixiang_system_api.repository.GroupRepository;
import com.qixiang.qixiang_system_api.repository.SportsMeetRepository;
import com.qixiang.qixiang_system_api.repository.TeamRepository;
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
    private TeamRepository teamRepository;
    
    @Autowired
    private SportsMeetRepository sportsMeetRepository;
    
    @Autowired
    private GroupRepository groupRepository;
    
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
        
        // 验证并初始化测试运动会
        initializeTestSportsMeets();
        
        // 验证并初始化竞赛组别
        initializeTestGroups();
        
        // 验证并初始化测试参赛单位
        initializeTestTeams();
        
        // 验证数据完整性
        validateDataIntegrity();
        
        logger.info("数据初始化完成");
    }
    
    /**
     * 初始化测试用户数据
     */
    private void initializeTestUsers() {
        logger.info("开始初始化测试用户...");
        
        // 定义测试用户列表 - 三角色权限体系
        List<TestUser> testUsers = Arrays.asList(
            // 系统管理员
            new TestUser("SYSTEM", "super_admin", "admin123", "SUPER_ADMIN", "系统管理员"),
            
            // 使用单位（机构管理员）
            new TestUser("TEST001", "org_admin", "admin123", "ORG_ADMIN", "TEST001机构管理员"),
            new TestUser("TEAM001", "org_admin", "admin123", "ORG_ADMIN", "TEAM001机构管理员"),
            
            // 普通用户（预留）
            new TestUser("TEST001", "user1", "password", "USER", "普通用户1"),
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
     * 初始化测试运动会数据
     */
    private void initializeTestSportsMeets() {
        logger.info("开始初始化测试运动会...");
        
        // 定义测试运动会列表
        List<TestSportsMeet> testSportsMeets = Arrays.asList(
            new TestSportsMeet("测试运动会001", "MEET001", "TEST001", "ONGOING"),
            new TestSportsMeet("测试运动会002", "MEET002", "TEST001", "REGISTRATION")
        );
        
        for (TestSportsMeet testSportsMeet : testSportsMeets) {
            try {
                // 检查运动会是否已存在
                if (!sportsMeetRepository.existsByMeetCodeAndOrgCode(
                        testSportsMeet.meetCode, testSportsMeet.orgCode, null)) {
                    
                    // 创建新运动会
                    SportsMeet sportsMeet = new SportsMeet();
                    sportsMeet.setName(testSportsMeet.name);
                    sportsMeet.setMeetCode(testSportsMeet.meetCode);
                    sportsMeet.setOrgCode(testSportsMeet.orgCode);
                    sportsMeet.setStatus(testSportsMeet.status);
                    sportsMeet.setStartTime(LocalDateTime.now().minusDays(1));
                    sportsMeet.setEndTime(LocalDateTime.now().plusDays(7));
                    sportsMeet.setRegistrationStart(LocalDateTime.now().minusDays(7));
                    sportsMeet.setRegistrationEnd(LocalDateTime.now().minusDays(1));
                    sportsMeet.setCreatedAt(LocalDateTime.now());
                    sportsMeet.setUpdatedAt(LocalDateTime.now());
                    
                    SportsMeet savedSportsMeet = sportsMeetRepository.save(sportsMeet);
                    logger.info("创建测试运动会: {} (ID: {}, 机构: {})", 
                               testSportsMeet.name, savedSportsMeet.getId(), testSportsMeet.orgCode);
                } else {
                    logger.debug("运动会已存在，跳过: {} ({})", testSportsMeet.name, testSportsMeet.meetCode);
                }
            } catch (Exception e) {
                logger.error("创建测试运动会失败: {} ({}), error: {}", 
                           testSportsMeet.name, testSportsMeet.meetCode, e.getMessage(), e);
            }
        }
    }
    
    /**
     * 初始化测试竞赛组别数据
     */
    private void initializeTestGroups() {
        logger.info("开始初始化测试竞赛组别...");
        
        // 定义测试竞赛组别列表
        List<TestGroup> testGroups = Arrays.asList(
            new TestGroup("高一男子组", 1L, "MALE", "高一", 2, 50, 3, 8, 1, false),
            new TestGroup("高一女子组", 1L, "FEMALE", "高一", 2, 50, 3, 8, 1, false),
            new TestGroup("高二男子组", 1L, "MALE", "高二", 2, 50, 3, 8, 1, false),
            new TestGroup("高二女子组", 1L, "FEMALE", "高二", 2, 50, 3, 8, 1, false),
            new TestGroup("高三男子组", 1L, "MALE", "高三", 2, 50, 3, 8, 1, false),
            new TestGroup("高三女子组", 1L, "FEMALE", "高三", 2, 50, 3, 8, 1, false),
            new TestGroup("教工男子组", 1L, "MALE", "教工", 2, 30, 3, 6, 1, false),
            new TestGroup("教工女子组", 1L, "FEMALE", "教工", 2, 30, 3, 6, 1, false)
        );
        
        for (TestGroup testGroup : testGroups) {
            try {
                // 检查组别是否已存在
                if (!groupRepository.existsByNameAndSportsMeetId(testGroup.name, testGroup.sportsMeetId, null)) {
                    
                    // 创建新组别
                    Group group = new Group();
                    group.setName(testGroup.name);
                    group.setSportsMeetId(testGroup.sportsMeetId);
                    group.setGender(testGroup.gender);
                    group.setGrade(testGroup.grade);
                    group.setMaxLeadersPerTeam(testGroup.maxLeadersPerTeam);
                    group.setMaxAthletesPerTeam(testGroup.maxAthletesPerTeam);
                    group.setMaxEventsPerAthlete(testGroup.maxEventsPerAthlete);
                    group.setMaxParticipantsPerEvent(testGroup.maxParticipantsPerEvent);
                    group.setMaxRelaysPerTeam(testGroup.maxRelaysPerTeam);
                    group.setAllowMixedEvents(testGroup.allowMixedEvents);
                    group.setStatus("ACTIVE");
                    group.setDescription(testGroup.grade + testGroup.gender + "竞赛组别");
                    group.setCreatedAt(LocalDateTime.now());
                    group.setUpdatedAt(LocalDateTime.now());
                    
                    Group savedGroup = groupRepository.save(group);
                    logger.info("创建测试竞赛组别: {} (ID: {}, 年级: {}, 性别: {})", 
                               testGroup.name, savedGroup.getId(), testGroup.grade, testGroup.gender);
                } else {
                    logger.debug("竞赛组别已存在，跳过: {}", testGroup.name);
                }
            } catch (Exception e) {
                logger.error("创建测试竞赛组别失败: {}, error: {}", testGroup.name, e.getMessage(), e);
            }
        }
    }
    
    /**
     * 初始化测试参赛单位数据
     */
    private void initializeTestTeams() {
        logger.info("开始初始化测试参赛单位...");
        
        // 定义测试参赛单位列表 - 明确机构归属和组别关联
        List<TestTeam> testTeams = Arrays.asList(
            // TEST001 机构的参赛单位
            new TestTeam("高一1班", "CLASS001", "张三", "13800138001", "TEST001", 1L), // 高一1班 -> 高一男子组
            new TestTeam("高一2班", "CLASS002", "李四", "13800138002", "TEST001", 2L), // 高一2班 -> 高一女子组
            
            // TEAM001 机构的参赛单位
            new TestTeam("班级A", "TEAM001_CLASS01", "王五", "13800138003", "TEAM001", 3L), // 班级A -> 高二男子组
            new TestTeam("班级B", "TEAM001_CLASS02", "赵六", "13800138004", "TEAM001", 4L)  // 班级B -> 高二女子组
        );
        
        for (TestTeam testTeam : testTeams) {
            try {
                // 检查参赛单位是否已存在（按teamCode查询）
                if (!teamRepository.existsByTeamCode(testTeam.teamCode)) {
                    
                    // 创建新参赛单位（不设置ID，让数据库自动生成）
                    Team team = new Team();
                    team.setName(testTeam.name);
                    team.setSportsMeetId(1L); // 默认运动会ID
                    team.setTeamCode(testTeam.teamCode);
                    team.setOrgCode(testTeam.orgCode); // 设置机构归属
                    team.setGroupId(testTeam.groupId); // 设置所属组别
                    // 使用正确的admin123 BCrypt加密结果
                    team.setPassword("$2b$10$gJvNcVQS2bzI2n6g.c59MOn/Fs1bpmRofmVmQQcFngPYkkndKKGYu");
                    team.setContactPerson(testTeam.contactPerson);
                    team.setContactPhone(testTeam.contactPhone);
                    team.setStatus("ACTIVE");
                    team.setCreatedAt(LocalDateTime.now());
                    team.setUpdatedAt(LocalDateTime.now());
                    
                    Team savedTeam = teamRepository.save(team);
                    logger.info("创建测试参赛单位: {} (ID: {}, 组别ID: {}, 联系人: {})", 
                               testTeam.name, savedTeam.getId(), testTeam.groupId, testTeam.contactPerson);
                } else {
                    logger.debug("参赛单位已存在，跳过: {} ({})", testTeam.name, testTeam.teamCode);
                }
            } catch (Exception e) {
                logger.error("创建测试参赛单位失败: {} ({}), error: {}", 
                           testTeam.name, testTeam.teamCode, e.getMessage(), e);
            }
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
        
        // 验证测试参赛单位
        validateKeyTeams();
    }
    
    /**
     * 验证关键参赛单位是否存在
     */
    private void validateKeyTeams() {
        try {
            long teamCount = teamRepository.count();
            logger.info("数据库中参赛单位总数: {}", teamCount);
            
            // 验证测试参赛单位（按teamCode查询）
            List<String> testTeamCodes = Arrays.asList("TEAM001", "TEAM002", "TEAM003");
            for (String teamCode : testTeamCodes) {
                Team team = teamRepository.findByTeamCode(teamCode);
                if (team != null) {
                    logger.info("参赛单位验证通过: {} (ID: {}, 状态: {})", 
                               team.getName(), team.getId(), team.getStatus());
                } else {
                    logger.warn("参赛单位缺失: {}", teamCode);
                }
            }
        } catch (Exception e) {
            logger.error("参赛单位验证失败: {}", e.getMessage(), e);
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
     * 测试参赛单位定义
     */
    private static class TestTeam {
        String name;
        String teamCode;
        String contactPerson;
        String contactPhone;
        String orgCode;
        Long groupId;
        
        TestTeam(String name, String teamCode, String contactPerson, String contactPhone, String orgCode, Long groupId) {
            this.name = name;
            this.teamCode = teamCode;
            this.contactPerson = contactPerson;
            this.contactPhone = contactPhone;
            this.orgCode = orgCode;
            this.groupId = groupId;
        }
    }
    
    /**
     * 测试竞赛组别定义
     */
    private static class TestGroup {
        String name;
        Long sportsMeetId;
        String gender;
        String grade;
        Integer maxLeadersPerTeam;
        Integer maxAthletesPerTeam;
        Integer maxEventsPerAthlete;
        Integer maxParticipantsPerEvent;
        Integer maxRelaysPerTeam;
        Boolean allowMixedEvents;
        
        TestGroup(String name, Long sportsMeetId, String gender, String grade,
                  Integer maxLeadersPerTeam, Integer maxAthletesPerTeam,
                  Integer maxEventsPerAthlete, Integer maxParticipantsPerEvent,
                  Integer maxRelaysPerTeam, Boolean allowMixedEvents) {
            this.name = name;
            this.sportsMeetId = sportsMeetId;
            this.gender = gender;
            this.grade = grade;
            this.maxLeadersPerTeam = maxLeadersPerTeam;
            this.maxAthletesPerTeam = maxAthletesPerTeam;
            this.maxEventsPerAthlete = maxEventsPerAthlete;
            this.maxParticipantsPerEvent = maxParticipantsPerEvent;
            this.maxRelaysPerTeam = maxRelaysPerTeam;
            this.allowMixedEvents = allowMixedEvents;
        }
    }
    
    /**
     * 测试运动会定义
     */
    private static class TestSportsMeet {
        String name;
        String meetCode;
        String orgCode;
        String status;
        
        TestSportsMeet(String name, String meetCode, String orgCode, String status) {
            this.name = name;
            this.meetCode = meetCode;
            this.orgCode = orgCode;
            this.status = status;
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
