package com.qixiang.qixiang_system_api.service;

import com.qixiang.qixiang_system_api.dto.AthleteDTO;
import com.qixiang.qixiang_system_api.dto.GroupDTO;
import com.qixiang.qixiang_system_api.entity.Athlete;
import com.qixiang.qixiang_system_api.entity.Group;
import com.qixiang.qixiang_system_api.repository.AthleteRepository;
import com.qixiang.qixiang_system_api.repository.GroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 运动员业务服务类
 * 提供运动员相关的业务逻辑处理
 */
@Service
@Transactional
public class AthleteService {
    
    private static final Logger logger = LoggerFactory.getLogger(AthleteService.class);
    
    @Autowired
    private AthleteRepository athleteRepository;
    
    @Autowired
    private GroupRepository groupRepository;
    
    /**
     * 根据参赛单位ID获取运动员列表
     * @param teamId 参赛单位ID
     * @return 运动员DTO列表
     */
    @Cacheable(value = "athletes", key = "'team:' + #teamId")
    public List<AthleteDTO> getAthletesByTeamId(Long teamId) {
        logger.info("从数据库查询参赛单位 {} 的运动员列表", teamId);
        
        try {
            // 获取运动员实体列表
            List<Athlete> athletes = athleteRepository.findByTeamId(teamId);
            
            // 转换为DTO列表
            List<AthleteDTO> athleteDTOs = athletes.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            logger.info("成功获取 {} 个运动员", athleteDTOs.size());
            return athleteDTOs;
            
        } catch (Exception e) {
            logger.error("获取运动员列表失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取运动员列表失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 添加新运动员
     * @param athleteDTO 运动员DTO
     * @return 保存后的运动员DTO
     */
    @CacheEvict(value = "athletes", allEntries = true)
    public AthleteDTO addAthlete(AthleteDTO athleteDTO) {
        logger.info("添加新运动员，清除所有运动员缓存: {}", athleteDTO.getName());
        
        try {
            // 验证组别是否存在
            Group group = groupRepository.findById(athleteDTO.getGroupId())
                    .orElseThrow(() -> new RuntimeException("竞赛组别不存在"));
            
            // 检查是否超出运动员名额限制
            int currentCount = athleteRepository.countByTeamAndGroup(
                    athleteDTO.getTeamId(), athleteDTO.getGroupId());
            if (currentCount >= group.getMaxAthletesPerTeam()) {
                throw new RuntimeException("该组别运动员名额已满（最大" + 
                        group.getMaxAthletesPerTeam() + "人）");
            }
            
            // 检查身份证号是否重复（如果提供了身份证号）
            if (athleteDTO.getIdNumber() != null && !athleteDTO.getIdNumber().trim().isEmpty()) {
                if (athleteRepository.findByTeamIdAndIdNumber(athleteDTO.getTeamId(), 
                        athleteDTO.getIdNumber()).isPresent()) {
                    throw new RuntimeException("该身份证号已存在");
                }
            }
            
            // 转换为实体并保存
            Athlete athlete = convertToEntity(athleteDTO);
            Athlete savedAthlete = athleteRepository.save(athlete);
            
            // 转换回DTO并返回
            AthleteDTO result = convertToDTO(savedAthlete);
            logger.info("运动员添加成功: {}", result.getName());
            return result;
            
        } catch (RuntimeException e) {
            logger.error("添加运动员失败: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("添加运动员失败: {}", e.getMessage(), e);
            throw new RuntimeException("添加运动员失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 更新运动员信息
     * @param id 运动员ID
     * @param athleteDTO 运动员DTO
     * @param teamId 参赛单位ID（用于权限验证）
     * @return 更新后的运动员DTO
     */
    @CacheEvict(value = "athletes", allEntries = true)
    public AthleteDTO updateAthlete(Long id, AthleteDTO athleteDTO, Long teamId) {
        logger.info("更新运动员信息，清除所有运动员缓存: ID={}, 新姓名={}", id, athleteDTO.getName());
        
        try {
            // 验证运动员是否存在且属于该参赛单位
            Athlete existingAthlete = athleteRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("运动员不存在"));
            
            if (!existingAthlete.getTeamId().equals(teamId)) {
                throw new RuntimeException("无权限修改该运动员信息");
            }
            
            // 验证新的组别是否存在
            Group group = groupRepository.findById(athleteDTO.getGroupId())
                    .orElseThrow(() -> new RuntimeException("竞赛组别不存在"));
            
            // 如果组别发生变化，检查新组别的名额限制
            if (!existingAthlete.getGroupId().equals(athleteDTO.getGroupId())) {
                int currentCount = athleteRepository.countByTeamAndGroup(
                        teamId, athleteDTO.getGroupId());
                if (currentCount >= group.getMaxAthletesPerTeam()) {
                    throw new RuntimeException("该组别运动员名额已满（最大" + 
                            group.getMaxAthletesPerTeam() + "人）");
                }
            }
            
            // 检查身份证号是否重复（排除自己）
            if (athleteDTO.getIdNumber() != null && !athleteDTO.getIdNumber().trim().isEmpty()) {
                if (athleteRepository.findByTeamIdAndIdNumber(teamId, athleteDTO.getIdNumber())
                        .filter(a -> !a.getId().equals(id))
                        .isPresent()) {
                    throw new RuntimeException("该身份证号已存在");
                }
            }
            
            // 更新运动员信息
            updateEntityFromDTO(existingAthlete, athleteDTO);
            Athlete updatedAthlete = athleteRepository.save(existingAthlete);
            
            // 转换为DTO并返回
            AthleteDTO result = convertToDTO(updatedAthlete);
            logger.info("运动员信息更新成功: {}", result.getName());
            return result;
            
        } catch (RuntimeException e) {
            logger.error("更新运动员信息失败: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("更新运动员信息失败: {}", e.getMessage(), e);
            throw new RuntimeException("更新运动员信息失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 删除运动员
     * @param id 运动员ID
     * @param teamId 参赛单位ID（用于权限验证）
     */
    @CacheEvict(value = "athletes", allEntries = true)
    public void deleteAthlete(Long id, Long teamId) {
        logger.info("删除运动员，清除所有运动员缓存: ID={}", id);
        
        try {
            // 验证运动员是否存在且属于该参赛单位
            Athlete athlete = athleteRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("运动员不存在"));
            
            if (!athlete.getTeamId().equals(teamId)) {
                throw new RuntimeException("无权限删除该运动员");
            }
            
            // TODO: 检查该运动员是否已有报名记录，如果有则不允许删除
            
            // 删除运动员
            athleteRepository.deleteById(id);
            logger.info("运动员删除成功: {}", athlete.getName());
            
        } catch (RuntimeException e) {
            logger.error("删除运动员失败: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("删除运动员失败: {}", e.getMessage(), e);
            throw new RuntimeException("删除运动员失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 根据ID获取运动员详细信息
     * @param id 运动员ID
     * @param teamId 参赛单位ID（用于权限验证）
     * @return 运动员DTO
     */
    public AthleteDTO getAthleteById(Long id, Long teamId) {
        logger.info("获取运动员详情: ID={}", id);
        
        try {
            // 验证运动员是否存在且属于该参赛单位
            Athlete athlete = athleteRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("运动员不存在"));
            
            if (!athlete.getTeamId().equals(teamId)) {
                throw new RuntimeException("无权限查看该运动员信息");
            }
            
            // 转换为DTO并返回
            AthleteDTO result = convertToDTO(athlete);
            logger.info("运动员详情获取成功: {}", result.getName());
            return result;
            
        } catch (RuntimeException e) {
            logger.error("获取运动员详情失败: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("获取运动员详情失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取运动员详情失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 将Athlete实体转换为AthleteDTO
     * @param athlete 运动员实体
     * @return 运动员DTO
     */
    private AthleteDTO convertToDTO(Athlete athlete) {
        AthleteDTO dto = new AthleteDTO();
        dto.setId(athlete.getId());
        dto.setTeamId(athlete.getTeamId());
        dto.setGroupId(athlete.getGroupId());
        dto.setName(athlete.getName());
        dto.setIdNumber(athlete.getIdNumber());
        dto.setContactPhone(athlete.getContactPhone());
        dto.setCreatedAt(athlete.getCreatedAt());
        dto.setUpdatedAt(athlete.getUpdatedAt());
        
        // 获取组别信息
        try {
            Group group = groupRepository.findById(athlete.getGroupId()).orElse(null);
            if (group != null) {
                dto.setGender(group.getGender());
                dto.setGroupName(group.getName());
                
                // 创建GroupDTO对象
                GroupDTO groupDTO = new GroupDTO();
                groupDTO.setId(group.getId());
                groupDTO.setName(group.getName());
                groupDTO.setGender(group.getGender());
                groupDTO.setGrade(group.getGrade());
                groupDTO.setMaxAthletesPerTeam(group.getMaxAthletesPerTeam());
                groupDTO.setMaxEventsPerAthlete(group.getMaxEventsPerAthlete());
                
                dto.setGroup(groupDTO);
            }
        } catch (Exception e) {
            logger.warn("获取组别信息失败: {}", e.getMessage());
        }
        
        // TODO: 获取已报项目列表
        dto.setRegisteredEvents(new ArrayList<>());
        
        return dto;
    }
    
    /**
     * 将AthleteDTO转换为Athlete实体
     * @param dto 运动员DTO
     * @return 运动员实体
     */
    private Athlete convertToEntity(AthleteDTO dto) {
        Athlete athlete = new Athlete();
        athlete.setTeamId(dto.getTeamId());
        athlete.setGroupId(dto.getGroupId());
        athlete.setName(dto.getName());
        athlete.setIdNumber(dto.getIdNumber());
        athlete.setContactPhone(dto.getContactPhone());
        
        return athlete;
    }
    
    /**
     * 使用DTO更新实体信息
     * @param athlete 运动员实体
     * @param dto 运动员DTO
     */
    private void updateEntityFromDTO(Athlete athlete, AthleteDTO dto) {
        athlete.setGroupId(dto.getGroupId());
        athlete.setName(dto.getName());
        athlete.setIdNumber(dto.getIdNumber());
        athlete.setContactPhone(dto.getContactPhone());
    }
    
    /**
     * 获取参赛单位的运动员统计信息
     * @param teamId 参赛单位ID
     * @return 统计信息
     */
    public Map<String, Object> getAthleteStatistics(Long teamId) {
        logger.info("获取参赛单位 {} 的运动员统计信息", teamId);
        
        try {
            Map<String, Object> statistics = athleteRepository.getAthleteStatisticsByTeamId(teamId);
            
            // 添加按性别统计
            int maleCount = athleteRepository.countByTeamIdAndGender(teamId, "MALE");
            int femaleCount = athleteRepository.countByTeamIdAndGender(teamId, "FEMALE");
            
            statistics.put("maleCount", maleCount);
            statistics.put("femaleCount", femaleCount);
            
            // 添加按年级统计
            List<Map<String, Object>> gradeStatistics = athleteRepository.countByTeamIdAndGrade(teamId);
            statistics.put("gradeStatistics", gradeStatistics);
            
            logger.info("运动员统计信息获取成功");
            return statistics;
            
        } catch (Exception e) {
            logger.error("获取运动员统计信息失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取运动员统计信息失败: " + e.getMessage(), e);
        }
    }
}
