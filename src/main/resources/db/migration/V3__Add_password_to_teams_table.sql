-- =====================================================
-- 麒翔田径编排系统 - 添加teams表password字段
-- 数据库版本: MySQL 8.0+
-- 创建时间: 2025-10-14
-- 描述: 为teams表添加password字段以支持参赛单位登录认证
-- =====================================================

-- 为teams表添加password字段
ALTER TABLE teams 
ADD COLUMN password VARCHAR(255) NOT NULL COMMENT '登录密码，使用BCrypt加密存储' 
AFTER team_code;

-- 为现有参赛单位设置默认密码（admin123的BCrypt加密结果）
-- 注意：在生产环境中，应该为每个参赛单位设置独立的密码
UPDATE teams 
SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa' 
WHERE password IS NULL OR password = '';

-- 添加索引以提高查询性能
CREATE INDEX idx_teams_password ON teams(password(50));

-- 验证表结构
DESCRIBE teams;

-- 验证数据更新
SELECT 
    id,
    name,
    team_code,
    CASE 
        WHEN password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa' 
        THEN 'DEFAULT_PASSWORD' 
        ELSE 'CUSTOM_PASSWORD' 
    END as password_status,
    status,
    created_at
FROM teams 
ORDER BY id;

-- =====================================================
-- 说明
-- =====================================================

/*
1. 字段说明：
   - password: 存储BCrypt加密后的密码，长度255确保足够容纳加密字符串
   - 位置：在team_code字段之后，contact_person字段之前

2. 默认密码：
   - 所有现有参赛单位的默认密码设置为：admin123
   - 使用BCrypt加密存储，确保安全性

3. 安全建议：
   - 生产环境中应该强制每个参赛单位修改默认密码
   - 建议实现密码修改功能和密码复杂度验证
   - 可以考虑添加密码过期策略

4. 索引说明：
   - 为password字段创建前缀索引，提高密码验证查询性能
   - 使用前缀长度50，在性能和索引大小之间取得平衡

5. 使用方法：
   - 参赛单位登录时，使用teamId + password进行认证
   - 后端使用BCrypt的matches()方法验证密码
   - 密码验证逻辑与用户认证保持一致
*/
