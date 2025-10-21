package com.qixiang.qixiang_system_api.controller;

import com.qixiang.qixiang_system_api.entity.Event;
import com.qixiang.qixiang_system_api.service.EventService;
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
 * 比赛项目控制器
 * 提供比赛项目相关的API接口
 */
@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {
    
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);
    
    @Autowired
    private EventService eventService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 根据组别ID获取可参加的比赛项目
     * @param groupId 竞赛组别ID
     * @param Authorization JWT令牌
     * @return 比赛项目列表
     */
    @GetMapping("/by-group/{groupId}")
    @PreAuthorize("hasRole('TEAM')")
    public ResponseEntity<Map<String, Object>> getEventsByGroupId(
            @PathVariable Long groupId,
            @RequestHeader("Authorization") String Authorization) {
        
        logger.info("接收到获取组别 {} 可参加项目请求", groupId);
        
        try {
            // 从JWT中提取参赛单位ID
            Long teamId = extractTeamIdFromToken(Authorization);
            
            logger.info("为参赛单位 {} 获取组别 {} 的可参加项目", teamId, groupId);
            
            // 获取可参加的项目列表
            List<Event> events = eventService.getEventsByGroupId(groupId);
            
            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "获取可参加项目成功");
            response.put("data", events);
            response.put("count", events.size());
            response.put("groupId", groupId);
            response.put("timestamp", System.currentTimeMillis());
            
            logger.info("获取可参加项目成功，返回 {} 个项目", events.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取可参加项目失败: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取可参加项目失败: " + e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * 获取所有比赛项目
     * @param Authorization JWT令牌
     * @return 所有比赛项目列表
     */
    @GetMapping
    @PreAuthorize("hasRole('TEAM')")
    public ResponseEntity<Map<String, Object>> getAllEvents(
            @RequestHeader("Authorization") String Authorization) {
        
        logger.info("接收到获取所有项目请求");
        
        try {
            // 从JWT中提取参赛单位ID
            Long teamId = extractTeamIdFromToken(Authorization);
            
            logger.info("为参赛单位 {} 获取所有项目", teamId);
            
            // 获取所有项目列表
            List<Event> events = eventService.getAllEvents();
            
            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "获取所有项目成功");
            response.put("data", events);
            response.put("count", events.size());
            response.put("timestamp", System.currentTimeMillis());
            
            logger.info("获取所有项目成功，返回 {} 个项目", events.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取所有项目失败: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取所有项目失败: " + e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * 根据项目类型获取比赛项目
     * @param eventType 项目类型
     * @param Authorization JWT令牌
     * @return 指定类型的项目列表
     */
    @GetMapping("/by-type/{eventType}")
    @PreAuthorize("hasRole('TEAM')")
    public ResponseEntity<Map<String, Object>> getEventsByType(
            @PathVariable String eventType,
            @RequestHeader("Authorization") String Authorization) {
        
        logger.info("接收到获取 {} 类型项目请求", eventType);
        
        try {
            // 从JWT中提取参赛单位ID
            Long teamId = extractTeamIdFromToken(Authorization);
            
            logger.info("为参赛单位 {} 获取 {} 类型项目", teamId, eventType);
            
            // 获取指定类型的项目列表
            List<Event> events = eventService.getEventsByEventType(eventType);
            
            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "获取项目成功");
            response.put("data", events);
            response.put("count", events.size());
            response.put("eventType", eventType);
            response.put("timestamp", System.currentTimeMillis());
            
            logger.info("获取 {} 类型项目成功，返回 {} 个项目", eventType, events.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取 {} 类型项目失败: {}", eventType, e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取项目失败: " + e.getMessage());
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
     * 健康检查接口
     * @return 健康状态
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "比赛项目API运行正常");
        response.put("service", "EventController");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
}
