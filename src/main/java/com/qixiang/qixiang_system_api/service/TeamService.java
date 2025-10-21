package com.qixiang.qixiang_system_api.service;

import com.qixiang.qixiang_system_api.dto.TeamSelectionDTO;
import com.qixiang.qixiang_system_api.entity.Team;
import com.qixiang.qixiang_system_api.repository.SportsMeetRepository;
import com.qixiang.qixiang_system_api.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 参赛单位业务逻辑层
 * 提供参赛单位相关的业务处理方法
 */
@Service
@Transactional
public class TeamService {
    
    @Autowired
    private TeamRepository teamRepository;
    
    @Autowired
    private SportsMeetRepository sportsMeetRepository;
    
    /**
     * 根据机构代码获取其下所有参赛单位列表
     * 这是登录流程的一部分，用于前端下拉选择框
     * 修正：直接按参赛单位的机构代码查询，不再通过运动会关联
     * 
     * @param orgCode 机构代码
     * @return 参赛单位选择列表
     */
    @Transactional(readOnly = true)
    public List<TeamSelectionDTO> getTeamsByOrgCode(String orgCode) {
        // 直接查询该机构下的所有激活状态参赛单位
        List<TeamSelectionDTO> teams = teamRepository.findTeamSelectionByOrgCode(orgCode);
        
        // 按名称排序
        return teams.stream()
                .sorted((t1, t2) -> t1.getName().compareTo(t2.getName()))
                .collect(Collectors.toList());
    }
    
    /**
     * 根据机构代码和运动会状态获取参赛单位列表
     * 
     * @param orgCode 机构代码
     * @param statuses 运动会状态列表
     * @return 参赛单位选择列表
     */
    @Transactional(readOnly = true)
    public List<TeamSelectionDTO> getTeamsByOrgCodeAndStatuses(String orgCode, List<String> statuses) {
        List<Long> sportsMeetIds = sportsMeetRepository.findIdsByOrgCodeAndStatusIn(orgCode, statuses);
        
        if (sportsMeetIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<TeamSelectionDTO> teams = teamRepository.findTeamSelectionBySportsMeetIds(sportsMeetIds);
        
        return teams.stream()
                .distinct()
                .sorted((t1, t2) -> t1.getName().compareTo(t2.getName()))
                .collect(Collectors.toList());
    }
    
    /**
     * 根据运动会ID获取参赛单位列表
     * 
     * @param sportsMeetId 运动会ID
     * @return 参赛单位选择列表
     */
    @Transactional(readOnly = true)
    public List<TeamSelectionDTO> getTeamsBySportsMeetId(Long sportsMeetId) {
        return teamRepository.findTeamSelectionBySportsMeetId(sportsMeetId);
    }
    
    /**
     * 根据运动会ID获取所有参赛单位（包括详细信息）
     * 
     * @param sportsMeetId 运动会ID
     * @return 参赛单位列表
     */
    @Transactional(readOnly = true)
    public List<Team> getFullTeamsBySportsMeetId(Long sportsMeetId) {
        return teamRepository.findBySportsMeetId(sportsMeetId);
    }
    
    /**
     * 根据运动会ID和状态获取参赛单位列表
     * 
     * @param sportsMeetId 运动会ID
     * @param status 参赛状态
     * @return 参赛单位列表
     */
    @Transactional(readOnly = true)
    public List<Team> getTeamsBySportsMeetIdAndStatus(Long sportsMeetId, String status) {
        return teamRepository.findBySportsMeetIdAndStatus(sportsMeetId, status);
    }
    
    /**
     * 创建参赛单位
     * 
     * @param team 参赛单位信息
     * @return 创建的参赛单位
     */
    public Team createTeam(Team team) {
        // 验证参赛单位名称在同一运动会中是否唯一
        if (teamRepository.existsByNameAndSportsMeetId(team.getName(), team.getSportsMeetId(), null)) {
            throw new IllegalArgumentException("参赛单位名称在该运动会中已存在");
        }
        
        // 如果提供了参赛单位代码，验证其唯一性
        if (team.getTeamCode() != null && !team.getTeamCode().trim().isEmpty()) {
            if (teamRepository.existsByTeamCodeAndSportsMeetId(team.getTeamCode(), team.getSportsMeetId(), null)) {
                throw new IllegalArgumentException("参赛单位代码在该运动会中已存在");
            }
        }
        
        return teamRepository.save(team);
    }
    
    /**
     * 更新参赛单位信息
     * 
     * @param team 参赛单位信息
     * @return 更新后的参赛单位
     */
    public Team updateTeam(Team team) {
        // 验证参赛单位是否存在
        Team existingTeam = teamRepository.findById(team.getId())
                .orElseThrow(() -> new IllegalArgumentException("参赛单位不存在"));
        
        // 验证参赛单位名称在同一运动会中是否唯一（排除自己）
        if (teamRepository.existsByNameAndSportsMeetId(team.getName(), team.getSportsMeetId(), team.getId())) {
            throw new IllegalArgumentException("参赛单位名称在该运动会中已存在");
        }
        
        // 如果提供了参赛单位代码，验证其唯一性（排除自己）
        if (team.getTeamCode() != null && !team.getTeamCode().trim().isEmpty()) {
            if (teamRepository.existsByTeamCodeAndSportsMeetId(team.getTeamCode(), team.getSportsMeetId(), team.getId())) {
                throw new IllegalArgumentException("参赛单位代码在该运动会中已存在");
            }
        }
        
        return teamRepository.save(team);
    }
    
    /**
     * 删除参赛单位
     * 
     * @param teamId 参赛单位ID
     */
    public void deleteTeam(Long teamId) {
        if (!teamRepository.existsById(teamId)) {
            throw new IllegalArgumentException("参赛单位不存在");
        }
        teamRepository.deleteById(teamId);
    }
    
    /**
     * 根据ID获取参赛单位
     * 
     * @param teamId 参赛单位ID
     * @return 参赛单位信息
     */
    @Transactional(readOnly = true)
    public Team getTeamById(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("参赛单位不存在"));
    }
    
    /**
     * 根据参赛单位代码获取参赛单位
     * 
     * @param teamCode 参赛单位代码
     * @return 参赛单位信息
     */
    @Transactional(readOnly = true)
    public Team getTeamByCode(String teamCode) {
        Team team = teamRepository.findByTeamCode(teamCode);
        if (team == null) {
            throw new IllegalArgumentException("参赛单位不存在");
        }
        return team;
    }
    
    /**
     * 统计运动会参赛单位数量
     * 
     * @param sportsMeetId 运动会ID
     * @return 参赛单位数量
     */
    @Transactional(readOnly = true)
    public Long countTeamsBySportsMeetId(Long sportsMeetId) {
        return teamRepository.countBySportsMeetId(sportsMeetId);
    }
    
    /**
     * 统计运动会指定状态的参赛单位数量
     * 
     * @param sportsMeetId 运动会ID
     * @param status 参赛状态
     * @return 参赛单位数量
     */
    @Transactional(readOnly = true)
    public Long countTeamsBySportsMeetIdAndStatus(Long sportsMeetId, String status) {
        return teamRepository.countBySportsMeetIdAndStatus(sportsMeetId, status);
    }
    
    /**
     * 批量创建参赛单位
     * 
     * @param teams 参赛单位列表
     * @return 创建的参赛单位列表
     */
    public List<Team> createTeams(List<Team> teams) {
        return teams.stream()
                .map(this::createTeam)
                .collect(Collectors.toList());
    }
}
