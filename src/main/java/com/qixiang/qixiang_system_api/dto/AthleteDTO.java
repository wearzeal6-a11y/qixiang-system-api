package com.qixiang.qixiang_system_api.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 运动员数据传输对象
 * 用于前后端数据交互
 */
public class AthleteDTO {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 参赛单位ID
     */
    private Long teamId;
    
    /**
     * 竞赛组别ID
     */
    private Long groupId;
    
    /**
     * 运动员姓名
     */
    private String name;
    
    /**
     * 身份证号
     */
    private String idNumber;
    
    /**
     * 联系电话
     */
    private String contactPhone;
    
    /**
     * 性别（从Group实体获取）
     */
    private String gender;
    
    /**
     * 组别名称（从Group实体获取）
     */
    private String groupName;
    
    /**
     * 组别对象（用于项目选择对话框）
     */
    private GroupDTO group;
    
    /**
     * 已报项目列表
     */
    private List<String> registeredEvents;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 默认构造函数
     */
    public AthleteDTO() {
    }
    
    /**
     * 构造函数
     * @param teamId 参赛单位ID
     * @param groupId 竞赛组别ID
     * @param name 运动员姓名
     */
    public AthleteDTO(Long teamId, Long groupId, String name) {
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
    public AthleteDTO(Long teamId, Long groupId, String name, String idNumber, String contactPhone) {
        this.teamId = teamId;
        this.groupId = groupId;
        this.name = name;
        this.idNumber = idNumber;
        this.contactPhone = contactPhone;
    }
    
    /**
     * 完整构造函数
     * @param id 主键ID
     * @param teamId 参赛单位ID
     * @param groupId 竞赛组别ID
     * @param name 运动员姓名
     * @param idNumber 身份证号
     * @param contactPhone 联系电话
     * @param gender 性别
     * @param groupName 组别名称
     * @param registeredEvents 已报项目列表
     * @param createdAt 创建时间
     * @param updatedAt 更新时间
     */
    public AthleteDTO(Long id, Long teamId, Long groupId, String name, String idNumber, 
                      String contactPhone, String gender, String groupName, 
                      List<String> registeredEvents, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.teamId = teamId;
        this.groupId = groupId;
        this.name = name;
        this.idNumber = idNumber;
        this.contactPhone = contactPhone;
        this.gender = gender;
        this.groupName = groupName;
        this.registeredEvents = registeredEvents;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getGroupName() {
        return groupName;
    }
    
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    
    public GroupDTO getGroup() {
        return group;
    }
    
    public void setGroup(GroupDTO group) {
        this.group = group;
    }
    
    public List<String> getRegisteredEvents() {
        return registeredEvents;
    }
    
    public void setRegisteredEvents(List<String> registeredEvents) {
        this.registeredEvents = registeredEvents;
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
     * 根据性别代码获取性别显示文本
     * @return 性别显示文本
     */
    public String getGenderDisplay() {
        if (gender == null) {
            return "未知";
        }
        switch (gender) {
            case "MALE":
                return "男";
            case "FEMALE":
                return "女";
            case "MIXED":
                return "混合";
            default:
                return gender;
        }
    }
    
    /**
     * 获取已报项目的显示文本
     * @return 已报项目字符串
     */
    public String getRegisteredEventsDisplay() {
        if (registeredEvents == null || registeredEvents.isEmpty()) {
            return "暂无项目";
        }
        return String.join("、", registeredEvents);
    }
    
    @Override
    public String toString() {
        return "AthleteDTO{" +
                "id=" + id +
                ", teamId=" + teamId +
                ", groupId=" + groupId +
                ", name='" + name + '\'' +
                ", idNumber='" + idNumber + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", gender='" + gender + '\'' +
                ", groupName='" + groupName + '\'' +
                ", registeredEvents=" + registeredEvents +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        AthleteDTO that = (AthleteDTO) o;
        
        return id != null ? id.equals(that.id) : that.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
