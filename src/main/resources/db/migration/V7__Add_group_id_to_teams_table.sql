-- ============================================================================
-- 麒翔田径编排系统 - 为Teams表添加组别关联
-- 创建时间: 2025-10-15
-- 描述: 为teams表添加group_id字段，建立班级与竞赛组别的直接关联
-- ============================================================================

-- 为teams表添加group_id字段
ALTER TABLE teams ADD COLUMN group_id BIGINT COMMENT '所属竞赛组别ID';

-- 为现有teams数据设置组别
-- 根据班级名称推断组别：
-- 高一(1)班 -> 高一男子组 (ID=1)
-- 高一(2)班 -> 高一女子组 (ID=2)  
-- 高二(1)班 -> 高二男子组 (ID=3)
-- 高二(2)班 -> 高二女子组 (ID=4)
-- 高三(1)班 -> 高三男子组 (ID=5)
-- 高三(2)班 -> 高三女子组 (ID=6)

UPDATE teams SET group_id = 1 WHERE name LIKE '高一(1)%' OR name LIKE '高一1%';
UPDATE teams SET group_id = 2 WHERE name LIKE '高一(2)%' OR name LIKE '高一2%';
UPDATE teams SET group_id = 3 WHERE name LIKE '高二(1)%' OR name LIKE '高二1%';
UPDATE teams SET group_id = 4 WHERE name LIKE '高二(2)%' OR name LIKE '高二2%';
UPDATE teams SET group_id = 5 WHERE name LIKE '高三(1)%' OR name LIKE '高三1%';
UPDATE teams SET group_id = 6 WHERE name LIKE '高三(2)%' OR name LIKE '高三2%';

-- 如果没有匹配的班级，默认设置为高一男子组
UPDATE teams SET group_id = 1 WHERE group_id IS NULL;

-- 添加索引以提高查询性能
CREATE INDEX idx_teams_group_id ON teams(group_id);

-- 添加外键约束（可选，如果需要严格的数据完整性）
-- ALTER TABLE teams ADD CONSTRAINT fk_teams_group_id FOREIGN KEY (group_id) REFERENCES groups(id);
