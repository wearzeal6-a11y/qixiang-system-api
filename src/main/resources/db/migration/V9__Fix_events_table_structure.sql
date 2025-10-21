-- ============================================================================
-- 麒翔田径编排系统 - 修复事件表结构
-- 创建时间: 2025-10-20
-- 描述: 修复events表结构问题，添加gender字段，确保与实体类匹配
-- ============================================================================

-- 1. 添加gender字段到events表（如果不存在）
ALTER TABLE events ADD COLUMN IF NOT EXISTS gender VARCHAR(10) NOT NULL DEFAULT 'MIXED' COMMENT '性别限制：MALE-男子项目，FEMALE-女子项目，MIXED-混合项目';

-- 2. 更新现有项目数据的gender字段
-- 根据项目名称推断性别分类
UPDATE events SET gender = 'MALE' 
WHERE name IN ('100米', '200米', '400米', '800米', '1500米', '跳远', '跳高', '铅球', '标枪') 
AND gender = 'MIXED';

UPDATE events SET gender = 'FEMALE' 
WHERE name LIKE '%女子%' 
AND gender = 'MIXED';

-- 3. 修正接力项目的gender字段
UPDATE events SET gender = 'MALE' 
WHERE name LIKE '男子%4x100米接力' OR name LIKE '男子4x100米接力' 
AND gender = 'MIXED';

UPDATE events SET gender = 'FEMALE' 
WHERE name LIKE '女子%4x100米接力' OR name LIKE '女子4x100米接力' 
AND gender = 'MIXED';

-- 4. 更新描述字段，添加更详细的描述
UPDATE events SET description = '男子100米短跑项目' WHERE name = '100米' AND gender = 'MALE';
UPDATE events SET description = '女子100米短跑项目' WHERE name = '100米' AND gender = 'FEMALE';
UPDATE events SET description = '男子200米短跑项目' WHERE name = '200米' AND gender = 'MALE';
UPDATE events SET description = '女子200米短跑项目' WHERE name = '200米' AND gender = 'FEMALE';
UPDATE events SET description = '男子400米短跑项目' WHERE name = '400米' AND gender = 'MALE';
UPDATE events SET description = '女子400米短跑项目' WHERE name = '400米' AND gender = 'FEMALE';
UPDATE events SET description = '男子跳远项目' WHERE name = '跳远' AND gender = 'MALE';
UPDATE events SET description = '女子跳远项目' WHERE name = '跳远' AND gender = 'FEMALE';
UPDATE events SET description = '男子跳高项目' WHERE name = '跳高' AND gender = 'MALE';
UPDATE events SET description = '女子跳高项目' WHERE name = '跳高' AND gender = 'FEMALE';
UPDATE events SET description = '男子铅球项目' WHERE name = '铅球' AND gender = 'MALE';
UPDATE events SET description = '女子铅球项目' WHERE name = '铅球' AND gender = 'FEMALE';
UPDATE events SET description = '男子标枪项目' WHERE name = '标枪' AND gender = 'MALE';
UPDATE events SET description = '女子标枪项目' WHERE name = '标枪' AND gender = 'FEMALE';
UPDATE events SET description = '男子4x100米接力项目' WHERE name = '4x100米接力' AND gender = 'MALE';
UPDATE events SET description = '女子4x100米接力项目' WHERE name = '4x100米接力' AND gender = 'FEMALE';
UPDATE events SET description = '男子4x400米接力项目' WHERE name = '4x400米接力' AND gender = 'MALE';
UPDATE events SET description = '女子4x400米接力项目' WHERE name = '4x400米接力' AND gender = 'FEMALE';

-- 5. 添加索引优化查询性能
CREATE INDEX IF NOT EXISTS idx_events_gender ON events(gender);
CREATE INDEX IF NOT EXISTS idx_events_name_gender ON events(name, gender);

-- 6. 验证数据修复结果
SELECT 
    id,
    name,
    event_type,
    gender,
    description,
    created_at,
    updated_at
FROM events 
ORDER BY id;

-- 7. 统计不同性别项目的数量
SELECT 
    gender,
    COUNT(*) as count,
    GROUP_CONCAT(name ORDER BY name) as events
FROM events 
GROUP BY gender
ORDER BY gender;

-- 8. 检查是否有遗漏的项目（没有正确设置gender的）
SELECT * FROM events WHERE gender = 'MIXED';
