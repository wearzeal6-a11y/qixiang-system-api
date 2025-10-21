-- 创建麒翔田径编排系统数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS qixiang_sports_system 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE qixiang_sports_system;

-- 创建系统管理员用户表
CREATE TABLE users (
    -- 主键ID，唯一标识一个管理员
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID, 唯一标识一个管理员',
    
    -- 单位/组织代码，用于登录时区分不同机构的管理员
    organization_code VARCHAR(50) NOT NULL COMMENT '单位/组织代码, 用于登录时区分不同机构的管理员',
    
    -- 管理员登录名
    username VARCHAR(50) NOT NULL COMMENT '管理员登录名',
    
    -- 存储BCrypt等哈希算法加密后的密码，绝不能存明文
    password VARCHAR(255) NOT NULL COMMENT '存储BCrypt等哈希算法加密后的密码, 绝不能存明文',
    
    -- 用户角色，用于权限控制
    role VARCHAR(20) NOT NULL DEFAULT 'ADMIN' COMMENT '用户角色, 用于权限控制',
    
    -- 记录创建时间
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    
    -- 记录更新时间
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
    
    -- 账户状态：ACTIVE-激活，INACTIVE-禁用，LOCKED-锁定
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '账户状态：ACTIVE-激活，INACTIVE-禁用，LOCKED-锁定',
    
    -- 最后登录时间
    last_login_at TIMESTAMP NULL COMMENT '最后登录时间',
    
    -- 最后登录IP地址
    last_login_ip VARCHAR(45) NULL COMMENT '最后登录IP地址',
    
    -- 登录失败次数
    login_failure_count INT NOT NULL DEFAULT 0 COMMENT '登录失败次数',
    
    -- 账户锁定时间（当登录失败次数过多时锁定）
    locked_until TIMESTAMP NULL COMMENT '账户锁定时间（当登录失败次数过多时锁定）',
    
    -- 邮箱地址（可选，用于密码重置等功能）
    email VARCHAR(100) NULL COMMENT '邮箱地址（可选，用于密码重置等功能）',
    
    -- 手机号码（可选，用于短信验证等功能）
    phone VARCHAR(20) NULL COMMENT '手机号码（可选，用于短信验证等功能）',
    
    -- 真实姓名
    real_name VARCHAR(50) NULL COMMENT '真实姓名',
    
    -- 备注信息
    remarks TEXT NULL COMMENT '备注信息'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统管理员用户表';

-- 创建联合唯一索引，确保在同一个组织代码下，管理员的用户名是唯一的
CREATE UNIQUE INDEX uk_org_username ON users (organization_code, username) COMMENT '组织代码和用户名的联合唯一索引';

-- 创建单列索引以提高查询性能
CREATE INDEX idx_organization_code ON users (organization_code) COMMENT '组织代码索引';
CREATE INDEX idx_username ON users (username) COMMENT '用户名索引';
CREATE INDEX idx_role ON users (role) COMMENT '角色索引';
CREATE INDEX idx_status ON users (status) COMMENT '状态索引';
CREATE INDEX idx_created_at ON users (created_at) COMMENT '创建时间索引';
CREATE INDEX idx_last_login_at ON users (last_login_at) COMMENT '最后登录时间索引';

-- 插入默认管理员账户（密码为 admin123 的BCrypt加密结果）
INSERT INTO users (
    organization_code, 
    username, 
    password, 
    role, 
    real_name, 
    email,
    remarks
) VALUES (
    'QIXIANG_HQ', 
    'admin', 
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', -- admin123
    'SUPER_ADMIN', 
    '系统超级管理员', 
    'admin@qixiangsports.com',
    '系统默认超级管理员账户'
);

-- 插入测试用的普通管理员账户
INSERT INTO users (
    organization_code, 
    username, 
    password, 
    role, 
    real_name, 
    email,
    remarks
) VALUES (
    'SCHOOL_001', 
    'school_admin', 
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', -- admin123
    'ADMIN', 
    '学校管理员', 
    'admin@school001.edu.cn',
    '测试学校管理员账户'
);
