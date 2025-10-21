package com.qixiang.qixiang_system_api.controller;

import com.qixiang.qixiang_system_api.dto.RegistrationSummaryDTO;
import com.qixiang.qixiang_system_api.service.RegistrationService;
import com.qixiang.qixiang_system_api.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 报名数据汇总控制器
 * 提供报名数据统计和汇总的API接口
 */
@RestController
@RequestMapping("/api/registrations")
@CrossOrigin(origins = "*")
public class RegistrationController {
    
    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);
    
    @Autowired
    private RegistrationService registrationService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 获取报名数据汇总
     * @param Authorization JWT令牌
     * @return 报名数据汇总列表
     */
    @GetMapping("/summary")
    @PreAuthorize("hasRole('TEAM')")
    public ResponseEntity<Map<String, Object>> getRegistrationSummary(
            @RequestHeader("Authorization") String Authorization) {
        
        logger.info("接收到获取报名数据汇总请求");
        
        try {
            // 从JWT中提取参赛单位ID
            Long teamId = extractTeamIdFromToken(Authorization);
            
            logger.info("为参赛单位 {} 获取报名数据汇总", teamId);
            
            // 获取报名数据汇总
            List<RegistrationSummaryDTO> summary = registrationService.getRegistrationSummary(teamId);
            
            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "获取报名数据汇总成功");
            response.put("data", summary);
            response.put("teamId", teamId);
            response.put("timestamp", System.currentTimeMillis());
            
            logger.info("报名数据汇总获取成功，返回 {} 条统计记录", summary.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取报名数据汇总失败: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取报名数据汇总失败: " + e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * 获取指定组别的详细报名统计
     * @param groupId 竞赛组别ID
     * @param Authorization JWT令牌
     * @return 组别详细统计
     */
    @GetMapping("/groups/{groupId}/statistics")
    @PreAuthorize("hasRole('TEAM')")
    public ResponseEntity<Map<String, Object>> getGroupStatistics(
            @PathVariable Long groupId,
            @RequestHeader("Authorization") String Authorization) {
        
        logger.info("接收到获取组别 {} 详细统计请求", groupId);
        
        try {
            // 从JWT中提取参赛单位ID
            Long teamId = extractTeamIdFromToken(Authorization);
            
            logger.info("为参赛单位 {} 获取组别 {} 的详细统计", teamId, groupId);
            
            // 获取组别详细统计
            Map<String, Object> statistics = registrationService.getGroupDetailedStatistics(teamId, groupId);
            
            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "获取组别详细统计成功");
            response.put("data", statistics);
            response.put("teamId", teamId);
            response.put("groupId", groupId);
            response.put("timestamp", System.currentTimeMillis());
            
            logger.info("组别详细统计获取成功");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取组别详细统计失败: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取组别详细统计失败: " + e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * 获取指定项目的报名统计
     * @param eventId 比赛项目ID
     * @param Authorization JWT令牌
     * @return 项目报名统计
     */
    @GetMapping("/events/{eventId}/statistics")
    @PreAuthorize("hasRole('TEAM')")
    public ResponseEntity<Map<String, Object>> getEventStatistics(
            @PathVariable Long eventId,
            @RequestHeader("Authorization") String Authorization) {
        
        logger.info("接收到获取项目 {} 报名统计请求", eventId);
        
        try {
            // 从JWT中提取参赛单位ID
            Long teamId = extractTeamIdFromToken(Authorization);
            
            logger.info("为参赛单位 {} 获取项目 {} 的报名统计", teamId, eventId);
            
            // 获取项目报名统计
            Map<String, Object> statistics = registrationService.getEventRegistrationStatistics(teamId, eventId);
            
            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "获取项目报名统计成功");
            response.put("data", statistics);
            response.put("teamId", teamId);
            response.put("eventId", eventId);
            response.put("timestamp", System.currentTimeMillis());
            
            logger.info("项目报名统计获取成功");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取项目报名统计失败: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取项目报名统计失败: " + e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * 获取报名数据概览
     * @param Authorization JWT令牌
     * @return 报名数据概览
     */
    @GetMapping("/overview")
    @PreAuthorize("hasRole('TEAM')")
    public ResponseEntity<Map<String, Object>> getRegistrationOverview(
            @RequestHeader("Authorization") String Authorization) {
        
        logger.info("接收到获取报名数据概览请求");
        
        try {
            // 从JWT中提取参赛单位ID
            Long teamId = extractTeamIdFromToken(Authorization);
            
            logger.info("为参赛单位 {} 获取报名数据概览", teamId);
            
            // 获取报名数据概览
            Map<String, Object> overview = registrationService.getRegistrationOverview(teamId);
            
            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "获取报名数据概览成功");
            response.put("data", overview);
            response.put("teamId", teamId);
            response.put("timestamp", System.currentTimeMillis());
            
            logger.info("报名数据概览获取成功");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取报名数据概览失败: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取报名数据概览失败: " + e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * 验证数据访问权限
     * @param targetTeamId 目标参赛单位ID
     * @param Authorization JWT令牌
     * @return 权限验证结果
     */
    @GetMapping("/verify-permission/{targetTeamId}")
    @PreAuthorize("hasRole('TEAM')")
    public ResponseEntity<Map<String, Object>> verifyDataAccessPermission(
            @PathVariable Long targetTeamId,
            @RequestHeader("Authorization") String Authorization) {
        
        logger.info("接收到数据访问权限验证请求，目标参赛单位: {}", targetTeamId);
        
        try {
            // 从JWT中提取参赛单位ID
            Long teamId = extractTeamIdFromToken(Authorization);
            
            logger.info("验证参赛单位 {} 对参赛单位 {} 的数据访问权限", teamId, targetTeamId);
            
            // 验证权限
            boolean hasPermission = registrationService.hasPermissionToAccessData(teamId, targetTeamId);
            
            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "权限验证完成");
            response.put("hasPermission", hasPermission);
            response.put("requestTeamId", teamId);
            response.put("targetTeamId", targetTeamId);
            response.put("timestamp", System.currentTimeMillis());
            
            logger.info("数据访问权限验证完成，结果: {}", hasPermission);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("数据访问权限验证失败: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "数据访问权限验证失败: " + e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * 从JWT令牌中提取参赛单位ID
     * @param Authorization JWT令牌
     * @return 参赛单位ID
     */
    private Long extractTeamIdFromToken(String Authorization) {
        try {
            // 移除 "Bearer " 前缀
            String token = Authorization.replace("Bearer ", "");
            
            // 从JWT中提取用户名（对于TEAM角色，用户名就是teamId）
            String username = jwtUtil.extractUsername(token);
            String role = jwtUtil.extractRole(token);
            String orgCode = jwtUtil.extractOrgCode(token);
            
            logger.debug("从JWT中提取的信息 - username: {}, role: {}, orgCode: {}", username, role, orgCode);
            
            Long teamId = null;
            
            if ("TEAM".equals(role)) {
                // 对于TEAM角色，用户名就是teamId
                try {
                    teamId = Long.parseLong(username);
                    logger.debug("TEAM角色：从username解析出teamId: {}", teamId);
                } catch (NumberFormatException e) {
                    logger.warn("无法将TEAM角色的username解析为teamId: {}", username);
                }
            } else if ("ADMIN".equals(role) && orgCode != null) {
                // 对于ADMIN角色，需要根据orgCode查找team_id
                teamId = getTeamIdByOrgCode(orgCode);
                logger.debug("ADMIN角色：根据orgCode {} 找到teamId: {}", orgCode, teamId);
            }
            
            if (teamId != null) {
                logger.debug("成功提取teamId: {}", teamId);
                return teamId;
            }
            
            throw new RuntimeException("无法提取参赛单位ID - username: " + username + ", role: " + role + ", orgCode: " + orgCode);
            
        } catch (Exception e) {
            logger.error("提取参赛单位ID失败: {}", e.getMessage(), e);
            throw new RuntimeException("提取参赛单位ID失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 根据组织代码获取参赛单位ID
     * @param orgCode 组织代码
     * @return 参赛单位ID
     */
    private Long getTeamIdByOrgCode(String orgCode) {
        // 这里应该查询数据库，暂时使用硬编码的映射
        switch (orgCode) {
            case "TEST001":
                return 1L;
            case "TEST002":
                return 2L;
            default:
                logger.warn("未知的组织代码: {}", orgCode);
                return null;
        }
    }
    
    /**
     * 处理异常响应
     * @param e 异常
     * @return 错误响应
     */
    private ResponseEntity<Map<String, Object>> handleException(Exception e) {
        logger.error("API调用失败: {}", e.getMessage(), e);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", "API调用失败: " + e.getMessage());
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * 获取指定运动员已报名的项目ID列表
     * @param athleteId 运动员ID
     * @param Authorization JWT令牌
     * @return 已报名项目ID列表
     */
    @GetMapping("/by-athlete/{athleteId}")
    @PreAuthorize("hasRole('TEAM')")
    public ResponseEntity<Map<String, Object>> getAthleteRegistrations(
            @PathVariable Long athleteId,
            @RequestHeader("Authorization") String Authorization) {
        
        logger.info("接收到获取运动员 {} 已报名项目请求", athleteId);
        
        try {
            // 从JWT中提取参赛单位ID
            Long teamId = extractTeamIdFromToken(Authorization);
            
            logger.info("为参赛单位 {} 获取运动员 {} 的已报名项目", teamId, athleteId);
            
            // 获取运动员已报名的项目ID列表
            List<Long> eventIds = registrationService.getAthleteRegisteredEventIds(teamId, athleteId);
            
            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "获取运动员已报名项目成功");
            response.put("data", eventIds);
            response.put("count", eventIds.size());
            response.put("athleteId", athleteId);
            response.put("timestamp", System.currentTimeMillis());
            
            logger.info("获取运动员 {} 已报名项目成功，返回 {} 个项目", athleteId, eventIds.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取运动员 {} 已报名项目失败: {}", athleteId, e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取运动员已报名项目失败: " + e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * 更新运动员的项目报名
     * @param athleteId 运动员ID
     * @param eventIds 项目ID列表
     * @param Authorization JWT令牌
     * @return 更新结果
     */
    @PutMapping("/by-athlete/{athleteId}")
    @PreAuthorize("hasRole('TEAM')")
    public ResponseEntity<Map<String, Object>> updateAthleteRegistrations(
            @PathVariable Long athleteId,
            @RequestBody(required = false) List<Long> eventIds,
            @RequestHeader("Authorization") String Authorization) {
        
        logger.info("=== 开始处理运动员项目报名更新请求 ===");
        
        // 处理空数组或null的情况
        if (eventIds == null) {
            eventIds = new java.util.ArrayList<>();
            logger.info("项目ID列表为null，初始化为空列表");
        }
        
        logger.info("请求参数 - 运动员ID: {}, 项目数量: {}, 项目IDs: {}", athleteId, eventIds.size(), eventIds);
        
        try {
            // 从JWT中提取参赛单位ID
            Long teamId = extractTeamIdFromToken(Authorization);
            logger.info("从JWT提取的参赛单位ID: {}", teamId);
            
            // 验证运动员信息
            logger.info("开始验证运动员 {} 属于参赛单位 {}", athleteId, teamId);
            
            // 更新运动员的项目报名
            logger.info("调用RegistrationService更新运动员 {} 的项目报名", athleteId);
            registrationService.updateAthleteRegistrations(teamId, athleteId, eventIds);
            logger.info("RegistrationService更新完成");
            
            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "更新运动员项目报名成功");
            response.put("data", eventIds);
            response.put("count", eventIds.size());
            response.put("athleteId", athleteId);
            response.put("teamId", teamId);
            response.put("timestamp", System.currentTimeMillis());
            
            logger.info("=== 运动员 {} 项目报名更新成功 ===", athleteId);
            logger.info("保存的项目数量: {}, 项目IDs: {}", eventIds.size(), eventIds);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("=== 运动员 {} 项目报名更新失败 ===", athleteId);
            logger.error("错误类型: {}", e.getClass().getSimpleName());
            logger.error("错误信息: {}", e.getMessage());
            logger.error("完整堆栈跟踪:", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "更新运动员项目报名失败: " + e.getMessage());
            errorResponse.put("athleteId", athleteId);
            errorResponse.put("errorType", e.getClass().getSimpleName());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * 健康检查接口
     * @return 健康状态
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "报名数据汇总API运行正常");
        response.put("service", "RegistrationController");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
}
