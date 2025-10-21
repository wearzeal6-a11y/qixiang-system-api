package com.qixiang.qixiang_system_api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 报名记录实体类
 * 代表参赛单位为运动员报名参加具体项目的记录
 */
@Entity
@Table(name = "registrations")
public class Registration {
    
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 参赛单位ID
     */
    @Column(name = "team_id", nullable = false)
    private Long teamId;
    
    /**
     * 运动员ID
     * 个人项目必填，接力项目可为空
     */
    @Column(name = "athlete_id")
    private Long athleteId;
    
    /**
     * 竞赛组别ID
     */
    @Column(name = "group_id", nullable = false)
    private Long groupId;
    
    /**
     * 比赛项目ID
     */
    @Column(name = "event_id", nullable = false)
    private Long eventId;
    
    /**
     * 报名时间
     */
    @Column(name = "registration_time")
    private LocalDateTime registrationTime;
    
    /**
     * 报名状态
     * CONFIRMED-已确认，CANCELLED-已取消，PENDING-待确认
     */
    @Column(name = "status", length = 20)
    private String status = "CONFIRMED";
    
    /**
     * 创建时间
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * 报名状态枚举
     */
    public enum RegistrationStatus {
        CONFIRMED("CONFIRMED", "已确认"),
        CANCELLED("CANCELLED", "已取消"),
        PENDING("PENDING", "待确认");
        
        private final String code;
        private final String description;
        
        RegistrationStatus(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
        
        public static RegistrationStatus fromCode(String code) {
            for (RegistrationStatus status : values()) {
                if (status.code.equals(code)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown registration status code: " + code);
        }
    }
    
    /**
     * 默认构造函数
     */
    public Registration() {
        this.registrationTime = LocalDateTime.now();
        this.status = RegistrationStatus.CONFIRMED.getCode();
    }
    
    /**
     * 构造函数
     * @param teamId 参赛单位ID
     * @param athleteId 运动员ID
     * @param groupId 竞赛组别ID
     * @param eventId 比赛项目ID
     */
    public Registration(Long teamId, Long athleteId, Long groupId, Long eventId) {
        this();
        this.teamId = teamId;
        this.athleteId = athleteId;
        this.groupId = groupId;
        this.eventId = eventId;
    }
    
    /**
     * 构造函数（接力项目，可能没有具体运动员）
     * @param teamId 参赛单位ID
     * @param groupId 竞赛组别ID
     * @param eventId 比赛项目ID
     * @param status 报名状态
     */
    public Registration(Long teamId, Long groupId, Long eventId, String status) {
        this();
        this.teamId = teamId;
        this.groupId = groupId;
        this.eventId = eventId;
        this.status = status;
    }
    
    /**
     * 构造函数（完整参数）
     * @param teamId 参赛单位ID
     * @param athleteId 运动员ID
     * @param groupId 竞赛组别ID
     * @param eventId 比赛项目ID
     * @param registrationTime 报名时间
     * @param status 报名状态
     */
    public Registration(Long teamId, Long athleteId, Long groupId, Long eventId, 
                       LocalDateTime registrationTime, String status) {
        this.teamId = teamId;
        this.athleteId = athleteId;
        this.groupId = groupId;
        this.eventId = eventId;
        this.registrationTime = registrationTime;
        this.status = status;
    }
    
    @PrePersist
    protected void onCreate() {
        if (registrationTime == null) {
            registrationTime = LocalDateTime.now();
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getter and Setter methods
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getTeamId() {
        return teamId;
    }
    
    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }
    
    public Long getAthleteId() {
        return athleteId;
    }
    
    public void setAthleteId(Long athleteId) {
        this.athleteId = athleteId;
    }
    
    public Long getGroupId() {
        return groupId;
    }
    
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
    
    public Long getEventId() {
        return eventId;
    }
    
    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
    
    public LocalDateTime getRegistrationTime() {
        return registrationTime;
    }
    
    public void setRegistrationTime(LocalDateTime registrationTime) {
        this.registrationTime = registrationTime;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    /**
     * 获取报名状态枚举
     * @return 报名状态枚举
     */
    public RegistrationStatus getRegistrationStatusEnum() {
        return RegistrationStatus.fromCode(status);
    }
    
    /**
     * 设置报名状态（使用枚举）
     * @param status 报名状态枚举
     */
    public void setRegistrationStatusEnum(RegistrationStatus status) {
        this.status = status.getCode();
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * 判断是否为已确认状态
     * @return true如果是已确认状态
     */
    public boolean isConfirmed() {
        return RegistrationStatus.CONFIRMED.getCode().equals(status);
    }
    
    /**
     * 判断是否为已取消状态
     * @return true如果是已取消状态
     */
    public boolean isCancelled() {
        return RegistrationStatus.CANCELLED.getCode().equals(status);
    }
    
    /**
     * 判断是否为待确认状态
     * @return true如果是待确认状态
     */
    public boolean isPending() {
        return RegistrationStatus.PENDING.getCode().equals(status);
    }
    
    /**
     * 确认报名
     */
    public void confirm() {
        this.status = RegistrationStatus.CONFIRMED.getCode();
    }
    
    /**
     * 取消报名
     */
    public void cancel() {
        this.status = RegistrationStatus.CANCELLED.getCode();
    }
    
    /**
     * 设为待确认
     */
    public void setPending() {
        this.status = RegistrationStatus.PENDING.getCode();
    }
    
    @Override
    public String toString() {
        return "Registration{" +
                "id=" + id +
                ", teamId=" + teamId +
                ", athleteId=" + athleteId +
                ", groupId=" + groupId +
                ", eventId=" + eventId +
                ", registrationTime=" + registrationTime +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Registration that = (Registration) o;
        
        return id != null ? id.equals(that.id) : that.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
