package com.qixiang.qixiang_system_api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 参赛单位实体类
 * 代表参加运动会的单位/队伍信息
 */
@Entity
@Table(name = "teams")
public class Team {
    
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 参赛单位名称
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    /**
     * 关联的运动会ID
     */
    @Column(name = "sports_meet_id", nullable = false)
    private Long sportsMeetId;
    
    /**
     * 所属机构代码
     * 用于明确参赛单位归属，解决数据归属混乱问题
     */
    @Column(name = "org_code", nullable = false, length = 50)
    private String orgCode;
    
    /**
     * 所属竞赛组别ID
     * 建立班级与竞赛组别的直接关联
     */
    @Column(name = "group_id")
    private Long groupId;
    
    /**
     * 参赛单位代码
     */
    @Column(name = "team_code", length = 50)
    private String teamCode;
    
    /**
     * 登录密码
     * 使用BCrypt加密存储
     */
    @Column(name = "password", nullable = false, length = 255)
    private String password;
    
    /**
     * 联系人姓名
     */
    @Column(name = "contact_person", length = 50)
    private String contactPerson;
    
    /**
     * 联系电话
     */
    @Column(name = "contact_phone", length = 20)
    private String contactPhone;
    
    /**
     * 参赛单位描述
     */
    @Column(name = "description", length = 500)
    private String description;
    
    /**
     * 参赛状态：ACTIVE-激活，INACTIVE-禁用，WITHDRAWN-退赛
     */
    @Column(name = "status", length = 20)
    private String status = "ACTIVE";
    
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
     * 备注信息
     */
    @Column(name = "remarks", length = 1000)
    private String remarks;
    
    /**
     * 默认构造函数
     */
    public Team() {
    }
    
    /**
     * 构造函数
     * @param name 参赛单位名称
     * @param sportsMeetId 运动会ID
     */
    public Team(String name, Long sportsMeetId) {
        this.name = name;
        this.sportsMeetId = sportsMeetId;
        this.status = "ACTIVE";
    }
    
    /**
     * 构造函数
     * @param name 参赛单位名称
     * @param sportsMeetId 运动会ID
     * @param teamCode 参赛单位代码
     */
    public Team(String name, Long sportsMeetId, String teamCode) {
        this.name = name;
        this.sportsMeetId = sportsMeetId;
        this.teamCode = teamCode;
        this.status = "ACTIVE";
    }
    
    /**
     * 构造函数
     * @param name 参赛单位名称
     * @param sportsMeetId 运动会ID
     * @param teamCode 参赛单位代码
     * @param password 登录密码
     */
    public Team(String name, Long sportsMeetId, String teamCode, String password) {
        this.name = name;
        this.sportsMeetId = sportsMeetId;
        this.teamCode = teamCode;
        this.password = password;
        this.status = "ACTIVE";
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
    
    public Long getSportsMeetId() {
        return sportsMeetId;
    }
    
    public void setSportsMeetId(Long sportsMeetId) {
        this.sportsMeetId = sportsMeetId;
    }
    
    public String getOrgCode() {
        return orgCode;
    }
    
    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }
    
    public Long getGroupId() {
        return groupId;
    }
    
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
    
    public String getTeamCode() {
        return teamCode;
    }
    
    public void setTeamCode(String teamCode) {
        this.teamCode = teamCode;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getContactPerson() {
        return contactPerson;
    }
    
    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }
    
    public String getContactPhone() {
        return contactPhone;
    }
    
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
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
    
    public String getRemarks() {
        return remarks;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sportsMeetId=" + sportsMeetId +
                ", teamCode='" + teamCode + '\'' +
                ", password='[PROTECTED]'" +
                ", contactPerson='" + contactPerson + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
