package com.qixiang.qixiang_system_api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 运动员实体类
 * 代表参加运动会的运动员信息
 */
@Entity
@Table(name = "athletes")
public class Athlete {
    
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
     * 竞赛组别ID
     */
    @Column(name = "group_id", nullable = false)
    private Long groupId;
    
    /**
     * 运动员姓名
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    /**
     * 身份证号
     */
    @Column(name = "id_number", length = 20)
    private String idNumber;
    
    /**
     * 联系电话
     */
    @Column(name = "contact_phone", length = 20)
    private String contactPhone;
    
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
     * 默认构造函数
     */
    public Athlete() {
    }
    
    /**
     * 构造函数
     * @param teamId 参赛单位ID
     * @param groupId 竞赛组别ID
     * @param name 运动员姓名
     */
    public Athlete(Long teamId, Long groupId, String name) {
        this.teamId = teamId;
        this.groupId = groupId;
        this.name = name;
    }
    
    /**
     * 构造函数
     * @param teamId 参赛单位ID
     * @param groupId 竞赛组别ID
     * @param name 运动员姓名
     * @param idNumber 身份证号
     * @param contactPhone 联系电话
     */
    public Athlete(Long teamId, Long groupId, String name, String idNumber, String contactPhone) {
        this.teamId = teamId;
        this.groupId = groupId;
        this.name = name;
        this.idNumber = idNumber;
        this.contactPhone = contactPhone;
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
    
    public Long getTeamId() {
        return teamId;
    }
    
    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }
    
    public Long getGroupId() {
        return groupId;
    }
    
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getIdNumber() {
        return idNumber;
    }
    
    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }
    
    public String getContactPhone() {
        return contactPhone;
    }
    
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
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
    
    @Override
    public String toString() {
        return "Athlete{" +
                "id=" + id +
                ", teamId=" + teamId +
                ", groupId=" + groupId +
                ", name='" + name + '\'' +
                ", idNumber='" + idNumber + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Athlete athlete = (Athlete) o;
        
        return id != null ? id.equals(athlete.id) : athlete.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
