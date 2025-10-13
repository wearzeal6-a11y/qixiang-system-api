-- ============================================================================
-- 麒翔田径编排系统 - 系统管理员用户表
-- 创建时间: 2025-10-13
-- 描述: 用于存储系统管理员账号信息的数据库表
-- ============================================================================

CREATE TABLE users (
    -- 主键字段
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID，自动增长',
    
    -- 用户基本信息
    organization_code VARCHAR(50) NOT NULL COMMENT '机构代码，用于区分不同组织',
    username VARCHAR(50) NOT NULL COMMENT '用户名，登录账号',
    password VARCHAR(255) NOT NULL COMMENT '密码，使用BCrypt算法加密存储',
    role VARCHAR(20) NOT NULL DEFAULT 'ADMIN' COMMENT '用户角色，默认为ADMIN',
    
    -- 审计字段
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    last_login_at TIMESTAMP NULL COMMENT '最后登录时间',
    status TINYINT DEFAULT 1 COMMENT '账号状态：1-启用，0-禁用',
    
    -- 创建联合唯一索引，确保同一机构内用户名唯一
    CONSTRAINT uk_org_username UNIQUE (organization_code, username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统管理员用户表';

-- 创建普通索引以提高查询性能
CREATE INDEX idx_organization_code ON users(organization_code);
CREATE INDEX idx_username ON users(username);
CREATE INDEX idx_role ON users(role);
CREATE INDEX idx_status ON users(status);
CREATE INDEX idx_created_at ON users(created_at);

-- 添加表注释说明
ALTER TABLE users COMMENT = '系统管理员用户表，存储管理员账号信息和认证数据';
