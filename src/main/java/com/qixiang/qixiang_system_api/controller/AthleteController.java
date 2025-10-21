package com.qixiang.qixiang_system_api.controller;

import com.qixiang.qixiang_system_api.dto.AthleteDTO;
import com.qixiang.qixiang_system_api.service.AthleteService;
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
 * 运动员管理控制器
 * 提供运动员信息的增删改查API接口
 */
@RestController
@RequestMapping("/api/athletes")
@CrossOrigin(origins = "*")
public class AthleteController {
    
    private static final Logger logger = LoggerFactory.getLogger(AthleteController.class);
    
    @Autowired
    private AthleteService athleteService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 获取当前参赛单位的运动员列表
     * @param Authorization JWT令牌
     * @return 运动员列表
     */
    @GetMapping
    @PreAuthorize("hasRole('TEAM')")
    public ResponseEntity<Map<String, Object>> getAthletesByTeam(
            @RequestHeader("Authorization") String Authorization) {
        
        logger.info("接收到获取运动员列表请求");
        
        try {
            // 从JWT中提取参赛单位ID
            Long teamId = extractTeamIdFromToken(Authorization);
            
            logger.info("为参赛单位 {} 获取运动员列表", teamId);
            
            // 获取运动员列表
            List<AthleteDTO> athletes = athleteService.getAthletesByTeamId(teamId);
            
            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "获取运动员列表成功");
            response.put("data", athletes);
            response.put("teamId", teamId);
            response.put("count", athletes.size());
            response.put("timestamp", System.currentTimeMillis());
            
            logger.info("运动员列表获取成功，返回 {} 条记录", athletes.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取运动员列表失败: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取运动员列表失败: " + e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * 添加新运动员
     * @param athleteDTO 运动员信息
     * @param Authorization JWT令牌
     * @return 添加结果
     */
    @PostMapping
    @PreAuthorize("hasRole('TEAM')")
    public ResponseEntity<Map<String, Object>> addAthlete(
            @RequestBody AthleteDTO athleteDTO,
            @RequestHeader("Authorization") String Authorization) {
        
        logger.info("接收到添加运动员请求: {}", athleteDTO.getName());
        
        try {
            // 从JWT中提取参赛单位ID
            Long teamId = extractTeamIdFromToken(Authorization);
            
            logger.info("为参赛单位 {} 添加运动员: {}", teamId, athleteDTO.getName());
            
            // 设置参赛单位ID
            athleteDTO.setTeamId(teamId);
            
            // 添加运动员
            AthleteDTO savedAthlete = athleteService.addAthlete(athleteDTO);
            
            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "添加运动员成功");
            response.put("data", savedAthlete);
            response.put("teamId", teamId);
            response.put("timestamp", System.currentTimeMillis());
            
            logger.info("运动员添加成功: {}", savedAthlete.getName());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("添加运动员失败: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "添加运动员失败: " + e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * 更新运动员信息
     * @param id 运动员ID
     * @param athleteDTO 运动员信息
     * @param Authorization JWT令牌
     * @return 更新结果
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEAM')")
    public ResponseEntity<Map<String, Object>> updateAthlete(
            @PathVariable Long id,
            @RequestBody AthleteDTO athleteDTO,
            @RequestHeader("Authorization") String Authorization) {
        
        logger.info("接收到更新运动员请求: ID={}, 姓名={}", id, athleteDTO.getName());
        
        try {
            // 从JWT中提取参赛单位ID
            Long teamId = extractTeamIdFromToken(Authorization);
            
            logger.info("为参赛单位 {} 更新运动员 ID: {}", teamId, id);
            
            // 验证权限并更新运动员
            AthleteDTO updatedAthlete = athleteService.updateAthlete(id, athleteDTO, teamId);
            
            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "更新运动员信息成功");
            response.put("data", updatedAthlete);
            response.put("teamId", teamId);
            response.put("timestamp", System.currentTimeMillis());
            
            logger.info("运动员信息更新成功: {}", updatedAthlete.getName());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("更新运动员信息失败: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "更新运动员信息失败: " + e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * 删除运动员
     * @param id 运动员ID
     * @param Authorization JWT令牌
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEAM')")
    public ResponseEntity<Map<String, Object>> deleteAthlete(
            @PathVariable Long id,
            @RequestHeader("Authorization") String Authorization) {
        
        logger.info("接收到删除运动员请求: ID={}", id);
        
        try {
            // 从JWT中提取参赛单位ID
            Long teamId = extractTeamIdFromToken(Authorization);
            
            logger.info("为参赛单位 {} 删除运动员 ID: {}", teamId, id);
            
            // 验证权限并删除运动员
            athleteService.deleteAthlete(id, teamId);
            
            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "删除运动员成功");
            response.put("athleteId", id);
            response.put("teamId", teamId);
            response.put("timestamp", System.currentTimeMillis());
            
            logger.info("运动员删除成功: ID={}", id);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("删除运动员失败: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "删除运动员失败: " + e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * 根据ID获取运动员详细信息
     * @param id 运动员ID
     * @param Authorization JWT令牌
     * @return 运动员详细信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TEAM')")
    public ResponseEntity<Map<String, Object>> getAthleteById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String Authorization) {
        
        logger.info("接收到获取运动员详情请求: ID={}", id);
        
        try {
            // 从JWT中提取参赛单位ID
            Long teamId = extractTeamIdFromToken(Authorization);
            
            logger.info("为参赛单位 {} 获取运动员详情 ID: {}", teamId, id);
            
            // 验证权限并获取运动员详情
            AthleteDTO athlete = athleteService.getAthleteById(id, teamId);
            
            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "获取运动员详情成功");
            response.put("data", athlete);
            response.put("teamId", teamId);
            response.put("timestamp", System.currentTimeMillis());
            
            logger.info("运动员详情获取成功: {}", athlete.getName());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取运动员详情失败: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取运动员详情失败: " + e.getMessage());
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
        // 实际实现应该注入TeamRepository并查询
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
        response.put("message", "运动员管理API运行正常");
        response.put("service", "AthleteController");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
}
