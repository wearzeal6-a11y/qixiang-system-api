package com.qixiang.qixiang_system_api.service;

import com.qixiang.qixiang_system_api.entity.Group;
import com.qixiang.qixiang_system_api.repository.GroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 竞赛组别服务类
 * 提供竞赛组别相关的业务逻辑和缓存管理
 */
@Service
public class GroupService {
    
    private static final Logger logger = LoggerFactory.getLogger(GroupService.class);
    
    @Autowired
    private GroupRepository groupRepository;
    
    /**
     * 获取所有激活的组别列表
     * @return 组别列表
     */
    @Cacheable(value = "groups", key = "'all_active'")
    public List<Group> getAllActiveGroups() {
        logger.info("从数据库获取所有激活的组别列表");
        return groupRepository.findByStatus("ACTIVE");
    }
    
    /**
     * 根据ID获取组别详情
     * @param id 组别ID
     * @return 组别详情
     */
    @Cacheable(value = "groups", key = "#id")
    public Optional<Group> getGroupById(Long id) {
        logger.info("从数据库获取组别详情: ID={}", id);
        return groupRepository.findById(id);
    }
    
    /**
     * 根据性别获取组别列表
     * @param gender 性别
     * @return 组别列表
     */
    @Cacheable(value = "groups", key = "'gender:' + #gender")
    public List<Group> getGroupsByGender(String gender) {
        logger.info("从数据库根据性别获取组别列表: gender={}", gender);
        return groupRepository.findByGenderAndStatus(gender, "ACTIVE");
    }
    
    /**
     * 根据年级获取组别列表
     * @param grade 年级
     * @return 组别列表
     */
    @Cacheable(value = "groups", key = "'grade:' + #grade")
    public List<Group> getGroupsByGrade(String grade) {
        logger.info("从数据库根据年级获取组别列表: grade={}", grade);
        return groupRepository.findByGradeAndStatus(grade, "ACTIVE");
    }
    
    /**
     * 根据运动会ID获取所有组别
     * @param sportsMeetId 运动会ID
     * @return 组别列表
     */
    @Cacheable(value = "groups", key = "'sports_meet:' + #sportsMeetId")
    public List<Group> getGroupsBySportsMeetId(Long sportsMeetId) {
        logger.info("从数据库根据运动会ID获取组别列表: sportsMeetId={}", sportsMeetId);
        return groupRepository.findBySportsMeetId(sportsMeetId);
    }
    
    /**
     * 根据运动会ID获取激活的组别列表
     * @param sportsMeetId 运动会ID
     * @return 组别列表
     */
    @Cacheable(value = "groups", key = "'sports_meet_active:' + #sportsMeetId")
    public List<Group> getActiveGroupsBySportsMeetId(Long sportsMeetId) {
        logger.info("从数据库根据运动会ID获取激活组别列表: sportsMeetId={}", sportsMeetId);
        return groupRepository.findBySportsMeetIdAndStatus(sportsMeetId, "ACTIVE");
    }
    
    /**
     * 根据运动会ID和性别获取组别列表
     * @param sportsMeetId 运动会ID
     * @param gender 性别
     * @return 组别列表
     */
    @Cacheable(value = "groups", key = "'sports_meet:' + #sportsMeetId + ':gender:' + #gender")
    public List<Group> getGroupsBySportsMeetIdAndGender(Long sportsMeetId, String gender) {
        logger.info("从数据库根据运动会ID和性别获取组别列表: sportsMeetId={}, gender={}", sportsMeetId, gender);
        return groupRepository.findBySportsMeetIdAndGender(sportsMeetId, gender);
    }
    
    /**
     * 根据运动会ID和年级获取组别列表
     * @param sportsMeetId 运动会ID
     * @param grade 年级
     * @return 组别列表
     */
    @Cacheable(value = "groups", key = "'sports_meet:' + #sportsMeetId + ':grade:' + #grade")
    public List<Group> getGroupsBySportsMeetIdAndGrade(Long sportsMeetId, String grade) {
        logger.info("从数据库根据运动会ID和年级获取组别列表: sportsMeetId={}, grade={}", sportsMeetId, grade);
        return groupRepository.findBySportsMeetIdAndGrade(sportsMeetId, grade);
    }
    
    /**
     * 根据运动会ID、性别和年级获取组别列表
     * @param sportsMeetId 运动会ID
     * @param gender 性别
     * @param grade 年级
     * @return 组别列表
     */
    @Cacheable(value = "groups", key = "'sports_meet:' + #sportsMeetId + ':gender:' + #gender + ':grade:' + #grade")
    public List<Group> getGroupsBySportsMeetIdAndGenderAndGrade(Long sportsMeetId, String gender, String grade) {
        logger.info("从数据库根据运动会ID、性别和年级获取组别列表: sportsMeetId={}, gender={}, grade={}", sportsMeetId, gender, grade);
        return groupRepository.findBySportsMeetIdAndGenderAndGrade(sportsMeetId, gender, grade);
    }
    
    /**
     * 根据组别名称查询组别
     * @param name 组别名称
     * @return 组别信息
     */
    @Cacheable(value = "groups", key = "'name:' + #name")
    public Optional<Group> getGroupByName(String name) {
        logger.info("从数据库根据名称获取组别: name={}", name);
        return groupRepository.findByName(name);
    }
    
    /**
     * 根据运动会ID和组别名称查询组别
     * @param sportsMeetId 运动会ID
     * @param name 组别名称
     * @return 组别信息
     */
    @Cacheable(value = "groups", key = "'sports_meet:' + #sportsMeetId + ':name:' + #name")
    public Optional<Group> getGroupBySportsMeetIdAndName(Long sportsMeetId, String name) {
        logger.info("从数据库根据运动会ID和名称获取组别: sportsMeetId={}, name={}", sportsMeetId, name);
        return groupRepository.findBySportsMeetIdAndName(sportsMeetId, name);
    }
    
    /**
     * 保存或更新组别
     * @param group 组别信息
     * @return 保存后的组别信息
     */
    @CacheEvict(value = "groups", allEntries = true)
    public Group saveGroup(Group group) {
        logger.info("保存组别信息，清除所有组别缓存: {}", group.getName());
        return groupRepository.save(group);
    }
    
    /**
     * 删除组别
     * @param id 组别ID
     */
    @CacheEvict(value = "groups", allEntries = true)
    public void deleteGroup(Long id) {
        logger.info("删除组别，清除所有组别缓存: ID={}", id);
        groupRepository.deleteById(id);
    }
    
    /**
     * 批量删除运动会的所有组别
     * @param sportsMeetId 运动会ID
     * @return 删除的记录数
     */
    @CacheEvict(value = "groups", allEntries = true)
    public int deleteGroupsBySportsMeetId(Long sportsMeetId) {
        logger.info("批量删除运动会组别，清除所有组别缓存: sportsMeetId={}", sportsMeetId);
        return groupRepository.deleteBySportsMeetId(sportsMeetId);
    }
    
    /**
     * 批量更新运动会中组别的状态
     * @param sportsMeetId 运动会ID
     * @param status 新状态
     * @return 更新的记录数
     */
    @CacheEvict(value = "groups", allEntries = true)
    public int updateGroupStatusBySportsMeetId(Long sportsMeetId, String status) {
        logger.info("批量更新运动会组别状态，清除所有组别缓存: sportsMeetId={}, status={}", sportsMeetId, status);
        return groupRepository.updateStatusBySportsMeetId(sportsMeetId, status);
    }
    
    /**
     * 统计运动会中的组别数量
     * @param sportsMeetId 运动会ID
     * @return 组别数量
     */
    @Cacheable(value = "groups", key = "'count:sports_meet:' + #sportsMeetId")
    public int countGroupsBySportsMeetId(Long sportsMeetId) {
        logger.info("从数据库统计运动会组别数量: sportsMeetId={}", sportsMeetId);
        return groupRepository.countBySportsMeetId(sportsMeetId);
    }
    
    /**
     * 统计运动会中激活状态的组别数量
     * @param sportsMeetId 运动会ID
     * @return 激活组别数量
     */
    @Cacheable(value = "groups", key = "'count_active:sports_meet:' + #sportsMeetId")
    public int countActiveGroupsBySportsMeetId(Long sportsMeetId) {
        logger.info("从数据库统计运动会激活组别数量: sportsMeetId={}", sportsMeetId);
        return groupRepository.countActiveGroupsBySportsMeetId(sportsMeetId);
    }
    
    /**
     * 查询运动会中所有不同的年级
     * @param sportsMeetId 运动会ID
     * @return 年级列表
     */
    @Cacheable(value = "groups", key = "'distinct_grades:sports_meet:' + #sportsMeetId")
    public List<String> getDistinctGradesBySportsMeetId(Long sportsMeetId) {
        logger.info("从数据库查询运动会不同年级: sportsMeetId={}", sportsMeetId);
        return groupRepository.findDistinctGradesBySportsMeetId(sportsMeetId);
    }
    
    /**
     * 查询运动会中所有不同的性别
     * @param sportsMeetId 运动会ID
     * @return 性别列表
     */
    @Cacheable(value = "groups", key = "'distinct_genders:sports_meet:' + #sportsMeetId")
    public List<String> getDistinctGendersBySportsMeetId(Long sportsMeetId) {
        logger.info("从数据库查询运动会不同性别: sportsMeetId={}", sportsMeetId);
        return groupRepository.findDistinctGendersBySportsMeetId(sportsMeetId);
    }
    
    /**
     * 检查组别名称是否已存在（在同一运动会内）
     * @param sportsMeetId 运动会ID
     * @param name 组别名称
     * @param excludeId 排除的组别ID（用于更新时检查）
     * @return 是否存在
     */
    @Cacheable(value = "groups", key = "'exists_name:sports_meet:' + #sportsMeetId + ':name:' + #name + ':exclude:' + #excludeId")
    public boolean existsByNameAndSportsMeetId(String name, Long sportsMeetId, Long excludeId) {
        logger.info("从数据库检查组别名称是否存在: sportsMeetId={}, name={}, excludeId={}", sportsMeetId, name, excludeId);
        return groupRepository.existsByNameAndSportsMeetId(name, sportsMeetId, excludeId);
    }
    
    /**
     * 清除所有组别缓存
     */
    @CacheEvict(value = "groups", allEntries = true)
    public void clearAllGroupsCache() {
        logger.info("手动清除所有组别缓存");
    }
}
