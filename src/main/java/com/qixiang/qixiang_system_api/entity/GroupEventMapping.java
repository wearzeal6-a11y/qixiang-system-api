package com.qixiang.qixiang_system_api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 组别-项目关联实体类
 * 定义哪些竞赛组别可以参加哪些比赛项目
 */
@Entity
@Table(name = "group_event_mappings")
public class GroupEventMapping {
    
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
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
     * 是否为必报项目
     */
    @Column(name = "is_mandatory", nullable = false)
    private Boolean isMandatory = false;
    
    /**
     * 创建时间
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 默认构造函数
     */
    public GroupEventMapping() {
        this.isMandatory = false;
    }
    
    /**
     * 构造函数
     * @param groupId 竞赛组别ID
     * @param eventId 比赛项目ID
     */
    public GroupEventMapping(Long groupId, Long eventId) {
        this();
        this.groupId = groupId;
        this.eventId = eventId;
    }
    
    /**
     * 构造函数
     * @param groupId 竞赛组别ID
     * @param eventId 比赛项目ID
     * @param isMandatory 是否为必报项目
     */
    public GroupEventMapping(Long groupId, Long eventId, Boolean isMandatory) {
        this.groupId = groupId;
        this.eventId = eventId;
        this.isMandatory = isMandatory != null ? isMandatory : false;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isMandatory == null) {
            isMandatory = false;
        }
    }
    
    // Getter and Setter methods
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public Boolean getIsMandatory() {
        return isMandatory;
    }
    
    public void setIsMandatory(Boolean isMandatory) {
        this.isMandatory = isMandatory != null ? isMandatory : false;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * 判断是否为必报项目
     * @return true如果是必报项目
     */
    public boolean isMandatoryEvent() {
        return Boolean.TRUE.equals(isMandatory);
    }
    
    /**
     * 设置为必报项目
     */
    public void setAsMandatory() {
        this.isMandatory = true;
    }
    
    /**
     * 设置为选报项目
     */
    public void setAsOptional() {
        this.isMandatory = false;
    }
    
    @Override
    public String toString() {
        return "GroupEventMapping{" +
                "id=" + id +
                ", groupId=" + groupId +
                ", eventId=" + eventId +
                ", isMandatory=" + isMandatory +
                ", createdAt=" + createdAt +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        GroupEventMapping that = (GroupEventMapping) o;
        
        return id != null ? id.equals(that.id) : that.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
