package com.qixiang.qixiang_system_api.repository;

import com.qixiang.qixiang_system_api.entity.SportsMeet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 运动会数据访问层
 * 提供运动会相关的数据库操作方法
 */
@Repository
public interface SportsMeetRepository extends JpaRepository<SportsMeet, Long> {
    
    /**
     * 根据机构代码查找所有运动会
     * @param orgCode 机构代码
     * @return 运动会列表
     */
    List<SportsMeet> findByOrgCode(String orgCode);
    
    /**
     * 根据机构代码和状态查找运动会
     * @param orgCode 机构代码
     * @param status 运动会状态
     * @return 运动会列表
     */
    List<SportsMeet> findByOrgCodeAndStatus(String orgCode, String status);
    
    /**
     * 根据机构代码和状态列表查找运动会
     * @param orgCode 机构代码
     * @param statuses 运动会状态列表
     * @return 运动会列表
     */
    List<SportsMeet> findByOrgCodeAndStatusIn(String orgCode, List<String> statuses);
    
    /**
     * 根据运动会代码查找运动会
     * @param meetCode 运动会代码
     * @return 运动会
     */
    Optional<SportsMeet> findByMeetCode(String meetCode);
    
    /**
     * 根据机构代码和运动会代码查找运动会
     * @param orgCode 机构代码
     * @param meetCode 运动会代码
     * @return 运动会
     */
    Optional<SportsMeet> findByOrgCodeAndMeetCode(String orgCode, String meetCode);
    
    /**
     * 根据机构代码和运动会名称查找运动会
     * @param orgCode 机构代码
     * @param name 运动会名称
     * @return 运动会
     */
    Optional<SportsMeet> findByOrgCodeAndName(String orgCode, String name);
    
    /**
     * 根据机构代码查找所有运动会的ID列表
     * @param orgCode 机构代码
     * @return 运动会ID列表
     */
    @Query("SELECT s.id FROM SportsMeet s WHERE s.orgCode = :orgCode")
    List<Long> findIdsByOrgCode(@Param("orgCode") String orgCode);
    
    /**
     * 根据机构代码和状态查找所有运动会的ID列表
     * @param orgCode 机构代码
     * @param status 运动会状态
     * @return 运动会ID列表
     */
    @Query("SELECT s.id FROM SportsMeet s WHERE s.orgCode = :orgCode AND s.status = :status")
    List<Long> findIdsByOrgCodeAndStatus(@Param("orgCode") String orgCode, 
                                         @Param("status") String status);
    
    /**
     * 根据机构代码和状态列表查找所有运动会的ID列表
     * @param orgCode 机构代码
     * @param statuses 运动会状态列表
     * @return 运动会ID列表
     */
    @Query("SELECT s.id FROM SportsMeet s WHERE s.orgCode = :orgCode AND s.status IN :statuses")
    List<Long> findIdsByOrgCodeAndStatusIn(@Param("orgCode") String orgCode, 
                                           @Param("statuses") List<String> statuses);
    
    /**
     * 根据机构代码统计运动会数量
     * @param orgCode 机构代码
     * @return 运动会数量
     */
    @Query("SELECT COUNT(s) FROM SportsMeet s WHERE s.orgCode = :orgCode")
    Long countByOrgCode(@Param("orgCode") String orgCode);
    
    /**
     * 根据机构代码和状态统计运动会数量
     * @param orgCode 机构代码
     * @param status 运动会状态
     * @return 运动会数量
     */
    @Query("SELECT COUNT(s) FROM SportsMeet s WHERE s.orgCode = :orgCode AND s.status = :status")
    Long countByOrgCodeAndStatus(@Param("orgCode") String orgCode, 
                                 @Param("status") String status);
    
    /**
     * 查找指定时间范围内的运动会
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 运动会列表
     */
    List<SportsMeet> findByStartTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 查找正在报名期间的运动会
     * @param currentTime 当前时间
     * @return 运动会列表
     */
    @Query("SELECT s FROM SportsMeet s WHERE s.registrationStart <= :currentTime " +
           "AND s.registrationEnd >= :currentTime AND s.status = 'REGISTRATION'")
    List<SportsMeet> findInRegistrationPeriod(@Param("currentTime") LocalDateTime currentTime);
    
    /**
     * 查找正在进行中的运动会
     * @param currentTime 当前时间
     * @return 运动会列表
     */
    @Query("SELECT s FROM SportsMeet s WHERE s.startTime <= :currentTime " +
           "AND s.endTime >= :currentTime AND s.status = 'ONGOING'")
    List<SportsMeet> findOngoing(@Param("currentTime") LocalDateTime currentTime);
    
    /**
     * 检查运动会名称在指定机构中是否已存在
     * @param name 运动会名称
     * @param orgCode 机构代码
     * @param excludeId 排除的运动会ID（用于更新时检查）
     * @return 是否存在
     */
    @Query("SELECT COUNT(s) > 0 FROM SportsMeet s WHERE s.name = :name AND s.orgCode = :orgCode " +
           "AND (:excludeId IS NULL OR s.id != :excludeId)")
    boolean existsByNameAndOrgCode(@Param("name") String name, 
                                   @Param("orgCode") String orgCode,
                                   @Param("excludeId") Long excludeId);
    
    /**
     * 检查运动会代码在指定机构中是否已存在
     * @param meetCode 运动会代码
     * @param orgCode 机构代码
     * @param excludeId 排除的运动会ID（用于更新时检查）
     * @return 是否存在
     */
    @Query("SELECT COUNT(s) > 0 FROM SportsMeet s WHERE s.meetCode = :meetCode AND s.orgCode = :orgCode " +
           "AND (:excludeId IS NULL OR s.id != :excludeId)")
    boolean existsByMeetCodeAndOrgCode(@Param("meetCode") String meetCode, 
                                       @Param("orgCode") String orgCode,
                                       @Param("excludeId") Long excludeId);
    
    /**
     * 根据创建者查找运动会
     * @param createdBy 创建者ID
     * @return 运动会列表
     */
    List<SportsMeet> findByCreatedBy(Long createdBy);
    
    /**
     * 根据创建者和机构代码查找运动会
     * @param createdBy 创建者ID
     * @param orgCode 机构代码
     * @return 运动会列表
     */
    List<SportsMeet> findByCreatedByAndOrgCode(Long createdBy, String orgCode);
}
