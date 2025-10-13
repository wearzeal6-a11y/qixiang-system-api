package com.qixiang.qixiang_system_api.repository;

import com.qixiang.qixiang_system_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问层接口
 * 继承JpaRepository提供基础的CRUD操作
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根据机构代码和用户名查询用户
     * 这是登录认证的核心查询方法
     * 
     * @param organizationCode 机构代码
     * @param username 用户名
     * @return 用户信息的Optional对象
     */
    Optional<User> findByOrganizationCodeAndUsername(String organizationCode, String username);
    
    /**
     * 根据机构代码查询所有用户
     * 
     * @param organizationCode 机构代码
     * @return 该机构下的所有用户列表
     */
    List<User> findByOrganizationCode(String organizationCode);
    
    /**
     * 根据用户名查询用户（不区分机构）
     * 用于检查用户名是否已被使用
     * 
     * @param username 用户名
     * @return 用户列表
     */
    List<User> findByUsername(String username);
    
    /**
     * 根据角色查询用户
     * 
     * @param role 角色
     * @return 指定角色的用户列表
     */
    List<User> findByRole(String role);
    
    /**
     * 根据状态查询用户
     * 
     * @param status 状态（1-启用，0-禁用）
     * @return 指定状态的用户列表
     */
    List<User> findByStatus(Integer status);
    
    /**
     * 检查用户名在指定机构中是否存在
     * 用于用户注册时的重复性检查
     * 
     * @param organizationCode 机构代码
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByOrganizationCodeAndUsername(String organizationCode, String username);
    
    /**
     * 根据机构代码和状态查询用户
     * 
     * @param organizationCode 机构代码
     * @param status 状态
     * @return 用户列表
     */
    List<User> findByOrganizationCodeAndStatus(String organizationCode, Integer status);
    
    /**
     * 根据角色和状态查询用户
     * 
     * @param role 角色
     * @param status 状态
     * @return 用户列表
     */
    List<User> findByRoleAndStatus(String role, Integer status);
    
    /**
     * 根据机构代码、角色和状态查询用户
     * 
     * @param organizationCode 机构代码
     * @param role 角色
     * @param status 状态
     * @return 用户列表
     */
    @Query("SELECT u FROM User u WHERE u.organizationCode = :organizationCode AND u.role = :role AND u.status = :status")
    List<User> findByOrganizationCodeAndRoleAndStatus(
            @Param("organizationCode") String organizationCode, 
            @Param("role") String role, 
            @Param("status") Integer status);
    
    /**
     * 统计指定机构的用户数量
     * 
     * @param organizationCode 机构代码
     * @return 用户数量
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.organizationCode = :organizationCode")
    long countByOrganizationCode(@Param("organizationCode") String organizationCode);
    
    /**
     * 统计指定机构的启用用户数量
     * 
     * @param organizationCode 机构代码
     * @return 启用用户数量
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.organizationCode = :organizationCode AND u.status = 1")
    long countActiveUsersByOrganizationCode(@Param("organizationCode") String organizationCode);
    
    /**
     * 根据用户名模糊查询用户
     * 
     * @param username 用户名（支持模糊匹配）
     * @return 用户列表
     */
    @Query("SELECT u FROM User u WHERE u.username LIKE %:username%")
    List<User> findByUsernameContaining(@Param("username") String username);
}
