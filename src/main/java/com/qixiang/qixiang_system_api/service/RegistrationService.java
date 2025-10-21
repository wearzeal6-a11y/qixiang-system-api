package com.qixiang.qixiang_system_api.service;

import com.qixiang.qixiang_system_api.dto.RegistrationSummaryDTO;
import com.qixiang.qixiang_system_api.entity.*;
import com.qixiang.qixiang_system_api.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 报名数据汇总服务类
 * 提供报名数据统计和汇总的核心业务逻辑
 */
@Service
@Transactional(readOnly = true)
public class RegistrationService {
    
    private static final Logger logger = LoggerFactory.getLogger(RegistrationService.class);
    
    @Autowired
    private TeamRepository teamRepository;
    
    @Autowired
    private GroupRepository groupRepository;
    
    @Autowired
    private AthleteRepository athleteRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private RegistrationRepository registrationRepository;
    
    @Autowired
    private GroupEventMappingRepository groupEventMappingRepository;
    
    /**
     * 获取报名数据汇总
     * @param teamId 参赛单位ID
     * @return 报名数据汇总列表
     */
    public List<RegistrationSummaryDTO> getRegistrationSummary(Long teamId) {
        logger.info("开始获取参赛单位 {} 的报名数据汇总", teamId);
        
        List<RegistrationSummaryDTO> summary = new ArrayList<>();
        
        try {
            // 1. 获取参赛单位信息
            Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("参赛单位不存在，ID: " + teamId));
            
            logger.info("查询到参赛单位: {}", team.getName());
            
            // 2. 获取该参赛单位所属的组别（基于team.group_id）
            List<Group> teamGroups = new ArrayList<>();
            if (team.getGroupId() != null) {
                Group teamGroup = groupRepository.findById(team.getGroupId())
                    .orElseThrow(() -> new RuntimeException("参赛单位所属组别不存在，group_id: " + team.getGroupId()));
                teamGroups.add(teamGroup);
                logger.info("参赛单位 {} 属于组别: {}", team.getName(), teamGroup.getName());
            } else {
                logger.warn("参赛单位 {} 未设置所属组别，将返回空数据", team.getName());
                return summary; // 返回空列表
            }
            
            // 3. 按组别分别统计
            for (Group group : teamGroups) {
                if (!group.isActive()) {
                    logger.debug("跳过非激活状态的组别: {}", group.getName());
                    continue;
                }
                
                logger.debug("处理组别: {}", group.getName());
                processGroupStatistics(teamId, group, summary);
            }
            
            // 4. 添加总体统计信息
            addOverallStatistics(teamId, teamGroups, summary);
            
            logger.info("报名数据汇总完成，共生成 {} 条统计记录", summary.size());
            
        } catch (Exception e) {
            logger.error("获取报名数据汇总失败，teamId: {}, error: {}", teamId, e.getMessage(), e);
            throw new RuntimeException("获取报名数据汇总失败: " + e.getMessage(), e);
        }
        
        return summary;
    }
    
    /**
     * 处理单个组别的统计信息
     * @param teamId 参赛单位ID
     * @param group 竞赛组别
     * @param summary 汇总列表
     */
    private void processGroupStatistics(Long teamId, Group group, List<RegistrationSummaryDTO> summary) {
        String groupLabel = group.getName();
        
        // 3.1 领队教练统计（按组别）
        int actualLeaders = registrationRepository.countLeadersByTeamAndGroup(teamId, group.getId());
        summary.add(new RegistrationSummaryDTO(
            groupLabel + "领队教练", 
            group.getMaxLeadersPerTeam(), 
            actualLeaders,
            RegistrationSummaryDTO.SummaryType.LEADER.getCode(),
            group.getId(),
            group.getName(),
            null,
            null
        ));
        
        logger.debug("组别 {} 领队教练统计: 限制={}, 实际={}", groupLabel, group.getMaxLeadersPerTeam(), actualLeaders);
        
        // 3.2 运动员统计（按组别）
        int actualAthletes = athleteRepository.countByTeamAndGroup(teamId, group.getId());
        summary.add(new RegistrationSummaryDTO(
            groupLabel + "运动员", 
            group.getMaxAthletesPerTeam(), 
            actualAthletes,
            RegistrationSummaryDTO.SummaryType.ATHLETE.getCode(),
            group.getId(),
            group.getName(),
            null,
            null
        ));
        
        logger.debug("组别 {} 运动员统计: 限制={}, 实际={}", groupLabel, group.getMaxAthletesPerTeam(), actualAthletes);
        
        // 3.3 各项目报名统计（按组别）
        List<Event> availableEvents = eventRepository.findEventsByGroupId(group.getId());
        logger.debug("组别 {} 可参加项目数量: {}", groupLabel, availableEvents.size());
        
        for (Event event : availableEvents) {
            int actualCount = registrationRepository.countByTeamGroupAndEvent(teamId, group.getId(), event.getId());
            String eventLabel = groupLabel + event.getName();
            
            summary.add(new RegistrationSummaryDTO(
                eventLabel, 
                group.getMaxParticipantsPerEvent(), 
                actualCount,
                RegistrationSummaryDTO.SummaryType.EVENT.getCode(),
                group.getId(),
                group.getName(),
                event.getId(),
                event.getName()
            ));
            
            logger.debug("项目 {} 统计: 限制={}, 实际={}", eventLabel, group.getMaxParticipantsPerEvent(), actualCount);
        }
    }
    
    /**
     * 添加总体统计信息
     * @param teamId 参赛单位ID
     * @param teamGroups 组别列表
     * @param summary 汇总列表
     */
    private void addOverallStatistics(Long teamId, List<Group> teamGroups, List<RegistrationSummaryDTO> summary) {
        // 计算总限制
        int totalMaxAthletes = teamGroups.stream()
            .mapToInt(Group::getMaxAthletesPerTeam)
            .sum();
        int totalMaxLeaders = teamGroups.stream()
            .mapToInt(Group::getMaxLeadersPerTeam)
            .sum();
        
        // 计算实际数量
        int totalAthletes = athleteRepository.countByTeamId(teamId);
        int totalLeaders = registrationRepository.countAllLeadersByTeam(teamId);
        
        // 添加总体统计
        summary.add(new RegistrationSummaryDTO(
            "运动员总数", 
            totalMaxAthletes, 
            totalAthletes,
            RegistrationSummaryDTO.SummaryType.TOTAL.getCode(),
            null,
            null,
            null,
            null
        ));
        
        summary.add(new RegistrationSummaryDTO(
            "领队教练总数", 
            totalMaxLeaders, 
            totalLeaders,
            RegistrationSummaryDTO.SummaryType.TOTAL.getCode(),
            null,
            null,
            null,
            null
        ));
        
        logger.info("总体统计 - 运动员: 限制={}, 实际={}; 领队教练: 限制={}, 实际={}", 
                   totalMaxAthletes, totalAthletes, totalMaxLeaders, totalLeaders);
    }
    
    /**
     * 获取指定组别的详细报名统计
     * @param teamId 参赛单位ID
     * @param groupId 竞赛组别ID
     * @return 组别详细统计
     */
    public Map<String, Object> getGroupDetailedStatistics(Long teamId, Long groupId) {
        logger.info("获取参赛单位 {} 组别 {} 的详细统计", teamId, groupId);
        
        try {
            // 验证组别存在且属于参赛单位的运动会
            Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("组别不存在，ID: " + groupId));
            
            Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("参赛单位不存在，ID: " + teamId));
            
            if (!group.getSportsMeetId().equals(team.getSportsMeetId())) {
                throw new RuntimeException("组别不属于参赛单位的运动会");
            }
            
            // 获取组别统计信息
            Map<String, Object> groupStats = registrationRepository.getRegistrationStatisticsByTeamAndGroup(teamId, groupId);
            Map<String, Object> athleteStats = athleteRepository.getAthleteStatisticsByTeamAndGroup(teamId, groupId);
            
            // 合并统计信息
            if (groupStats != null && athleteStats != null) {
                groupStats.putAll(athleteStats);
            }
            
            // 添加组别基本信息
            groupStats.put("groupId", groupId);
            groupStats.put("groupName", group.getName());
            groupStats.put("groupGender", group.getGender());
            groupStats.put("groupGrade", group.getGrade());
            
            logger.info("组别详细统计获取成功");
            return groupStats;
            
        } catch (Exception e) {
            logger.error("获取组别详细统计失败，teamId: {}, groupId: {}, error: {}", teamId, groupId, e.getMessage(), e);
            throw new RuntimeException("获取组别详细统计失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取指定项目的报名统计
     * @param teamId 参赛单位ID
     * @param eventId 比赛项目ID
     * @return 项目报名统计
     */
    public Map<String, Object> getEventRegistrationStatistics(Long teamId, Long eventId) {
        logger.info("获取参赛单位 {} 项目 {} 的报名统计", teamId, eventId);
        
        try {
            // 验证项目存在
            Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("比赛项目不存在，ID: " + eventId));
            
            // 获取项目在各组别的报名情况
            List<Map<String, Object>> eventStats = new ArrayList<>();
            
            Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("参赛单位不存在，ID: " + teamId));
            
            List<Group> teamGroups = groupRepository.findBySportsMeetId(team.getSportsMeetId());
            
            for (Group group : teamGroups) {
                if (!group.isActive()) continue;
                
                // 检查组别是否可以参加该项目
                if (!eventRepository.isEventAvailableForGroup(eventId, group.getId())) {
                    continue;
                }
                
                int registrationCount = registrationRepository.countByTeamGroupAndEvent(teamId, group.getId(), eventId);
                
                Map<String, Object> stat = Map.of(
                    "groupId", group.getId(),
                    "groupName", group.getName(),
                    "registrationCount", registrationCount,
                    "maxParticipants", group.getMaxParticipantsPerEvent(),
                    "isOverLimit", registrationCount > group.getMaxParticipantsPerEvent()
                );
                
                eventStats.add(stat);
            }
            
            Map<String, Object> result = Map.of(
                "eventId", eventId,
                "eventName", event.getName(),
                "eventType", event.getEventType(),
                "groupStatistics", eventStats
            );
            
            logger.info("项目报名统计获取成功");
            return result;
            
        } catch (Exception e) {
            logger.error("获取项目报名统计失败，teamId: {}, eventId: {}, error: {}", teamId, eventId, e.getMessage(), e);
            throw new RuntimeException("获取项目报名统计失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 验证参赛单位是否有权限访问指定数据
     * @param teamId 参赛单位ID
     * @param targetTeamId 目标参赛单位ID
     * @return 是否有权限
     */
    public boolean hasPermissionToAccessData(Long teamId, Long targetTeamId) {
        // 简单的权限检查：只能访问自己的数据
        return teamId.equals(targetTeamId);
    }
    
    /**
     * 获取报名数据的概览信息
     * @param teamId 参赛单位ID
     * @return 概览信息
     */
    public Map<String, Object> getRegistrationOverview(Long teamId) {
        logger.info("获取参赛单位 {} 的报名数据概览", teamId);
        
        try {
            Map<String, Object> registrationStats = registrationRepository.getRegistrationStatisticsByTeamId(teamId);
            Map<String, Object> athleteStats = athleteRepository.getAthleteStatisticsByTeamId(teamId);
            
            // 合并统计信息
            if (registrationStats != null && athleteStats != null) {
                registrationStats.putAll(athleteStats);
            }
            
            // 添加项目类型统计
            List<Map<String, Object>> eventTypeStats = registrationRepository.getEventTypeStatisticsByTeamId(teamId);
            registrationStats.put("eventTypeStatistics", eventTypeStats);
            
            logger.info("报名数据概览获取成功");
            return registrationStats;
            
        } catch (Exception e) {
            logger.error("获取报名数据概览失败，teamId: {}, error: {}", teamId, e.getMessage(), e);
            throw new RuntimeException("获取报名数据概览失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取指定运动员已报名的项目ID列表
     * @param teamId 参赛单位ID
     * @param athleteId 运动员ID
     * @return 已报名项目ID列表
     */
    public List<Long> getAthleteRegisteredEventIds(Long teamId, Long athleteId) {
        logger.info("获取参赛单位 {} 运动员 {} 已报名的项目ID列表", teamId, athleteId);
        
        try {
            // 验证运动员属于该参赛单位
            Athlete athlete = athleteRepository.findById(athleteId)
                .orElseThrow(() -> new RuntimeException("运动员不存在，ID: " + athleteId));
            
            if (!athlete.getTeamId().equals(teamId)) {
                throw new RuntimeException("运动员不属于该参赛单位");
            }
            
            // 获取已报名的项目
            List<Registration> registrations = registrationRepository.findByAthleteId(athleteId);
            List<Long> eventIds = new ArrayList<>();
            
            for (Registration registration : registrations) {
                if (registration.getTeamId().equals(teamId) && 
                    "CONFIRMED".equals(registration.getStatus())) {
                    eventIds.add(registration.getEventId());
                }
            }
            
            logger.info("获取运动员 {} 已报名项目成功，共 {} 个项目", athleteId, eventIds.size());
            return eventIds;
            
        } catch (Exception e) {
            logger.error("获取运动员 {} 已报名项目失败: {}", athleteId, e.getMessage(), e);
            throw new RuntimeException("获取运动员已报名项目失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 更新运动员的项目报名
     * @param teamId 参赛单位ID
     * @param athleteId 运动员ID
     * @param eventIds 项目ID列表
     */
    @Transactional
    public void updateAthleteRegistrations(Long teamId, Long athleteId, List<Long> eventIds) {
        logger.info("=== 开始更新运动员项目报名 ===");
        logger.info("参赛单位ID: {}, 运动员ID: {}, 项目数量: {}, 项目IDs: {}", teamId, athleteId, eventIds.size(), eventIds);
        
        try {
            // 验证运动员属于该参赛单位
            logger.info("步骤1: 验证运动员 {} 属于参赛单位 {}", athleteId, teamId);
            Athlete athlete = athleteRepository.findById(athleteId)
                .orElseThrow(() -> new RuntimeException("运动员不存在，ID: " + athleteId));
            
            if (!athlete.getTeamId().equals(teamId)) {
                logger.error("运动员 {} 的参赛单位ID {} 与请求的参赛单位ID {} 不匹配", athleteId, athlete.getTeamId(), teamId);
                throw new RuntimeException("运动员不属于该参赛单位");
            }
            logger.info("运动员验证通过 - ID: {}, 姓名: {}, 参赛单位ID: {}", athleteId, athlete.getName(), athlete.getTeamId());
            
            // 验证组别信息
            logger.info("步骤2: 验证运动员所属组别，组别ID: {}", athlete.getGroupId());
            Group group = groupRepository.findById(athlete.getGroupId())
                .orElseThrow(() -> new RuntimeException("运动员所属组别不存在，ID: " + athlete.getGroupId()));
            
            logger.info("组别验证通过 - ID: {}, 名称: {}, 最大报名项目数: {}", group.getId(), group.getName(), group.getMaxEventsPerAthlete());
            
            // 验证项目数量不超过限制
            if (eventIds.size() > group.getMaxEventsPerAthlete()) {
                logger.error("报名项目数量 {} 超过限制 {}", eventIds.size(), group.getMaxEventsPerAthlete());
                throw new RuntimeException("报名项目数量超过限制，最多允许 " + group.getMaxEventsPerAthlete() + " 个项目");
            }
            logger.info("项目数量验证通过，{}/{}", eventIds.size(), group.getMaxEventsPerAthlete());
            
            // 获取现有报名记录（用于容量检查）
            logger.info("步骤3: 获取现有报名记录");
            List<Registration> existingRegistrations = registrationRepository.findByAthleteId(athleteId);
            logger.info("找到 {} 条现有报名记录", existingRegistrations.size());
            
            // 验证所有项目都是该组别可以参加的，并检查容量限制
            logger.info("步骤4: 验证项目是否属于该组别可参加的项目，并检查容量限制");
            for (Long eventId : eventIds) {
                boolean isAvailable = eventRepository.isEventAvailableForGroup(eventId, athlete.getGroupId());
                logger.info("项目 {} 对组别 {} 的可用性: {}", eventId, athlete.getGroupId(), isAvailable);
                
                if (!isAvailable) {
                    logger.error("项目 ID {} 不属于组别 {} 可参加的项目", eventId, athlete.getGroupId());
                    throw new RuntimeException("项目 ID " + eventId + " 不属于该组别可参加的项目");
                }
                
                // 检查项目容量限制
                Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new RuntimeException("项目不存在，ID: " + eventId));
                
                if (Boolean.TRUE.equals(event.getIsCapacityLimited())) {
                    // 计算当前报名人数（包括这个运动员将要报名的）
                    int currentCount = registrationRepository.countByEventId(eventId);
                    
                    // 检查这个运动员是否已经报名了这个项目
                    boolean alreadyRegistered = existingRegistrations.stream()
                        .anyMatch(reg -> reg.getEventId().equals(eventId) && "CONFIRMED".equals(reg.getStatus()));
                    
                    // 如果还没有报名，需要增加计数
                    if (!alreadyRegistered) {
                        currentCount++;
                    }
                    
                    logger.info("项目 {} 容量检查: 当前={}, 限制={}, 运动员已报名={}", 
                        event.getName(), currentCount, event.getMaxParticipants(), alreadyRegistered);
                    
                    if (currentCount > event.getMaxParticipants()) {
                        logger.error("项目 {} 已满员，当前报名人数 {} 超过限制 {}", 
                            event.getName(), currentCount, event.getMaxParticipants());
                        throw new RuntimeException("项目 " + event.getName() + " 已满员，无法报名");
                    }
                } else {
                    logger.info("项目 {} 未设置容量限制", event.getName());
                }
            }
            logger.info("所有项目验证通过，共 {} 个项目", eventIds.size());
            
            // 删除现有的报名记录
            logger.info("步骤5: 删除运动员 {} 的现有报名记录", athleteId);
            
            int deletedCount = 0;
            for (Registration registration : existingRegistrations) {
                if (registration.getTeamId().equals(teamId)) {
                    logger.info("删除报名记录: 运动员ID: {}, 项目ID: {}, 状态: {}", 
                        registration.getAthleteId(), registration.getEventId(), registration.getStatus());
                    registrationRepository.delete(registration);
                    deletedCount++;
                } else {
                    logger.warn("跳过不属于参赛单位 {} 的报名记录: 运动员ID: {}, 项目ID: {}", 
                        teamId, registration.getAthleteId(), registration.getEventId());
                }
            }
            logger.info("删除完成，共删除 {} 条报名记录", deletedCount);
            
            // 创建新的报名记录
            logger.info("步骤6: 创建新的报名记录");
            int createdCount = 0;
            for (Long eventId : eventIds) {
                logger.info("创建报名记录: 运动员ID: {}, 项目ID: {}", athleteId, eventId);
                
                Registration registration = new Registration();
                registration.setTeamId(teamId);
                registration.setGroupId(athlete.getGroupId());
                registration.setAthleteId(athleteId);
                registration.setEventId(eventId);
                registration.setStatus("CONFIRMED");
                registration.setRegistrationTime(java.time.LocalDateTime.now());
                
                Registration saved = registrationRepository.save(registration);
                logger.info("保存报名记录成功: ID: {}, 运动员ID: {}, 项目ID: {}", 
                    saved.getId(), saved.getAthleteId(), saved.getEventId());
                createdCount++;
            }
            logger.info("创建完成，共创建 {} 条新报名记录", createdCount);
            
            logger.info("=== 运动员 {} 项目报名更新成功 ===", athleteId);
            logger.info("最终状态: 报名项目数: {}, 项目IDs: {}", eventIds.size(), eventIds);
            
        } catch (Exception e) {
            logger.error("=== 运动员 {} 项目报名更新失败 ===", athleteId);
            logger.error("错误类型: {}", e.getClass().getSimpleName());
            logger.error("错误信息: {}", e.getMessage());
            logger.error("完整堆栈跟踪:", e);
            throw new RuntimeException("更新运动员项目报名失败: " + e.getMessage(), e);
        }
    }
}
