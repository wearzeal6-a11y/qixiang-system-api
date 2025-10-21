package com.qixiang.qixiang_system_api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 竞赛组别实体类
 * 代表田径运动会中的竞赛组别（如高一男子组、高二女子组等）
 * 这是报名规则的核心存储中心
 */
@Entity
@Table(name = "groups")
public class Group {
    
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 组别名称
     * 如：高一男子组、高二女子组、教工组等
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    /**
     * 关联的运动会ID
     */
    @Column(name = "sports_meet_id", nullable = false)
    private Long sportsMeetId;
    
    /**
     * 性别
     * MALE-男性，FEMALE-女性，MIXED-混合
     */
    @Column(name = "gender", length = 10)
    private String gender;
    
    /**
     * 年级
     * 如：高一、高二、高三、教工等
     */
    @Column(name = "grade", length = 20)
    private String grade;
    
    /**
     * 每队领队教练最大名额
     */
    @Column(name = "max_leaders_per_team")
    private Integer maxLeadersPerTeam = 2;
    
    /**
     * 每队运动员最大名额
     */
    @Column(name = "max_athletes_per_team")
    private Integer maxAthletesPerTeam = 50;
    
    /**
     * 每人最大报名项目数
     */
    @Column(name = "max_events_per_athlete")
    private Integer maxEventsPerAthlete = 3;
    
    /**
     * 每项目最大参赛人数(0=无限制)
     */
    @Column(name = "max_participants_per_event")
    private Integer maxParticipantsPerEvent = 0;
    
    /**
     * 每队每接力项目最大队伍数
     */
    @Column(name = "max_relays_per_team")
    private Integer maxRelaysPerTeam = 1;
    
    /**
     * 是否允许混合项目
     */
    @Column(name = "allow_mixed_events")
    private Boolean allowMixedEvents = false;
    
    /**
     * 组别状态：ACTIVE-激活，INACTIVE-禁用
     */
    @Column(name = "status", length = 20)
    private String status = "ACTIVE";
    
    /**
     * 组别描述
     */
    @Column(name = "description", length = 500)
    private String description;
    
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
     * 性别枚举
     */
    public enum Gender {
        MALE("MALE", "男性"),
        FEMALE("FEMALE", "女性"),
        MIXED("MIXED", "混合");
        
        private final String code;
        private final String description;
        
        Gender(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
        
        public static Gender fromCode(String code) {
            for (Gender gender : values()) {
                if (gender.code.equals(code)) {
                    return gender;
                }
            }
            throw new IllegalArgumentException("Unknown gender code: " + code);
        }
    }
    
    /**
     * 组别状态枚举
     */
    public enum GroupStatus {
        ACTIVE("ACTIVE", "激活"),
        INACTIVE("INACTIVE", "禁用");
        
        private final String code;
        private final String description;
        
        GroupStatus(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
        
        public static GroupStatus fromCode(String code) {
            for (GroupStatus status : values()) {
                if (status.code.equals(code)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown group status code: " + code);
        }
    }
    
    /**
     * 默认构造函数
     */
    public Group() {
        this.status = GroupStatus.ACTIVE.getCode();
        this.maxLeadersPerTeam = 2;
        this.maxAthletesPerTeam = 50;
        this.maxEventsPerAthlete = 3;
        this.maxParticipantsPerEvent = 0;
        this.maxRelaysPerTeam = 1;
        this.allowMixedEvents = false;
    }
    
    /**
     * 构造函数
     * @param name 组别名称
     * @param sportsMeetId 运动会ID
     */
    public Group(String name, Long sportsMeetId) {
        this();
        this.name = name;
        this.sportsMeetId = sportsMeetId;
    }
    
    /**
     * 构造函数
     * @param name 组别名称
     * @param sportsMeetId 运动会ID
     * @param gender 性别
     * @param grade 年级
     */
    public Group(String name, Long sportsMeetId, String gender, String grade) {
        this(name, sportsMeetId);
        this.gender = gender;
        this.grade = grade;
    }
    
    /**
     * 构造函数（完整参数）
     * @param name 组别名称
     * @param sportsMeetId 运动会ID
     * @param gender 性别
     * @param grade 年级
     * @param maxLeadersPerTeam 每队领队教练最大名额
     * @param maxAthletesPerTeam 每队运动员最大名额
     * @param maxEventsPerAthlete 每人最大报名项目数
     * @param maxParticipantsPerEvent 每项目最大参赛人数
     */
    public Group(String name, Long sportsMeetId, String gender, String grade,
                  Integer maxLeadersPerTeam, Integer maxAthletesPerTeam,
                  Integer maxEventsPerAthlete, Integer maxParticipantsPerEvent) {
        this(name, sportsMeetId, gender, grade);
        this.maxLeadersPerTeam = maxLeadersPerTeam;
        this.maxAthletesPerTeam = maxAthletesPerTeam;
        this.maxEventsPerAthlete = maxEventsPerAthlete;
        this.maxParticipantsPerEvent = maxParticipantsPerEvent;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        // 设置默认值
        if (status == null) {
            status = GroupStatus.ACTIVE.getCode();
        }
        if (maxLeadersPerTeam == null) {
            maxLeadersPerTeam = 2;
        }
        if (maxAthletesPerTeam == null) {
            maxAthletesPerTeam = 50;
        }
        if (maxEventsPerAthlete == null) {
            maxEventsPerAthlete = 3;
        }
        if (maxParticipantsPerEvent == null) {
            maxParticipantsPerEvent = 0;
        }
        if (maxRelaysPerTeam == null) {
            maxRelaysPerTeam = 1;
        }
        if (allowMixedEvents == null) {
            allowMixedEvents = false;
        }
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
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    /**
     * 获取性别枚举
     * @return 性别枚举
     */
    public Gender getGenderEnum() {
        return gender != null ? Gender.fromCode(gender) : null;
    }
    
    /**
     * 设置性别（使用枚举）
     * @param gender 性别枚举
     */
    public void setGenderEnum(Gender gender) {
        this.gender = gender != null ? gender.getCode() : null;
    }
    
    public String getGrade() {
        return grade;
    }
    
    public void setGrade(String grade) {
        this.grade = grade;
    }
    
    public Integer getMaxLeadersPerTeam() {
        return maxLeadersPerTeam;
    }
    
    public void setMaxLeadersPerTeam(Integer maxLeadersPerTeam) {
        this.maxLeadersPerTeam = maxLeadersPerTeam != null ? maxLeadersPerTeam : 2;
    }
    
    public Integer getMaxAthletesPerTeam() {
        return maxAthletesPerTeam;
    }
    
    public void setMaxAthletesPerTeam(Integer maxAthletesPerTeam) {
        this.maxAthletesPerTeam = maxAthletesPerTeam != null ? maxAthletesPerTeam : 50;
    }
    
    public Integer getMaxEventsPerAthlete() {
        return maxEventsPerAthlete;
    }
    
    public void setMaxEventsPerAthlete(Integer maxEventsPerAthlete) {
        this.maxEventsPerAthlete = maxEventsPerAthlete != null ? maxEventsPerAthlete : 3;
    }
    
    public Integer getMaxParticipantsPerEvent() {
        return maxParticipantsPerEvent;
    }
    
    public void setMaxParticipantsPerEvent(Integer maxParticipantsPerEvent) {
        this.maxParticipantsPerEvent = maxParticipantsPerEvent != null ? maxParticipantsPerEvent : 0;
    }
    
    public Integer getMaxRelaysPerTeam() {
        return maxRelaysPerTeam;
    }
    
    public void setMaxRelaysPerTeam(Integer maxRelaysPerTeam) {
        this.maxRelaysPerTeam = maxRelaysPerTeam != null ? maxRelaysPerTeam : 1;
    }
    
    public Boolean getAllowMixedEvents() {
        return allowMixedEvents;
    }
    
    public void setAllowMixedEvents(Boolean allowMixedEvents) {
        this.allowMixedEvents = allowMixedEvents != null ? allowMixedEvents : false;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    /**
     * 获取组别状态枚举
     * @return 组别状态枚举
     */
    public GroupStatus getGroupStatusEnum() {
        return GroupStatus.fromCode(status);
    }
    
    /**
     * 设置组别状态（使用枚举）
     * @param status 组别状态枚举
     */
    public void setGroupStatusEnum(GroupStatus status) {
        this.status = status.getCode();
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
    
    /**
     * 判断是否为男性组别
     * @return true如果是男性组别
     */
    public boolean isMaleGroup() {
        return Gender.MALE.getCode().equals(gender);
    }
    
    /**
     * 判断是否为女性组别
     * @return true如果是女性组别
     */
    public boolean isFemaleGroup() {
        return Gender.FEMALE.getCode().equals(gender);
    }
    
    /**
     * 判断是否为混合组别
     * @return true如果是混合组别
     */
    public boolean isMixedGroup() {
        return Gender.MIXED.getCode().equals(gender);
    }
    
    /**
     * 判断组别是否激活
     * @return true如果组别激活
     */
    public boolean isActive() {
        return GroupStatus.ACTIVE.getCode().equals(status);
    }
    
    /**
     * 判断项目名额是否无限制
     * @return true如果无限制
     */
    public boolean hasUnlimitedParticipants() {
        return maxParticipantsPerEvent == null || maxParticipantsPerEvent == 0;
    }
    
    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sportsMeetId=" + sportsMeetId +
                ", gender='" + gender + '\'' +
                ", grade='" + grade + '\'' +
                ", maxLeadersPerTeam=" + maxLeadersPerTeam +
                ", maxAthletesPerTeam=" + maxAthletesPerTeam +
                ", maxEventsPerAthlete=" + maxEventsPerAthlete +
                ", maxParticipantsPerEvent=" + maxParticipantsPerEvent +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Group group = (Group) o;
        
        return id != null ? id.equals(group.id) : group.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
