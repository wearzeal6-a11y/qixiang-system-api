package com.qixiang.qixiang_system_api.repository;

import com.qixiang.qixiang_system_api.entity.Athlete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 运动员数据访问接口
 * 提供运动员相关的数据库操作
 */
@Repository
public interface AthleteRepository extends JpaRepository<Athlete, Long> {
    
    /**
     * 根据参赛单位ID统计运动员数量
     * @param teamId 参赛单位ID
     * @return 运动员数量
     */
    @Query("SELECT COUNT(a) FROM Athlete a WHERE a.teamId = :teamId")
    int countByTeamId(@Param("teamId") Long teamId);
    
    /**
     * 根据参赛单位ID和组别ID统计运动员数量
     * @param teamId 参赛单位ID
     * @param groupId 竞赛组别ID
     * @return 运动员数量
     */
    @Query("SELECT COUNT(a) FROM Athlete a WHERE a.teamId = :teamId AND a.groupId = :groupId")
    int countByTeamAndGroup(@Param("teamId") Long teamId, @Param("groupId") Long groupId);
    
    /**
     * 根据参赛单位ID和性别统计运动员数量
     * @param teamId 参赛单位ID
     * @param gender 性别
     * @return 运动员数量
     */
    @Query("SELECT COUNT(a) FROM Athlete a " +
           "JOIN Group g ON a.groupId = g.id " +
           "WHERE a.teamId = :teamId AND g.gender = :gender")
    int countByTeamIdAndGender(@Param("teamId") Long teamId, @Param("gender") String gender);
    
    /**
     * 根据参赛单位ID按年级分组统计运动员数量
     * @param teamId 参赛单位ID
     * @return 年级-数量映射
     */
    @Query("SELECT g.grade as grade, COUNT(a) as count " +
           "FROM Athlete a " +
           "JOIN Group g ON a.groupId = g.id " +
           "WHERE a.teamId = :teamId AND g.grade IS NOT NULL " +
           "GROUP BY g.grade")
    List<Map<String, Object>> countByTeamIdAndGrade(@Param("teamId") Long teamId);
    
    /**
     * 根据参赛单位ID查询所有运动员
     * @param teamId 参赛单位ID
     * @return 运动员列表
     */
    List<Athlete> findByTeamId(Long teamId);
    
    /**
     * 根据参赛单位ID和组别ID查询运动员
     * @param teamId 参赛单位ID
     * @param groupId 竞赛组别ID
     * @return 运动员列表
     */
    List<Athlete> findByTeamIdAndGroupId(Long teamId, Long groupId);
    
    /**
     * 根据组别ID查询运动员
     * @param groupId 竞赛组别ID
     * @return 运动员列表
     */
    List<Athlete> findByGroupId(Long groupId);
    
    /**
     * 根据姓名模糊查询运动员（限定参赛单位）
     * @param teamId 参赛单位ID
     * @param name 姓名关键词
     * @return 运动员列表
     */
    @Query("SELECT a FROM Athlete a WHERE a.teamId = :teamId AND a.name LIKE %:name%")
    List<Athlete> findByTeamIdAndNameContaining(@Param("teamId") Long teamId, @Param("name") String name);
    
    /**
     * 根据身份证号查询运动员
     * @param idNumber 身份证号
     * @return 运动员信息
     */
    Optional<Athlete> findByIdNumber(String idNumber);
    
    /**
     * 根据参赛单位ID和身份证号查询运动员
     * @param teamId 参赛单位ID
     * @param idNumber 身份证号
     * @return 运动员信息
     */
    Optional<Athlete> findByTeamIdAndIdNumber(Long teamId, String idNumber);
    
    /**
     * 检查同一参赛单位内是否存在重名运动员
     * @param teamId 参赛单位ID
     * @param name 运动员姓名
     * @param excludeId 排除的运动员ID（用于更新时检查）
     * @return 是否存在重名
     */
    @Query("SELECT COUNT(a) > 0 FROM Athlete a " +
           "WHERE a.teamId = :teamId AND a.name = :name " +
           "AND (:excludeId IS NULL OR a.id != :excludeId)")
    boolean existsByNameAndTeamId(@Param("name") String name, 
                                  @Param("teamId") Long teamId, 
                                  @Param("excludeId") Long excludeId);
    
    /**
     * 根据参赛单位ID查询运动员的基本统计信息
     * @param teamId 参赛单位ID
     * @return 统计信息映射
     */
    @Query("SELECT " +
           "COUNT(a) as totalAthletes, " +
           "COUNT(DISTINCT a.groupId) as groupCount, " +
           "MIN(a.createdAt) as firstRegistrationTime, " +
           "MAX(a.createdAt) as lastRegistrationTime " +
           "FROM Athlete a WHERE a.teamId = :teamId")
    Map<String, Object> getAthleteStatisticsByTeamId(@Param("teamId") Long teamId);
    
    /**
     * 根据参赛单位ID和组别ID查询运动员的详细统计信息
     * @param teamId 参赛单位ID
     * @param groupId 竞赛组别ID
     * @return 统计信息映射
     */
    @Query("SELECT " +
           "COUNT(a) as athleteCount, " +
           "MIN(a.createdAt) as firstRegistrationTime, " +
           "MAX(a.createdAt) as lastRegistrationTime " +
           "FROM Athlete a " +
           "WHERE a.teamId = :teamId AND a.groupId = :groupId")
    Map<String, Object> getAthleteStatisticsByTeamAndGroup(@Param("teamId") Long teamId, 
                                                          @Param("groupId") Long groupId);
    
    /**
     * 查询最近注册的运动员
     * @param teamId 参赛单位ID
     * @param limit 限制数量
     * @return 运动员列表
     */
    @Query("SELECT a FROM Athlete a WHERE a.teamId = :teamId ORDER BY a.createdAt DESC")
    List<Athlete> findRecentAthletesByTeamId(@Param("teamId") Long teamId, 
                                            org.springframework.data.domain.Pageable pageable);
    
    /**
     * 批量删除参赛单位的运动员
     * @param teamId 参赛单位ID
     * @return 删除的记录数
     */
    @Query("DELETE FROM Athlete a WHERE a.teamId = :teamId")
    int deleteByTeamId(@Param("teamId") Long teamId);
    
    /**
     * 检查运动员是否属于指定的参赛单位
     * @param athleteId 运动员ID
     * @param teamId 参赛单位ID
     * @return 是否属于
     */
    @Query("SELECT COUNT(a) > 0 FROM Athlete a WHERE a.id = :athleteId AND a.teamId = :teamId")
    boolean existsByIdAndTeamId(@Param("athleteId") Long athleteId, @Param("teamId") Long teamId);
}
