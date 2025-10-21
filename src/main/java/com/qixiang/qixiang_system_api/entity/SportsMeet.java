package com.qixiang.qixiang_system_api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 运动会实体类
 * 代表田径运动会的基本信息
 */
@Entity
@Table(name = "sports_meets")
public class SportsMeet {
    
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 运动会名称
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    /**
     * 机构代码 - 用于关联不同的组织机构
     */
    @Column(name = "org_code", nullable = false, length = 50)
    private String orgCode;
    
    /**
     * 运动会代码
     */
    @Column(name = "meet_code", length = 50)
    private String meetCode;
    
    /**
     * 运动会描述
     */
    @Column(name = "description", length = 500)
    private String description;
    
    /**
     * 运动会地点
     */
    @Column(name = "location", length = 200)
    private String location;
    
    /**
     * 开始时间
     */
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    /**
     * 报名开始时间
     */
    @Column(name = "registration_start")
    private LocalDateTime registrationStart;
    
    /**
     * 报名结束时间
     */
    @Column(name = "registration_end")
    private LocalDateTime registrationEnd;
    
    /**
     * 运动会状态：PLANNING-规划中，REGISTRATION-报名中，ONGOING-进行中，COMPLETED-已完成，CANCELLED-已取消
     */
    @Column(name = "status", length = 20)
    private String status = "PLANNING";
    
    /**
     * 最大参赛队伍数量
     */
    @Column(name = "max_teams")
    private Integer maxTeams;
    
    /**
     * 当前参赛队伍数量
     */
    @Column(name = "current_teams")
    private Integer currentTeams = 0;
    
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
     * 创建者ID
     */
    @Column(name = "created_by")
    private Long createdBy;
    
    /**
     * 备注信息
     */
    @Column(name = "remarks", length = 1000)
    private String remarks;
    
    /**
     * 默认构造函数
     */
    public SportsMeet() {
    }
    
    /**
     * 构造函数
     * @param name 运动会名称
     * @param orgCode 机构代码
     */
    public SportsMeet(String name, String orgCode) {
        this.name = name;
        this.orgCode = orgCode;
        this.status = "PLANNING";
    }
    
    /**
     * 构造函数
     * @param name 运动会名称
     * @param orgCode 机构代码
     * @param meetCode 运动会代码
     */
    public SportsMeet(String name, String orgCode, String meetCode) {
        this.name = name;
        this.orgCode = orgCode;
        this.meetCode = meetCode;
        this.status = "PLANNING";
    }
    
    @PrePersist
    protected void onCreate() {
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
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getOrgCode() {
        return orgCode;
    }
    
    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }
    
    public String getMeetCode() {
        return meetCode;
    }
    
    public void setMeetCode(String meetCode) {
        this.meetCode = meetCode;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public LocalDateTime getRegistrationStart() {
        return registrationStart;
    }
    
    public void setRegistrationStart(LocalDateTime registrationStart) {
        this.registrationStart = registrationStart;
    }
    
    public LocalDateTime getRegistrationEnd() {
        return registrationEnd;
    }
    
    public void setRegistrationEnd(LocalDateTime registrationEnd) {
        this.registrationEnd = registrationEnd;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Integer getMaxTeams() {
        return maxTeams;
    }
    
    public void setMaxTeams(Integer maxTeams) {
        this.maxTeams = maxTeams;
    }
    
    public Integer getCurrentTeams() {
        return currentTeams;
    }
    
    public void setCurrentTeams(Integer currentTeams) {
        this.currentTeams = currentTeams;
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
    
    public Long getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
    
    public String getRemarks() {
        return remarks;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    @Override
    public String toString() {
        return "SportsMeet{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", orgCode='" + orgCode + '\'' +
                ", meetCode='" + meetCode + '\'' +
                ", status='" + status + '\'' +
                ", location='" + location + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", currentTeams=" + currentTeams +
                ", maxTeams=" + maxTeams +
                ", createdAt=" + createdAt +
                '}';
    }
}
