package com.qixiang.qixiang_system_api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 比赛项目实体类
 * 代表田径运动会中的各个比赛项目
 */
@Entity
@Table(name = "events")
public class Event {
    
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 项目名称
     * 如：100米、跳远、铅球、4x100米接力
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    /**
     * 项目类型
     * INDIVIDUAL-个人项目，RELAY-接力项目，TEAM-团体项目
     */
    @Column(name = "event_type", nullable = false, length = 20)
    private String eventType;
    
    /**
     * 项目描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    /**
     * 性别限制
     * MALE-男子项目，FEMALE-女子项目，MIXED-混合项目
     */
    @Column(name = "gender", nullable = false, length = 10)
    private String gender;
    
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
     * 项目类型枚举
     */
    public enum EventType {
        INDIVIDUAL("INDIVIDUAL", "个人项目"),
        RELAY("RELAY", "接力项目"),
        TEAM("TEAM", "团体项目");
        
        private final String code;
        private final String description;
        
        EventType(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
        
        public static EventType fromCode(String code) {
            for (EventType type : values()) {
                if (type.code.equals(code)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown event type code: " + code);
        }
    }
    
    /**
     * 默认构造函数
     */
    public Event() {
    }
    
    /**
     * 构造函数
     * @param name 项目名称
     * @param eventType 项目类型
     */
    public Event(String name, String eventType) {
        this.name = name;
        this.eventType = eventType;
    }
    
    /**
     * 构造函数
     * @param name 项目名称
     * @param eventType 项目类型
     * @param description 项目描述
     */
    public Event(String name, String eventType, String description) {
        this.name = name;
        this.eventType = eventType;
        this.description = description;
    }
    
    /**
     * 构造函数（使用枚举）
     * @param name 项目名称
     * @param eventType 项目类型枚举
     * @param description 项目描述
     */
    public Event(String name, EventType eventType, String description) {
        this.name = name;
        this.eventType = eventType.getCode();
        this.description = description;
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
    
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    /**
     * 获取项目类型枚举
     * @return 项目类型枚举
     */
    public EventType getEventTypeEnum() {
        return EventType.fromCode(eventType);
    }
    
    /**
     * 设置项目类型（使用枚举）
     * @param eventType 项目类型枚举
     */
    public void setEventTypeEnum(EventType eventType) {
        this.eventType = eventType.getCode();
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
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
     * 判断是否为个人项目
     * @return true如果是个人项目
     */
    public boolean isIndividual() {
        return EventType.INDIVIDUAL.getCode().equals(eventType);
    }
    
    /**
     * 判断是否为接力项目
     * @return true如果是接力项目
     */
    public boolean isRelay() {
        return EventType.RELAY.getCode().equals(eventType);
    }
    
    /**
     * 判断是否为团体项目
     * @return true如果是团体项目
     */
    public boolean isTeam() {
        return EventType.TEAM.getCode().equals(eventType);
    }
    
    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", eventType='" + eventType + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Event event = (Event) o;
        
        return id != null ? id.equals(event.id) : event.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
