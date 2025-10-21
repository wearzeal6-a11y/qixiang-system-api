package com.qixiang.qixiang_system_api.dto;

/**
 * 组别数据传输对象
 * 用于前后端数据交互
 */
public class GroupDTO {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 组别名称
     */
    private String name;
    
    /**
     * 性别
     */
    private String gender;
    
    /**
     * 年级
     */
    private String grade;
    
    /**
     * 每队最大运动员数
     */
    private Integer maxAthletesPerTeam;
    
    /**
     * 每个运动员最大报名项目数
     */
    private Integer maxEventsPerAthlete;
    
    /**
     * 默认构造函数
     */
    public GroupDTO() {
    }
    
    /**
     * 构造函数
     * @param id 主键ID
     * @param name 组别名称
     * @param gender 性别
     * @param grade 年级
     */
    public GroupDTO(Long id, String name, String gender, String grade) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.grade = grade;
    }
    
    /**
     * 完整构造函数
     * @param id 主键ID
     * @param name 组别名称
     * @param gender 性别
     * @param grade 年级
     * @param maxAthletesPerTeam 每队最大运动员数
     * @param maxEventsPerAthlete 每个运动员最大报名项目数
     */
    public GroupDTO(Long id, String name, String gender, String grade, 
                   Integer maxAthletesPerTeam, Integer maxEventsPerAthlete) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.grade = grade;
        this.maxAthletesPerTeam = maxAthletesPerTeam;
        this.maxEventsPerAthlete = maxEventsPerAthlete;
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
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getGrade() {
        return grade;
    }
    
    public void setGrade(String grade) {
        this.grade = grade;
    }
    
    public Integer getMaxAthletesPerTeam() {
        return maxAthletesPerTeam;
    }
    
    public void setMaxAthletesPerTeam(Integer maxAthletesPerTeam) {
        this.maxAthletesPerTeam = maxAthletesPerTeam;
    }
    
    public Integer getMaxEventsPerAthlete() {
        return maxEventsPerAthlete;
    }
    
    public void setMaxEventsPerAthlete(Integer maxEventsPerAthlete) {
        this.maxEventsPerAthlete = maxEventsPerAthlete;
    }
    
    @Override
    public String toString() {
        return "GroupDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", grade='" + grade + '\'' +
                ", maxAthletesPerTeam=" + maxAthletesPerTeam +
                ", maxEventsPerAthlete=" + maxEventsPerAthlete +
                '}';
    }
}
