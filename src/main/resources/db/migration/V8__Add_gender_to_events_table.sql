-- 为events表添加gender字段
ALTER TABLE events ADD COLUMN gender VARCHAR(10) NOT NULL DEFAULT 'MIXED';

-- 更新现有数据的gender字段
UPDATE events SET gender = 'MALE' WHERE name LIKE '%男子%';
UPDATE events SET gender = 'FEMALE' WHERE name LIKE '%女子%';
UPDATE events SET gender = 'MIXED' WHERE gender IS NULL OR gender = 'MIXED';
