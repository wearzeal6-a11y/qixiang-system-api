package com.qixiang.qixiang_system_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 报名数据汇总数据传输对象
 * 用于返回报名数据的统计信息
 */
public class RegistrationSummaryDTO {
    
    /**
     * 统计项目标签
     * 如：高一男子组领队教练、高一男子组运动员、100米项目等
     */
    @JsonProperty("label")
    private String label;
    
    /**
     * 限制数量
     * 如：最大名额、最大参赛人数等
     */
    @JsonProperty("limit")
    private Integer limit;
    
    /**
     * 实际数量
     * 如：已报名人数、已确认数量等
     */
    @JsonProperty("actual")
    private Integer actual;
    
    /**
     * 使用率（百分比）
     * 计算方式：actual / limit * 100
     */
    @JsonProperty("usageRate")
    private Double usageRate;
    
    /**
     * 是否超限
     */
    @JsonProperty("isOverLimit")
    private Boolean isOverLimit;
    
    /**
     * 统计类型
     * LEADER-领队教练，ATHLETE-运动员，EVENT-项目，TOTAL-总计
     */
    @JsonProperty("type")
    private String type;
    
    /**
     * 组别ID（如果适用）
     */
    @JsonProperty("groupId")
    private Long groupId;
    
    /**
     * 组别名称（如果适用）
     */
    @JsonProperty("groupName")
    private String groupName;
    
    /**
     * 项目ID（如果适用）
     */
    @JsonProperty("eventId")
    private Long eventId;
    
    /**
     * 项目名称（如果适用）
     */
    @JsonProperty("eventName")
    private String eventName;
    
    /**
     * 默认构造函数
     */
    public RegistrationSummaryDTO() {
    }
    
    /**
     * 构造函数
     * @param label 统计项目标签
     * @param limit 限制数量
     * @param actual 实际数量
     */
    public RegistrationSummaryDTO(String label, Integer limit, Integer actual) {
        this.label = label;
        this.limit = limit;
        this.actual = actual;
        this.calculateDerivedFields();
    }
    
    /**
     * 构造函数（完整参数）
     * @param label 统计项目标签
     * @param limit 限制数量
     * @param actual 实际数量
     * @param type 统计类型
     * @param groupId 组别ID
     * @param groupName 组别名称
     * @param eventId 项目ID
     * @param eventName 项目名称
     */
    public RegistrationSummaryDTO(String label, Integer limit, Integer actual, 
                                 String type, Long groupId, String groupName,
                                 Long eventId, String eventName) {
        this.label = label;
        this.limit = limit;
        this.actual = actual;
        this.type = type;
        this.groupId = groupId;
        this.groupName = groupName;
        this.eventId = eventId;
        this.eventName = eventName;
        this.calculateDerivedFields();
    }
    
    /**
     * 计算衍生字段
     */
    private void calculateDerivedFields() {
        // 计算使用率
        if (limit != null && limit > 0) {
            this.usageRate = (double) actual / limit * 100;
            this.usageRate = Math.round(this.usageRate * 100.0) / 100.0; // 保留两位小数
        } else {
            this.usageRate = null; // 无限制时使用率为null
        }
        
        // 判断是否超限
        this.isOverLimit = (limit != null && actual > limit);
    }
    
    /**
     * 统计类型枚举
     */
    public enum SummaryType {
        LEADER("LEADER", "领队教练"),
        ATHLETE("ATHLETE", "运动员"),
        EVENT("EVENT", "项目"),
        TOTAL("TOTAL", "总计");
        
        private final String code;
        private final String description;
        
        SummaryType(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
        
        public static SummaryType fromCode(String code) {
            for (SummaryType type : values()) {
                if (type.code.equals(code)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown summary type code: " + code);
        }
    }
    
    // Getter and Setter methods
    
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
    public Integer getLimit() {
        return limit;
    }
    
    public void setLimit(Integer limit) {
        this.limit = limit;
        this.calculateDerivedFields(); // 重新计算衍生字段
    }
    
    public Integer getActual() {
        return actual;
    }
    
    public void setActual(Integer actual) {
        this.actual = actual;
        this.calculateDerivedFields(); // 重新计算衍生字段
    }
    
    public Double getUsageRate() {
        return usageRate;
    }
    
    public Boolean getIsOverLimit() {
        return isOverLimit;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    /**
     * 获取统计类型枚举
     * @return 统计类型枚举
     */
    public SummaryType getSummaryTypeEnum() {
        return type != null ? SummaryType.fromCode(type) : null;
    }
    
    /**
     * 设置统计类型（使用枚举）
     * @param type 统计类型枚举
     */
    public void setSummaryTypeEnum(SummaryType type) {
        this.type = type.getCode();
    }
    
    public Long getGroupId() {
        return groupId;
    }
    
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
    
    public String getGroupName() {
        return groupName;
    }
    
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    
    public Long getEventId() {
        return eventId;
    }
    
    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
    
    public String getEventName() {
        return eventName;
    }
    
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
    
    /**
     * 判断是否为领队教练统计
     * @return true如果是领队教练统计
     */
    public boolean isLeaderSummary() {
        return SummaryType.LEADER.getCode().equals(type);
    }
    
    /**
     * 判断是否为运动员统计
     * @return true如果是运动员统计
     */
    public boolean isAthleteSummary() {
        return SummaryType.ATHLETE.getCode().equals(type);
    }
    
    /**
     * 判断是否为项目统计
     * @return true如果是项目统计
     */
    public boolean isEventSummary() {
        return SummaryType.EVENT.getCode().equals(type);
    }
    
    /**
     * 判断是否为总计统计
     * @return true如果是总计统计
     */
    public boolean isTotalSummary() {
        return SummaryType.TOTAL.getCode().equals(type);
    }
    
    /**
     * 判断是否有名额限制
     * @return true如果有名额限制
     */
    public boolean hasLimit() {
        return limit != null && limit > 0;
    }
    
    /**
     * 获取剩余名额
     * @return 剩余名额，如果无限制则返回null
     */
    public Integer getRemainingSlots() {
        if (limit == null) {
            return null;
        }
        return Math.max(0, limit - actual);
    }
    
    /**
     * 获取剩余名额百分比
     * @return 剩余名额百分比，如果无限制则返回null
     */
    public Double getRemainingRate() {
        if (limit == null || limit == 0) {
            return null;
        }
        double remaining = Math.max(0, limit - actual);
        return Math.round((remaining / limit) * 10000.0) / 100.0; // 保留两位小数
    }
    
    @Override
    public String toString() {
        return "RegistrationSummaryDTO{" +
                "label='" + label + '\'' +
                ", limit=" + limit +
                ", actual=" + actual +
                ", usageRate=" + usageRate +
                ", isOverLimit=" + isOverLimit +
                ", type='" + type + '\'' +
                ", groupId=" + groupId +
                ", groupName='" + groupName + '\'' +
                ", eventId=" + eventId +
                ", eventName='" + eventName + '\'' +
                '}';
    }
}
