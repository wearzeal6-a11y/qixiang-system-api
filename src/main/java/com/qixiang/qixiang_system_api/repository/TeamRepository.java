package com.qixiang.qixiang_system_api.repository;

import com.qixiang.qixiang_system_api.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 参赛单位数据访问层
 * 提供参赛单位相关的数据库操作方法
 */
@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    
    /**
     * 根据运动会ID查找所有参赛单位
     * @param sportsMeetId 运动会ID
     * @return 参赛单位列表
     */
    List<Team> findBySportsMeetId(Long sportsMeetId);
    
    /**
     * 根据运动会ID和状态查找参赛单位
     * @param sportsMeetId 运动会ID
     * @param status 参赛状态
     * @return 参赛单位列表
     */
    List<Team> findBySportsMeetIdAndStatus(Long sportsMeetId, String status);
    
    /**
     * 根据运动会ID列表查找所有参赛单位
     * @param sportsMeetIds 运动会ID列表
     * @return 参赛单位列表
     */
    List<Team> findBySportsMeetIdIn(List<Long> sportsMeetIds);
    
    /**
     * 根据运动会ID列表和状态查找参赛单位
     * @param sportsMeetIds 运动会ID列表
     * @param status 参赛状态
     * @return 参赛单位列表
     */
    List<Team> findBySportsMeetIdInAndStatus(List<Long> sportsMeetIds, String status);
    
    /**
     * 根据参赛单位代码查找参赛单位
     * @param teamCode 参赛单位代码
     * @return 参赛单位
     */
    Team findByTeamCode(String teamCode);
    
    /**
     * 检查参赛单位代码是否存在
     * @param teamCode 参赛单位代码
     * @return 是否存在
     */
    boolean existsByTeamCode(String teamCode);
    
    /**
     * 根据参赛单位名称和运动会ID查找参赛单位
     * @param name 参赛单位名称
     * @param sportsMeetId 运动会ID
     * @return 参赛单位
     */
    Team findByNameAndSportsMeetId(String name, Long sportsMeetId);
    
    /**
     * 根据运动会ID统计参赛单位数量
     * @param sportsMeetId 运动会ID
     * @return 参赛单位数量
     */
    @Query("SELECT COUNT(t) FROM Team t WHERE t.sportsMeetId = :sportsMeetId")
    Long countBySportsMeetId(@Param("sportsMeetId") Long sportsMeetId);
    
    /**
     * 根据运动会ID和状态统计参赛单位数量
     * @param sportsMeetId 运动会ID
     * @param status 参赛状态
     * @return 参赛单位数量
     */
    @Query("SELECT COUNT(t) FROM Team t WHERE t.sportsMeetId = :sportsMeetId AND t.status = :status")
    Long countBySportsMeetIdAndStatus(@Param("sportsMeetId") Long sportsMeetId, 
                                      @Param("status") String status);
    
    /**
     * 根据运动会ID列表查找参赛单位的基本信息（仅ID和名称）
     * @param sportsMeetIds 运动会ID列表
     * @return 参赛单位基本信息列表
     */
    @Query("SELECT new com.qixiang.qixiang_system_api.dto.TeamSelectionDTO(t.id, t.name) " +
           "FROM Team t WHERE t.sportsMeetId IN :sportsMeetIds AND t.status = 'ACTIVE' " +
           "ORDER BY t.name")
    List<com.qixiang.qixiang_system_api.dto.TeamSelectionDTO> findTeamSelectionBySportsMeetIds(@Param("sportsMeetIds") List<Long> sportsMeetIds);
    
    /**
     * 根据运动会ID查找参赛单位的基本信息（仅ID和名称）
     * @param sportsMeetId 运动会ID
     * @return 参赛单位基本信息列表
     */
    @Query("SELECT new com.qixiang.qixiang_system_api.dto.TeamSelectionDTO(t.id, t.name) " +
           "FROM Team t WHERE t.sportsMeetId = :sportsMeetId AND t.status = 'ACTIVE' " +
           "ORDER BY t.name")
    List<com.qixiang.qixiang_system_api.dto.TeamSelectionDTO> findTeamSelectionBySportsMeetId(@Param("sportsMeetId") Long sportsMeetId);
    
    /**
     * 检查参赛单位名称在指定运动会中是否已存在
     * @param name 参赛单位名称
     * @param sportsMeetId 运动会ID
     * @param excludeId 排除的参赛单位ID（用于更新时检查）
     * @return 是否存在
     */
    @Query("SELECT COUNT(t) > 0 FROM Team t WHERE t.name = :name AND t.sportsMeetId = :sportsMeetId " +
           "AND (:excludeId IS NULL OR t.id != :excludeId)")
    boolean existsByNameAndSportsMeetId(@Param("name") String name, 
                                        @Param("sportsMeetId") Long sportsMeetId,
                                        @Param("excludeId") Long excludeId);
    
    /**
     * 检查参赛单位代码在指定运动会中是否已存在
     * @param teamCode 参赛单位代码
     * @param sportsMeetId 运动会ID
     * @param excludeId 排除的参赛单位ID（用于更新时检查）
     * @return 是否存在
     */
    @Query("SELECT COUNT(t) > 0 FROM Team t WHERE t.teamCode = :teamCode AND t.sportsMeetId = :sportsMeetId " +
           "AND (:excludeId IS NULL OR t.id != :excludeId)")
    boolean existsByTeamCodeAndSportsMeetId(@Param("teamCode") String teamCode, 
                                           @Param("sportsMeetId") Long sportsMeetId,
                                           @Param("excludeId") Long excludeId);
    
    /**
     * 根据机构代码和状态查询参赛单位
     * @param orgCode 机构代码
     * @param status 状态
     * @return 参赛单位列表
     */
    @Query("SELECT t FROM Team t WHERE t.orgCode = :orgCode AND t.status = :status")
    List<Team> findByOrgCodeAndStatus(@Param("orgCode") String orgCode, @Param("status") String status);
    
    /**
     * 根据机构代码查询参赛单位基本信息（仅ID和名称）
     * @param orgCode 机构代码
     * @return 参赛单位基本信息列表
     */
    @Query("SELECT new com.qixiang.qixiang_system_api.dto.TeamSelectionDTO(t.id, t.name) " +
           "FROM Team t WHERE t.orgCode = :orgCode AND t.status = 'ACTIVE' ORDER BY t.name")
    List<com.qixiang.qixiang_system_api.dto.TeamSelectionDTO> findTeamSelectionByOrgCode(@Param("orgCode") String orgCode);
    
    /**
     * 根据机构代码统计参赛单位数量
     * @param orgCode 机构代码
     * @return 参赛单位数量
     */
    @Query("SELECT COUNT(t) FROM Team t WHERE t.orgCode = :orgCode")
    Long countByOrgCode(@Param("orgCode") String orgCode);
    
    /**
     * 根据机构代码和状态统计参赛单位数量
     * @param orgCode 机构代码
     * @param status 状态
     * @return 参赛单位数量
     */
    @Query("SELECT COUNT(t) FROM Team t WHERE t.orgCode = :orgCode AND t.status = :status")
    Long countByOrgCodeAndStatus(@Param("orgCode") String orgCode, @Param("status") String status);
}
