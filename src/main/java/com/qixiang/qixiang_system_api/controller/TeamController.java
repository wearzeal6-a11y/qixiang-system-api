package com.qixiang.qixiang_system_api.controller;

import com.qixiang.qixiang_system_api.dto.TeamSelectionDTO;
import com.qixiang.qixiang_system_api.entity.Team;
import com.qixiang.qixiang_system_api.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 参赛单位API控制器
 * 提供参赛单位相关的RESTful API接口
 */
@RestController
@RequestMapping("/api/teams")
@CrossOrigin(origins = "*")
public class TeamController {
    
    @Autowired
    private TeamService teamService;
    
    /**
     * 根据机构代码获取其下所有参赛单位列表
     * 这是登录流程的一部分，用于前端下拉选择框
     * 
     * @param orgCode 机构代码
     * @return 参赛单位选择列表
     */
    @GetMapping("/by-org-code/{orgCode}")
    public ResponseEntity<List<TeamSelectionDTO>> getTeamsByOrgCode(@PathVariable String orgCode) {
        try {
            List<TeamSelectionDTO> teams = teamService.getTeamsByOrgCode(orgCode);
            return ResponseEntity.ok(teams);
        } catch (Exception e) {
            // 记录错误日志
            System.err.println("获取参赛单位列表失败: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 根据机构代码获取其下所有参赛单位列表（兼容旧版本API）
     * 这是登录流程的一部分，用于前端下拉选择框
     * 
     * @param organizationCode 机构代码
     * @return 参赛单位选择列表
     */
    @GetMapping("/by-organization")
    public ResponseEntity<List<TeamSelectionDTO>> getTeamsByOrganization(@RequestParam String organizationCode) {
        try {
            List<TeamSelectionDTO> teams = teamService.getTeamsByOrgCode(organizationCode);
            return ResponseEntity.ok(teams);
        } catch (Exception e) {
            // 记录错误日志
            System.err.println("获取参赛单位列表失败: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 根据机构代码和运动会状态获取参赛单位列表
     * 
     * @param orgCode 机构代码
     * @param statuses 运动会状态列表（逗号分隔）
     * @return 参赛单位选择列表
     */
    @GetMapping("/by-org-code/{orgCode}/by-statuses")
    public ResponseEntity<List<TeamSelectionDTO>> getTeamsByOrgCodeAndStatuses(
            @PathVariable String orgCode,
            @RequestParam String statuses) {
        try {
            List<String> statusList = List.of(statuses.split(","));
            List<TeamSelectionDTO> teams = teamService.getTeamsByOrgCodeAndStatuses(orgCode, statusList);
            return ResponseEntity.ok(teams);
        } catch (Exception e) {
            System.err.println("获取参赛单位列表失败: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 根据运动会ID获取参赛单位列表
     * 
     * @param sportsMeetId 运动会ID
     * @return 参赛单位选择列表
     */
    @GetMapping("/by-sports-meet/{sportsMeetId}")
    public ResponseEntity<List<TeamSelectionDTO>> getTeamsBySportsMeetId(@PathVariable Long sportsMeetId) {
        try {
            List<TeamSelectionDTO> teams = teamService.getTeamsBySportsMeetId(sportsMeetId);
            return ResponseEntity.ok(teams);
        } catch (Exception e) {
            System.err.println("获取参赛单位列表失败: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 根据运动会ID获取所有参赛单位详细信息
     * 
     * @param sportsMeetId 运动会ID
     * @return 参赛单位详细信息列表
     */
    @GetMapping("/full/by-sports-meet/{sportsMeetId}")
    public ResponseEntity<List<Team>> getFullTeamsBySportsMeetId(@PathVariable Long sportsMeetId) {
        try {
            List<Team> teams = teamService.getFullTeamsBySportsMeetId(sportsMeetId);
            return ResponseEntity.ok(teams);
        } catch (Exception e) {
            System.err.println("获取参赛单位详细信息失败: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 根据运动会ID和状态获取参赛单位列表
     * 
     * @param sportsMeetId 运动会ID
     * @param status 参赛状态
     * @return 参赛单位列表
     */
    @GetMapping("/by-sports-meet/{sportsMeetId}/by-status/{status}")
    public ResponseEntity<List<Team>> getTeamsBySportsMeetIdAndStatus(
            @PathVariable Long sportsMeetId,
            @PathVariable String status) {
        try {
            List<Team> teams = teamService.getTeamsBySportsMeetIdAndStatus(sportsMeetId, status);
            return ResponseEntity.ok(teams);
        } catch (Exception e) {
            System.err.println("获取参赛单位列表失败: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 根据ID获取参赛单位详细信息
     * 
     * @param teamId 参赛单位ID
     * @return 参赛单位详细信息
     */
    @GetMapping("/{teamId}")
    public ResponseEntity<Team> getTeamById(@PathVariable Long teamId) {
        try {
            Team team = teamService.getTeamById(teamId);
            return ResponseEntity.ok(team);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("获取参赛单位信息失败: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 根据参赛单位代码获取参赛单位详细信息
     * 
     * @param teamCode 参赛单位代码
     * @return 参赛单位详细信息
     */
    @GetMapping("/by-code/{teamCode}")
    public ResponseEntity<Team> getTeamByCode(@PathVariable String teamCode) {
        try {
            Team team = teamService.getTeamByCode(teamCode);
            return ResponseEntity.ok(team);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("获取参赛单位信息失败: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 创建参赛单位
     * 
     * @param team 参赛单位信息
     * @return 创建的参赛单位
     */
    @PostMapping
    public ResponseEntity<Team> createTeam(@RequestBody Team team) {
        try {
            Team createdTeam = teamService.createTeam(team);
            return ResponseEntity.ok(createdTeam);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            System.err.println("创建参赛单位失败: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 更新参赛单位信息
     * 
     * @param teamId 参赛单位ID
     * @param team 参赛单位信息
     * @return 更新后的参赛单位
     */
    @PutMapping("/{teamId}")
    public ResponseEntity<Team> updateTeam(@PathVariable Long teamId, @RequestBody Team team) {
        try {
            team.setId(teamId);
            Team updatedTeam = teamService.updateTeam(team);
            return ResponseEntity.ok(updatedTeam);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            System.err.println("更新参赛单位失败: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 删除参赛单位
     * 
     * @param teamId 参赛单位ID
     * @return 删除结果
     */
    @DeleteMapping("/{teamId}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long teamId) {
        try {
            teamService.deleteTeam(teamId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("删除参赛单位失败: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 统计运动会参赛单位数量
     * 
     * @param sportsMeetId 运动会ID
     * @return 参赛单位数量
     */
    @GetMapping("/count/by-sports-meet/{sportsMeetId}")
    public ResponseEntity<Long> countTeamsBySportsMeetId(@PathVariable Long sportsMeetId) {
        try {
            Long count = teamService.countTeamsBySportsMeetId(sportsMeetId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            System.err.println("统计参赛单位数量失败: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 统计运动会指定状态的参赛单位数量
     * 
     * @param sportsMeetId 运动会ID
     * @param status 参赛状态
     * @return 参赛单位数量
     */
    @GetMapping("/count/by-sports-meet/{sportsMeetId}/by-status/{status}")
    public ResponseEntity<Long> countTeamsBySportsMeetIdAndStatus(
            @PathVariable Long sportsMeetId,
            @PathVariable String status) {
        try {
            Long count = teamService.countTeamsBySportsMeetIdAndStatus(sportsMeetId, status);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            System.err.println("统计参赛单位数量失败: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 批量创建参赛单位
     * 
     * @param teams 参赛单位列表
     * @return 创建的参赛单位列表
     */
    @PostMapping("/batch")
    public ResponseEntity<List<Team>> createTeams(@RequestBody List<Team> teams) {
        try {
            List<Team> createdTeams = teamService.createTeams(teams);
            return ResponseEntity.ok(createdTeams);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            System.err.println("批量创建参赛单位失败: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
