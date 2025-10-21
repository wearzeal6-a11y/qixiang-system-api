package com.qixiang.qixiang_system_api.repository;

import com.qixiang.qixiang_system_api.entity.GroupEventMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 组别-项目关联数据访问接口
 * 提供组别与项目关联关系相关的数据库操作
 */
@Repository
public interface GroupEventMappingRepository extends JpaRepository<GroupEventMapping, Long> {
    
    /**
     * 根据组别ID查询所有关联的项目
     * @param groupId 竞赛组别ID
     * @return 关联关系列表
     */
    List<GroupEventMapping> findByGroupId(Long groupId);
    
    /**
     * 根据项目ID查询所有关联的组别
     * @param eventId 比赛项目ID
     * @return 关联关系列表
     */
    List<GroupEventMapping> findByEventId(Long eventId);
    
    /**
     * 根据组别ID查询必报项目关联
     * @param groupId 竞赛组别ID
     * @return 必报项目关联列表
     */
    List<GroupEventMapping> findByGroupIdAndIsMandatoryTrue(Long groupId);
    
    /**
     * 根据组别ID查询选报项目关联
     * @param groupId 竞赛组别ID
     * @return 选报项目关联列表
     */
    List<GroupEventMapping> findByGroupIdAndIsMandatoryFalse(Long groupId);
    
    /**
     * 根据组别ID和项目ID查询关联关系
     * @param groupId 竞赛组别ID
     * @param eventId 比赛项目ID
     * @return 关联关系
     */
    Optional<GroupEventMapping> findByGroupIdAndEventId(Long groupId, Long eventId);
    
    /**
     * 检查组别和项目是否已关联
     * @param groupId 竞赛组别ID
     * @param eventId 比赛项目ID
     * @return 是否已关联
     */
    boolean existsByGroupIdAndEventId(Long groupId, Long eventId);
    
    /**
     * 根据组别ID统计关联的项目数量
     * @param groupId 竞赛组别ID
     * @return 关联项目数量
     */
    @Query("SELECT COUNT(gem) FROM GroupEventMapping gem WHERE gem.groupId = :groupId")
    int countByGroupId(@Param("groupId") Long groupId);
    
    /**
     * 根据项目ID统计关联的组别数量
     * @param eventId 比赛项目ID
     * @return 关联组别数量
     */
    @Query("SELECT COUNT(gem) FROM GroupEventMapping gem WHERE gem.eventId = :eventId")
    int countByEventId(@Param("eventId") Long eventId);
    
    /**
     * 根据组别ID统计必报项目数量
     * @param groupId 竞赛组别ID
     * @return 必报项目数量
     */
    @Query("SELECT COUNT(gem) FROM GroupEventMapping gem " +
           "WHERE gem.groupId = :groupId AND gem.isMandatory = true")
    int countMandatoryEventsByGroupId(@Param("groupId") Long groupId);
    
    /**
     * 根据组别ID统计选报项目数量
     * @param groupId 竞赛组别ID
     * @return 选报项目数量
     */
    @Query("SELECT COUNT(gem) FROM GroupEventMapping gem " +
           "WHERE gem.groupId = :groupId AND gem.isMandatory = false")
    int countOptionalEventsByGroupId(@Param("groupId") Long groupId);
    
    /**
     * 根据多个组别ID查询关联的项目ID列表
     * @param groupIds 竞赛组别ID列表
     * @return 项目ID列表
     */
    @Query("SELECT DISTINCT gem.eventId FROM GroupEventMapping gem WHERE gem.groupId IN :groupIds")
    List<Long> findEventIdsByGroupIds(@Param("groupIds") List<Long> groupIds);
    
    /**
     * 根据多个项目ID查询关联的组别ID列表
     * @param eventIds 比赛项目ID列表
     * @return 组别ID列表
     */
    @Query("SELECT DISTINCT gem.groupId FROM GroupEventMapping gem WHERE gem.eventId IN :eventIds")
    List<Long> findGroupIdsByEventIds(@Param("eventIds") List<Long> eventIds);
    
    /**
     * 批量删除组别的所有关联关系
     * @param groupId 竞赛组别ID
     * @return 删除的记录数
     */
    @Query("DELETE FROM GroupEventMapping gem WHERE gem.groupId = :groupId")
    int deleteByGroupId(@Param("groupId") Long groupId);
    
    /**
     * 批量删除项目的所有关联关系
     * @param eventId 比赛项目ID
     * @return 删除的记录数
     */
    @Query("DELETE FROM GroupEventMapping gem WHERE gem.eventId = :eventId")
    int deleteByEventId(@Param("eventId") Long eventId);
    
    /**
     * 查询没有关联任何项目的组别
     * @return 组别ID列表
     */
    @Query("SELECT g.id FROM Group g " +
           "WHERE NOT EXISTS (SELECT 1 FROM GroupEventMapping gem WHERE gem.groupId = g.id)")
    List<Long> findUnassignedGroups();
    
    /**
     * 查询没有关联任何组别的项目
     * @return 项目ID列表
     */
    @Query("SELECT e.id FROM Event e " +
           "WHERE NOT EXISTS (SELECT 1 FROM GroupEventMapping gem WHERE gem.eventId = e.id)")
    List<Long> findUnassignedEvents();
    
    /**
     * 根据组别ID查询关联项目的详细信息
     * @param groupId 竞赛组别ID
     * @return 项目信息映射列表
     */
    @Query("SELECT " +
           "e.id as eventId, " +
           "e.name as eventName, " +
           "e.eventType as eventType, " +
           "gem.isMandatory as isMandatory, " +
           "gem.createdAt as mappingTime " +
           "FROM GroupEventMapping gem " +
           "JOIN Event e ON gem.eventId = e.id " +
           "WHERE gem.groupId = :groupId " +
           "ORDER BY gem.isMandatory DESC, e.name")
    List<Object[]> findEventDetailsByGroupId(@Param("groupId") Long groupId);
    
    /**
     * 根据项目ID查询关联组别的详细信息
     * @param eventId 比赛项目ID
     * @return 组别信息映射列表
     */
    @Query("SELECT " +
           "g.id as groupId, " +
           "g.name as groupName, " +
           "g.gender as gender, " +
           "g.grade as grade, " +
           "gem.isMandatory as isMandatory, " +
           "gem.createdAt as mappingTime " +
           "FROM GroupEventMapping gem " +
           "JOIN Group g ON gem.groupId = g.id " +
           "WHERE gem.eventId = :eventId " +
           "ORDER BY g.name")
    List<Object[]> findGroupDetailsByEventId(@Param("eventId") Long eventId);
    
    /**
     * 根据运动会ID查询所有组别项目关联统计
     * @param sportsMeetId 运动会ID
     * @return 统计信息映射列表
     */
    @Query("SELECT " +
           "g.id as groupId, " +
           "g.name as groupName, " +
           "COUNT(gem) as totalEvents, " +
           "SUM(CASE WHEN gem.isMandatory = true THEN 1 ELSE 0 END) as mandatoryEvents, " +
           "SUM(CASE WHEN gem.isMandatory = false THEN 1 ELSE 0 END) as optionalEvents " +
           "FROM Group g " +
           "LEFT JOIN GroupEventMapping gem ON g.id = gem.groupId " +
           "WHERE g.sportsMeetId = :sportsMeetId " +
           "GROUP BY g.id, g.name " +
           "ORDER BY g.name")
    List<Object[]> getGroupEventMappingStatisticsBySportsMeet(@Param("sportsMeetId") Long sportsMeetId);
    
    /**
     * 检查关联关系是否属于指定的组别
     * @param mappingId 关联关系ID
     * @param groupId 竞赛组别ID
     * @return 是否属于
     */
    @Query("SELECT COUNT(gem) > 0 FROM GroupEventMapping gem " +
           "WHERE gem.id = :mappingId AND gem.groupId = :groupId")
    boolean existsByIdAndGroupId(@Param("mappingId") Long mappingId, @Param("groupId") Long groupId);
}
