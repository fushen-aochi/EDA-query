create table if not exists knowledge_entry (
    id bigint auto_increment primary key,
    question varchar(300) not null,
    answer varchar(2000) not null,
    keywords varchar(500)
);
