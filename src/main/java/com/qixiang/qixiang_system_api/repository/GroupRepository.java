package com.qixiang.qixiang_system_api.repository;

import com.qixiang.qixiang_system_api.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 竞赛组别数据访问接口
 * 提供竞赛组别相关的数据库操作
 */
@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    
    /**
     * 根据运动会ID查询所有组别
     * @param sportsMeetId 运动会ID
     * @return 组别列表
     */
    List<Group> findBySportsMeetId(Long sportsMeetId);
    
    /**
     * 根据运动会ID和状态查询组别
     * @param sportsMeetId 运动会ID
     * @param status 组别状态
     * @return 组别列表
     */
    List<Group> findBySportsMeetIdAndStatus(Long sportsMeetId, String status);
    
    /**
     * 根据性别查询组别
     * @param gender 性别
     * @return 组别列表
     */
    List<Group> findByGender(String gender);
    
    /**
     * 根据年级查询组别
     * @param grade 年级
     * @return 组别列表
     */
    List<Group> findByGrade(String grade);
    
    /**
     * 根据性别和年级查询组别
     * @param gender 性别
     * @param grade 年级
     * @return 组别列表
     */
    List<Group> findByGenderAndGrade(String gender, String grade);
    
    /**
     * 根据运动会ID和性别查询组别
     * @param sportsMeetId 运动会ID
     * @param gender 性别
     * @return 组别列表
     */
    List<Group> findBySportsMeetIdAndGender(Long sportsMeetId, String gender);
    
    /**
     * 根据运动会ID和年级查询组别
     * @param sportsMeetId 运动会ID
     * @param grade 年级
     * @return 组别列表
     */
    List<Group> findBySportsMeetIdAndGrade(Long sportsMeetId, String grade);
    
    /**
     * 根据运动会ID、性别和年级查询组别
     * @param sportsMeetId 运动会ID
     * @param gender 性别
     * @param grade 年级
     * @return 组别列表
     */
    List<Group> findBySportsMeetIdAndGenderAndGrade(Long sportsMeetId, String gender, String grade);
    
    /**
     * 根据组别名称查询组别
     * @param name 组别名称
     * @return 组别信息
     */
    Optional<Group> findByName(String name);
    
    /**
     * 根据组别名称模糊查询
     * @param name 组别名称关键词
     * @return 组别列表
     */
    List<Group> findByNameContaining(String name);
    
    /**
     * 根据运动会ID和组别名称查询组别
     * @param sportsMeetId 运动会ID
     * @param name 组别名称
     * @return 组别信息
     */
    Optional<Group> findBySportsMeetIdAndName(Long sportsMeetId, String name);
    
    /**
     * 检查组别名称是否已存在（在同一运动会内）
     * @param sportsMeetId 运动会ID
     * @param name 组别名称
     * @param excludeId 排除的组别ID（用于更新时检查）
     * @return 是否存在
     */
    @Query("SELECT COUNT(g) > 0 FROM Group g " +
           "WHERE g.sportsMeetId = :sportsMeetId AND g.name = :name " +
           "AND (:excludeId IS NULL OR g.id != :excludeId)")
    boolean existsByNameAndSportsMeetId(@Param("name") String name, 
                                       @Param("sportsMeetId") Long sportsMeetId, 
                                       @Param("excludeId") Long excludeId);
    
    /**
     * 统计运动会中的组别数量
     * @param sportsMeetId 运动会ID
     * @return 组别数量
     */
    @Query("SELECT COUNT(g) FROM Group g WHERE g.sportsMeetId = :sportsMeetId")
    int countBySportsMeetId(@Param("sportsMeetId") Long sportsMeetId);
    
    /**
     * 统计运动会中激活状态的组别数量
     * @param sportsMeetId 运动会ID
     * @return 激活组别数量
     */
    @Query("SELECT COUNT(g) FROM Group g " +
           "WHERE g.sportsMeetId = :sportsMeetId AND g.status = 'ACTIVE'")
    int countActiveGroupsBySportsMeetId(@Param("sportsMeetId") Long sportsMeetId);
    
    /**
     * 根据运动会ID按性别分组统计组别
     * @param sportsMeetId 运动会ID
     * @return 性别-数量映射
     */
    @Query("SELECT g.gender as gender, COUNT(g) as count " +
           "FROM Group g " +
           "WHERE g.sportsMeetId = :sportsMeetId " +
           "GROUP BY g.gender")
    List<Object[]> countGroupsByGender(@Param("sportsMeetId") Long sportsMeetId);
    
    /**
     * 根据运动会ID按年级分组统计组别
     * @param sportsMeetId 运动会ID
     * @return 年级-数量映射
     */
    @Query("SELECT g.grade as grade, COUNT(g) as count " +
           "FROM Group g " +
           "WHERE g.sportsMeetId = :sportsMeetId AND g.grade IS NOT NULL " +
           "GROUP BY g.grade")
    List<Object[]> countGroupsByGrade(@Param("sportsMeetId") Long sportsMeetId);
    
    /**
     * 查询运动会中所有不同的年级
     * @param sportsMeetId 运动会ID
     * @return 年级列表
     */
    @Query("SELECT DISTINCT g.grade FROM Group g " +
           "WHERE g.sportsMeetId = :sportsMeetId AND g.grade IS NOT NULL " +
           "ORDER BY g.grade")
    List<String> findDistinctGradesBySportsMeetId(@Param("sportsMeetId") Long sportsMeetId);
    
    /**
     * 查询运动会中所有不同的性别
     * @param sportsMeetId 运动会ID
     * @return 性别列表
     */
    @Query("SELECT DISTINCT g.gender FROM Group g " +
           "WHERE g.sportsMeetId = :sportsMeetId AND g.gender IS NOT NULL " +
           "ORDER BY g.gender")
    List<String> findDistinctGendersBySportsMeetId(@Param("sportsMeetId") Long sportsMeetId);
    
    /**
     * 根据运动会ID查询组别的规则统计信息
     * @param sportsMeetId 运动会ID
     * @return 规则统计信息映射
     */
    @Query("SELECT " +
           "g.name as groupName, " +
           "g.maxLeadersPerTeam as maxLeaders, " +
           "g.maxAthletesPerTeam as maxAthletes, " +
           "g.maxEventsPerAthlete as maxEventsPerAthlete, " +
           "g.maxParticipantsPerEvent as maxParticipants, " +
           "g.maxRelaysPerTeam as maxRelays, " +
           "g.allowMixedEvents as allowMixed " +
           "FROM Group g " +
           "WHERE g.sportsMeetId = :sportsMeetId AND g.status = 'ACTIVE' " +
           "ORDER BY g.name")
    List<Object[]> getGroupRulesBySportsMeetId(@Param("sportsMeetId") Long sportsMeetId);
    
    /**
     * 根据运动会ID计算总的名额限制
     * @param sportsMeetId 运动会ID
     * @return 总额限制映射
     */
    @Query("SELECT " +
           "SUM(g.maxLeadersPerTeam) as totalMaxLeaders, " +
           "SUM(g.maxAthletesPerTeam) as totalMaxAthletes " +
           "FROM Group g " +
           "WHERE g.sportsMeetId = :sportsMeetId AND g.status = 'ACTIVE'")
    Object[] getTotalLimitsBySportsMeetId(@Param("sportsMeetId") Long sportsMeetId);
    
    /**
     * 查询最近创建的组别
     * @param sportsMeetId 运动会ID
     * @param limit 限制数量
     * @return 组别列表
     */
    @Query("SELECT g FROM Group g WHERE g.sportsMeetId = :sportsMeetId ORDER BY g.createdAt DESC")
    List<Group> findRecentGroupsBySportsMeetId(@Param("sportsMeetId") Long sportsMeetId,
                                              org.springframework.data.domain.Pageable pageable);
    
    /**
     * 批量删除运动会的所有组别
     * @param sportsMeetId 运动会ID
     * @return 删除的记录数
     */
    @Query("DELETE FROM Group g WHERE g.sportsMeetId = :sportsMeetId")
    int deleteBySportsMeetId(@Param("sportsMeetId") Long sportsMeetId);
    
    /**
     * 批量更新运动会中组别的状态
     * @param sportsMeetId 运动会ID
     * @param status 新状态
     * @return 更新的记录数
     */
    @Query("UPDATE Group g SET g.status = :status WHERE g.sportsMeetId = :sportsMeetId")
    int updateStatusBySportsMeetId(@Param("sportsMeetId") Long sportsMeetId, 
                                   @Param("status") String status);
    
    /**
     * 检查组别是否属于指定的运动会
     * @param groupId 组别ID
     * @param sportsMeetId 运动会ID
     * @return 是否属于
     */
    @Query("SELECT COUNT(g) > 0 FROM Group g WHERE g.id = :groupId AND g.sportsMeetId = :sportsMeetId")
    boolean existsByIdAndSportsMeetId(@Param("groupId") Long groupId, 
                                     @Param("sportsMeetId") Long sportsMeetId);
    
    /**
     * 查询具有混合项目权限的组别
     * @param sportsMeetId 运动会ID
     * @return 组别列表
     */
    @Query("SELECT g FROM Group g " +
           "WHERE g.sportsMeetId = :sportsMeetId AND g.allowMixedEvents = true AND g.status = 'ACTIVE'")
    List<Group> findMixedEventGroupsBySportsMeetId(@Param("sportsMeetId") Long sportsMeetId);
    
    /**
     * 根据状态查询组别
     * @param status 组别状态
     * @return 组别列表
     */
    List<Group> findByStatus(String status);
    
    /**
     * 根据性别和状态查询组别
     * @param gender 性别
     * @param status 组别状态
     * @return 组别列表
     */
    List<Group> findByGenderAndStatus(String gender, String status);
    
    /**
     * 根据年级和状态查询组别
     * @param grade 年级
     * @param status 组别状态
     * @return 组别列表
     */
    List<Group> findByGradeAndStatus(String grade, String status);
}
