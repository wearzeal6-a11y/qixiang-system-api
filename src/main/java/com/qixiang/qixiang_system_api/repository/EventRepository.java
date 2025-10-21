package com.qixiang.qixiang_system_api.repository;

import com.qixiang.qixiang_system_api.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 比赛项目数据访问接口
 * 提供比赛项目相关的数据库操作
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    /**
     * 根据项目类型查询比赛项目
     * @param eventType 项目类型
     * @return 比赛项目列表
     */
    List<Event> findByEventType(String eventType);
    
    /**
     * 根据项目名称查询比赛项目
     * @param name 项目名称
     * @return 比赛项目信息
     */
    Optional<Event> findByName(String name);
    
    /**
     * 根据项目名称模糊查询
     * @param name 项目名称关键词
     * @return 比赛项目列表
     */
    List<Event> findByNameContaining(String name);
    
    /**
     * 根据项目类型和名称模糊查询
     * @param eventType 项目类型
     * @param name 项目名称关键词
     * @return 比赛项目列表
     */
    @Query("SELECT e FROM Event e WHERE e.eventType = :eventType AND e.name LIKE %:name%")
    List<Event> findByEventTypeAndNameContaining(@Param("eventType") String eventType, 
                                                @Param("name") String name);
    
    /**
     * 查询所有个人项目
     * @return 个人项目列表
     */
    @Query("SELECT e FROM Event e WHERE e.eventType = 'INDIVIDUAL'")
    List<Event> findIndividualEvents();
    
    /**
     * 查询所有接力项目
     * @return 接力项目列表
     */
    @Query("SELECT e FROM Event e WHERE e.eventType = 'RELAY'")
    List<Event> findRelayEvents();
    
    /**
     * 查询所有团体项目
     * @return 团体项目列表
     */
    @Query("SELECT e FROM Event e WHERE e.eventType = 'TEAM'")
    List<Event> findTeamEvents();
    
    /**
     * 根据组别ID查询可参加的比赛项目
     * @param groupId 竞赛组别ID
     * @return 比赛项目列表
     */
    @Query("SELECT DISTINCT e FROM Event e " +
           "JOIN GroupEventMapping gem ON e.id = gem.eventId " +
           "WHERE gem.groupId = :groupId " +
           "ORDER BY e.name")
    List<Event> findEventsByGroupId(@Param("groupId") Long groupId);
    
    /**
     * 根据组别ID和项目类型查询可参加的比赛项目
     * @param groupId 竞赛组别ID
     * @param eventType 项目类型
     * @return 比赛项目列表
     */
    @Query("SELECT DISTINCT e FROM Event e " +
           "JOIN GroupEventMapping gem ON e.id = gem.eventId " +
           "WHERE gem.groupId = :groupId AND e.eventType = :eventType " +
           "ORDER BY e.name")
    List<Event> findEventsByGroupIdAndEventType(@Param("groupId") Long groupId, 
                                                @Param("eventType") String eventType);
    
    /**
     * 根据组别ID查询必报项目
     * @param groupId 竞赛组别ID
     * @return 必报项目列表
     */
    @Query("SELECT DISTINCT e FROM Event e " +
           "JOIN GroupEventMapping gem ON e.id = gem.eventId " +
           "WHERE gem.groupId = :groupId AND gem.isMandatory = true " +
           "ORDER BY e.name")
    List<Event> findMandatoryEventsByGroupId(@Param("groupId") Long groupId);
    
    /**
     * 根据组别ID查询选报项目
     * @param groupId 竞赛组别ID
     * @return 选报项目列表
     */
    @Query("SELECT DISTINCT e FROM Event e " +
           "JOIN GroupEventMapping gem ON e.id = gem.eventId " +
           "WHERE gem.groupId = :groupId AND gem.isMandatory = false " +
           "ORDER BY e.name")
    List<Event> findOptionalEventsByGroupId(@Param("groupId") Long groupId);
    
    /**
     * 统计不同类型项目的数量
     * @return 类型-数量映射
     */
    @Query("SELECT e.eventType as eventType, COUNT(e) as count " +
           "FROM Event e " +
           "GROUP BY e.eventType")
    List<Object[]> countEventsByType();
    
    /**
     * 检查项目名称是否已存在
     * @param name 项目名称
     * @param excludeId 排除的项目ID（用于更新时检查）
     * @return 是否存在
     */
    @Query("SELECT COUNT(e) > 0 FROM Event e " +
           "WHERE e.name = :name " +
           "AND (:excludeId IS NULL OR e.id != :excludeId)")
    boolean existsByName(@Param("name") String name, @Param("excludeId") Long excludeId);
    
    /**
     * 查询最近创建的比赛项目
     * @param limit 限制数量
     * @return 比赛项目列表
     */
    @Query("SELECT e FROM Event e ORDER BY e.createdAt DESC")
    List<Event> findRecentEvents(org.springframework.data.domain.Pageable pageable);
    
    /**
     * 根据项目类型统计项目数量
     * @param eventType 项目类型
     * @return 项目数量
     */
    @Query("SELECT COUNT(e) FROM Event e WHERE e.eventType = :eventType")
    int countByEventType(@Param("eventType") String eventType);
    
    /**
     * 查询所有项目的基本统计信息
     * @return 统计信息映射
     */
    @Query("SELECT " +
           "COUNT(e) as totalEvents, " +
           "COUNT(DISTINCT e.eventType) as typeCount, " +
           "MIN(e.createdAt) as firstEventTime, " +
           "MAX(e.createdAt) as lastEventTime " +
           "FROM Event e")
    Object[] getEventStatistics();
    
    /**
     * 根据多个组别ID查询可参加的比赛项目
     * @param groupIds 竞赛组别ID列表
     * @return 比赛项目列表
     */
    @Query("SELECT DISTINCT e FROM Event e " +
           "JOIN GroupEventMapping gem ON e.id = gem.eventId " +
           "WHERE gem.groupId IN :groupIds " +
           "ORDER BY e.name")
    List<Event> findEventsByGroupIds(@Param("groupIds") List<Long> groupIds);
    
    /**
     * 检查项目是否可以被指定组别参加
     * @param eventId 项目ID
     * @param groupId 组别ID
     * @return 是否可以参加
     */
    @Query("SELECT COUNT(gem) > 0 FROM GroupEventMapping gem " +
           "WHERE gem.eventId = :eventId AND gem.groupId = :groupId")
    boolean isEventAvailableForGroup(@Param("eventId") Long eventId, @Param("groupId") Long groupId);
    
    /**
     * 查询没有关联任何组别的项目
     * @return 项目列表
     */
    @Query("SELECT e FROM Event e " +
           "WHERE NOT EXISTS (SELECT 1 FROM GroupEventMapping gem WHERE gem.eventId = e.id)")
    List<Event> findUnassignedEvents();
}
