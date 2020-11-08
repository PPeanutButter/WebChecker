PRAGMA foreign_keys=ON;
-- 任务表
create table IF NOT EXISTS tasks(id INTEGER primary key AUTOINCREMENT,MD5 text);
-- 单个任务的上次查询到的list表
create table IF NOT EXISTS lists(id INTEGER primary key,item text,timeMillis numeric,FOREIGN KEY(id) REFERENCES tasks(id) on delete cascade);
-- 任务的详细配置
create table IF NOT EXISTS task(id INTEGER primary key,name text,url text,css text,attr text,FOREIGN KEY(id) REFERENCES tasks(id) on delete cascade);