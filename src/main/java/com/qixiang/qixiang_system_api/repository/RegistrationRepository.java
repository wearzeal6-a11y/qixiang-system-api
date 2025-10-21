package com.qixiang.qixiang_system_api.repository;

import com.qixiang.qixiang_system_api.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 报名记录数据访问接口
 * 提供报名记录相关的数据库操作
 */
@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    
    /**
     * 根据参赛单位ID统计报名记录数量
     * @param teamId 参赛单位ID
     * @return 报名记录数量
     */
    @Query("SELECT COUNT(r) FROM Registration r WHERE r.teamId = :teamId")
    int countByTeamId(@Param("teamId") Long teamId);
    
    /**
     * 根据参赛单位ID和组别ID统计报名记录数量
     * @param teamId 参赛单位ID
     * @param groupId 竞赛组别ID
     * @return 报名记录数量
     */
    @Query("SELECT COUNT(r) FROM Registration r WHERE r.teamId = :teamId AND r.groupId = :groupId")
    int countByTeamAndGroup(@Param("teamId") Long teamId, @Param("groupId") Long groupId);
    
    /**
     * 根据参赛单位ID、组别ID和项目ID统计报名记录数量
     * @param teamId 参赛单位ID
     * @param groupId 竞赛组别ID
     * @param eventId 比赛项目ID
     * @return 报名记录数量
     */
    @Query("SELECT COUNT(r) FROM Registration r " +
           "WHERE r.teamId = :teamId AND r.groupId = :groupId AND r.eventId = :eventId")
    int countByTeamGroupAndEvent(@Param("teamId") Long teamId, 
                                @Param("groupId") Long groupId, 
                                @Param("eventId") Long eventId);
    
    /**
     * 根据参赛单位ID和运动员ID统计报名记录数量
     * @param teamId 参赛单位ID
     * @param athleteId 运动员ID
     * @return 报名记录数量
     */
    @Query("SELECT COUNT(r) FROM Registration r WHERE r.teamId = :teamId AND r.athleteId = :athleteId")
    int countByTeamAndAthlete(@Param("teamId") Long teamId, @Param("athleteId") Long athleteId);
    
    /**
     * 根据参赛单位ID和状态统计报名记录数量
     * @param teamId 参赛单位ID
     * @param status 报名状态
     * @return 报名记录数量
     */
    @Query("SELECT COUNT(r) FROM Registration r WHERE r.teamId = :teamId AND r.status = :status")
    int countByTeamIdAndStatus(@Param("teamId") Long teamId, @Param("status") String status);
    
    /**
     * 统计参赛单位的领队教练数量（通过特殊标记的报名记录）
     * @param teamId 参赛单位ID
     * @return 领队教练数量
     */
    @Query("SELECT COUNT(r) FROM Registration r " +
           "WHERE r.teamId = :teamId AND r.athleteId IS NULL")
    int countLeadersByTeam(@Param("teamId") Long teamId);
    
    /**
     * 统计参赛单位指定组别的领队教练数量
     * @param teamId 参赛单位ID
     * @param groupId 竞赛组别ID
     * @return 领队教练数量
     */
    @Query("SELECT COUNT(r) FROM Registration r " +
           "WHERE r.teamId = :teamId AND r.groupId = :groupId AND r.athleteId IS NULL")
    int countLeadersByTeamAndGroup(@Param("teamId") Long teamId, @Param("groupId") Long groupId);
    
    /**
     * 统计参赛单位所有领队教练数量
     * @param teamId 参赛单位ID
     * @return 领队教练数量
     */
    @Query("SELECT COUNT(r) FROM Registration r " +
           "WHERE r.teamId = :teamId AND r.athleteId IS NULL AND r.status = 'CONFIRMED'")
    int countAllLeadersByTeam(@Param("teamId") Long teamId);
    
    /**
     * 根据参赛单位ID查询所有报名记录
     * @param teamId 参赛单位ID
     * @return 报名记录列表
     */
    List<Registration> findByTeamId(Long teamId);
    
    /**
     * 根据参赛单位ID和状态查询报名记录
     * @param teamId 参赛单位ID
     * @param status 报名状态
     * @return 报名记录列表
     */
    List<Registration> findByTeamIdAndStatus(Long teamId, String status);
    
    /**
     * 根据运动员ID查询报名记录
     * @param athleteId 运动员ID
     * @return 报名记录列表
     */
    List<Registration> findByAthleteId(Long athleteId);
    
    /**
     * 根据组别ID查询报名记录
     * @param groupId 竞赛组别ID
     * @return 报名记录列表
     */
    List<Registration> findByGroupId(Long groupId);
    
    /**
     * 根据项目ID查询报名记录
     * @param eventId 比赛项目ID
     * @return 报名记录列表
     */
    List<Registration> findByEventId(Long eventId);
    
    /**
     * 根据项目ID统计报名记录数量
     * @param eventId 比赛项目ID
     * @return 报名记录数量
     */
    @Query("SELECT COUNT(r) FROM Registration r WHERE r.eventId = :eventId AND r.status = 'CONFIRMED'")
    int countByEventId(@Param("eventId") Long eventId);
    
    /**
     * 根据参赛单位ID和组别ID查询报名记录
     * @param teamId 参赛单位ID
     * @param groupId 竞赛组别ID
     * @return 报名记录列表
     */
    List<Registration> findByTeamIdAndGroupId(Long teamId, Long groupId);
    
    /**
     * 检查运动员是否已经报名了指定项目
     * @param teamId 参赛单位ID
     * @param athleteId 运动员ID
     * @param groupId 竞赛组别ID
     * @param eventId 比赛项目ID
     * @return 是否已报名
     */
    @Query("SELECT COUNT(r) > 0 FROM Registration r " +
           "WHERE r.teamId = :teamId AND r.athleteId = :athleteId " +
           "AND r.groupId = :groupId AND r.eventId = :eventId " +
           "AND r.status = 'CONFIRMED'")
    boolean existsByTeamAthleteGroupAndEvent(@Param("teamId") Long teamId,
                                           @Param("athleteId") Long athleteId,
                                           @Param("groupId") Long groupId,
                                           @Param("eventId") Long eventId);
    
    /**
     * 根据参赛单位ID获取报名数据汇总统计
     * @param teamId 参赛单位ID
     * @return 统计信息映射
     */
    @Query("SELECT " +
           "COUNT(r) as totalRegistrations, " +
           "COUNT(DISTINCT r.groupId) as groupCount, " +
           "COUNT(DISTINCT r.athleteId) as athleteCount, " +
           "COUNT(DISTINCT r.eventId) as eventCount, " +
           "MIN(r.registrationTime) as firstRegistrationTime, " +
           "MAX(r.registrationTime) as lastRegistrationTime " +
           "FROM Registration r " +
           "WHERE r.teamId = :teamId AND r.status = 'CONFIRMED'")
    Map<String, Object> getRegistrationStatisticsByTeamId(@Param("teamId") Long teamId);
    
    /**
     * 根据参赛单位ID和组别ID获取详细统计信息
     * @param teamId 参赛单位ID
     * @param groupId 竞赛组别ID
     * @return 统计信息映射
     */
    @Query("SELECT " +
           "COUNT(r) as registrationCount, " +
           "COUNT(DISTINCT r.athleteId) as athleteCount, " +
           "COUNT(DISTINCT r.eventId) as eventCount, " +
           "MIN(r.registrationTime) as firstRegistrationTime, " +
           "MAX(r.registrationTime) as lastRegistrationTime " +
           "FROM Registration r " +
           "WHERE r.teamId = :teamId AND r.groupId = :groupId AND r.status = 'CONFIRMED'")
    Map<String, Object> getRegistrationStatisticsByTeamAndGroup(@Param("teamId") Long teamId,
                                                                @Param("groupId") Long groupId);
    
    /**
     * 根据参赛单位ID按组别分组统计报名情况
     * @param teamId 参赛单位ID
     * @return 组别-统计信息映射
     */
    @Query("SELECT " +
           "g.id as groupId, " +
           "g.name as groupName, " +
           "COUNT(r) as registrationCount, " +
           "COUNT(DISTINCT r.athleteId) as athleteCount, " +
           "COUNT(DISTINCT r.eventId) as eventCount " +
           "FROM Registration r " +
           "JOIN Group g ON r.groupId = g.id " +
           "WHERE r.teamId = :teamId AND r.status = 'CONFIRMED' " +
           "GROUP BY g.id, g.name " +
           "ORDER BY g.name")
    List<Map<String, Object>> getRegistrationStatisticsByGroup(@Param("teamId") Long teamId);
    
    /**
     * 根据参赛单位ID和组别ID按项目分组统计报名情况
     * @param teamId 参赛单位ID
     * @param groupId 竞赛组别ID
     * @return 项目-统计信息映射
     */
    @Query("SELECT " +
           "e.id as eventId, " +
           "e.name as eventName, " +
           "e.eventType as eventType, " +
           "COUNT(r) as registrationCount, " +
           "COUNT(DISTINCT r.athleteId) as athleteCount " +
           "FROM Registration r " +
           "JOIN Event e ON r.eventId = e.id " +
           "WHERE r.teamId = :teamId AND r.groupId = :groupId AND r.status = 'CONFIRMED' " +
           "GROUP BY e.id, e.name, e.eventType " +
           "ORDER BY e.name")
    List<Map<String, Object>> getRegistrationStatisticsByEvent(@Param("teamId") Long teamId,
                                                               @Param("groupId") Long groupId);
    
    /**
     * 查询指定时间范围内的报名记录
     * @param teamId 参赛单位ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 报名记录列表
     */
    @Query("SELECT r FROM Registration r " +
           "WHERE r.teamId = :teamId AND r.registrationTime BETWEEN :startTime AND :endTime " +
           "ORDER BY r.registrationTime DESC")
    List<Registration> findByTeamIdAndRegistrationTimeBetween(@Param("teamId") Long teamId,
                                                              @Param("startTime") LocalDateTime startTime,
                                                              @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询最近的报名记录
     * @param teamId 参赛单位ID
     * @param limit 限制数量
     * @return 报名记录列表
     */
    @Query("SELECT r FROM Registration r WHERE r.teamId = :teamId ORDER BY r.registrationTime DESC")
    List<Registration> findRecentRegistrationsByTeamId(@Param("teamId") Long teamId,
                                                      org.springframework.data.domain.Pageable pageable);
    
    /**
     * 批量删除参赛单位的报名记录
     * @param teamId 参赛单位ID
     * @return 删除的记录数
     */
    @Query("DELETE FROM Registration r WHERE r.teamId = :teamId")
    int deleteByTeamId(@Param("teamId") Long teamId);
    
    /**
     * 批量删除指定状态的报名记录
     * @param teamId 参赛单位ID
     * @param status 报名状态
     * @return 删除的记录数
     */
    @Query("DELETE FROM Registration r WHERE r.teamId = :teamId AND r.status = :status")
    int deleteByTeamIdAndStatus(@Param("teamId") Long teamId, @Param("status") String status);
    
    /**
     * 检查报名记录是否属于指定的参赛单位
     * @param registrationId 报名记录ID
     * @param teamId 参赛单位ID
     * @return 是否属于
     */
    @Query("SELECT COUNT(r) > 0 FROM Registration r " +
           "WHERE r.id = :registrationId AND r.teamId = :teamId")
    boolean existsByIdAndTeamId(@Param("registrationId") Long registrationId, @Param("teamId") Long teamId);
    
    /**
     * 根据参赛单位ID获取报名项目的类型分布统计
     * @param teamId 参赛单位ID
     * @return 项目类型-数量映射
     */
    @Query("SELECT " +
           "e.eventType as eventType, " +
           "COUNT(r) as registrationCount, " +
           "COUNT(DISTINCT r.athleteId) as athleteCount " +
           "FROM Registration r " +
           "JOIN Event e ON r.eventId = e.id " +
           "WHERE r.teamId = :teamId AND r.status = 'CONFIRMED' " +
           "GROUP BY e.eventType")
    List<Map<String, Object>> getEventTypeStatisticsByTeamId(@Param("teamId") Long teamId);
}
