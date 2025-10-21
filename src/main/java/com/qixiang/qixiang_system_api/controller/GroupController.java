package com.qixiang.qixiang_system_api.controller;

import com.qixiang.qixiang_system_api.entity.Group;
import com.qixiang.qixiang_system_api.service.GroupService;
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
 * 竞赛组别管理控制器
 * 提供竞赛组别相关的API接口
 */
@RestController
@RequestMapping("/api/groups")
@CrossOrigin(origins = "*")
public class GroupController {
    
    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);
    
    @Autowired
    private GroupService groupService;
    
    /**
     * 获取所有竞赛组别列表
     * @return 组别列表
     */
    @GetMapping
    @PreAuthorize("hasRole('TEAM')")
    public ResponseEntity<Map<String, Object>> getAllGroups() {
        logger.info("接收到获取竞赛组别列表请求");
        
        try {
            // 获取所有激活的组别
            List<Group> groups = groupService.getAllActiveGroups();
            
            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "获取竞赛组别列表成功");
            response.put("data", groups);
            response.put("count", groups.size());
            response.put("timestamp", System.currentTimeMillis());
            
            logger.info("竞赛组别列表获取成功，返回 {} 条记录", groups.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取竞赛组别列表失败: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取竞赛组别列表失败: " + e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * 根据ID获取组别详情
     * @param id 组别ID
     * @return 组别详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TEAM')")
    public ResponseEntity<Map<String, Object>> getGroupById(@PathVariable Long id) {
        logger.info("接收到获取组别详情请求: ID={}", id);
        
        try {
            Group group = groupService.getGroupById(id)
                    .orElseThrow(() -> new RuntimeException("组别不存在"));
            
            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "获取组别详情成功");
            response.put("data", group);
            response.put("timestamp", System.currentTimeMillis());
            
            logger.info("组别详情获取成功: {}", group.getName());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取组别详情失败: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取组别详情失败: " + e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * 根据性别获取组别列表
     * @param gender 性别
     * @return 组别列表
     */
    @GetMapping("/by-gender/{gender}")
    @PreAuthorize("hasRole('TEAM')")
    public ResponseEntity<Map<String, Object>> getGroupsByGender(@PathVariable String gender) {
        logger.info("接收到根据性别获取组别列表请求: gender={}", gender);
        
        try {
            List<Group> groups = groupService.getGroupsByGender(gender);
            
            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "根据性别获取组别列表成功");
            response.put("data", groups);
            response.put("gender", gender);
            response.put("count", groups.size());
            response.put("timestamp", System.currentTimeMillis());
            
            logger.info("根据性别 {} 获取组别列表成功，返回 {} 条记录", gender, groups.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("根据性别获取组别列表失败: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "根据性别获取组别列表失败: " + e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * 根据年级获取组别列表
     * @param grade 年级
     * @return 组别列表
     */
    @GetMapping("/by-grade/{grade}")
    @PreAuthorize("hasRole('TEAM')")
    public ResponseEntity<Map<String, Object>> getGroupsByGrade(@PathVariable String grade) {
        logger.info("接收到根据年级获取组别列表请求: grade={}", grade);
        
        try {
            List<Group> groups = groupService.getGroupsByGrade(grade);
            
            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "根据年级获取组别列表成功");
            response.put("data", groups);
            response.put("grade", grade);
            response.put("count", groups.size());
            response.put("timestamp", System.currentTimeMillis());
            
            logger.info("根据年级 {} 获取组别列表成功，返回 {} 条记录", grade, groups.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("根据年级获取组别列表失败: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "根据年级获取组别列表失败: " + e.getMessage());
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
        response.put("message", "竞赛组别API运行正常");
        response.put("service", "GroupController");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
}
