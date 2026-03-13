-- 将老师的数据整理为该格式后导入数据库
-- 表名建议: knowledge_entry

create table if not exists knowledge_entry (
    id bigint auto_increment primary key,
    question varchar(300) not null,
    answer varchar(2000) not null,
    keywords varchar(500)
);

-- 示例数据
insert into knowledge_entry(question, answer, keywords) values
('什么是时序分析？', '时序分析用于验证电路在目标时钟频率下是否满足建立保持时间约束。', '时序分析,建立时间,保持时间'),
('Verilog中阻塞赋值和非阻塞赋值区别？', '阻塞赋值按顺序立即生效，非阻塞赋值在时间步末统一更新。', 'Verilog,阻塞赋值,非阻塞赋值');
