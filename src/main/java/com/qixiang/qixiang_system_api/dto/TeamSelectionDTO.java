package com.qixiang.qixiang_system_api.dto;

/**
 * 参赛单位选择数据传输对象
 * 用于前端下拉选择框展示参赛单位信息
 */
public class TeamSelectionDTO {
    
    /**
     * 参赛单位ID
     */
    private Long id;
    
    /**
     * 参赛单位名称
     */
    private String name;
    
    /**
     * 默认构造函数
     */
    public TeamSelectionDTO() {
    }
    
    /**
     * 构造函数
     * @param id 参赛单位ID
     * @param name 参赛单位名称
     */
    public TeamSelectionDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    
    /**
     * 获取参赛单位ID
     * @return 参赛单位ID
     */
    public Long getId() {
        return id;
    }
    
    /**
     * 设置参赛单位ID
     * @param id 参赛单位ID
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * 获取参赛单位名称
     * @return 参赛单位名称
     */
    public String getName() {
        return name;
    }
    
    /**
     * 设置参赛单位名称
     * @param name 参赛单位名称
     */
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return "TeamSelectionDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        TeamSelectionDTO that = (TeamSelectionDTO) o;
        
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return name != null ? name.equals(that.name) : that.name == null;
    }
    
    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
