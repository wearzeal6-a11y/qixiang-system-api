-- 强制修复项目容量限制数据
-- 执行日期: 2025-10-21
-- 原因: V11迁移可能未正确执行，需要强制修复

-- 步骤1: 强制更新所有项目的容量限制设置
UPDATE events 
SET 
    is_capacity_limited = true,
    max_participants = CASE 
        WHEN name LIKE '%100米%' OR name LIKE '%200米%' THEN 16
        WHEN name LIKE '%400米%' THEN 12
        WHEN name LIKE '%跳远%' OR name LIKE '%跳高%' OR name LIKE '%铅球%' THEN 20
        WHEN name LIKE '%4x100米接力%' THEN 8
        ELSE 16
    END,
    current_participants = COALESCE((
        SELECT COUNT(*) 
        FROM registrations r 
        WHERE r.event_id = events.id AND r.status = 'CONFIRMED'
    ), 0);

-- 步骤2: 验证并确保数据完整性
UPDATE events 
SET is_capacity_limited = true 
WHERE is_capacity_limited IS NULL OR is_capacity_limited = false;

UPDATE events 
SET max_participants = 16 
WHERE max_participants IS NULL OR max_participants <= 0;

UPDATE events 
SET current_participants = 0 
WHERE current_participants IS NULL;

-- 步骤3: 添加调试信息，记录修复结果
-- 注意：INSERT语句如果表不存在会失败，但这不影响主要修复逻辑
-- 这里仅用于记录修复日志，可以忽略错误
