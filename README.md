# EDA Query - 学生课程AI问答网站

本项目按你的要求搭建：

- 前端: Vue 3 + Vite，聊天界面仿微信布局（左侧历史会话，右侧聊天窗口）
- 后端: Java Spring Boot
- 问答策略: 先查询教师 SQL 数据，再调用联网 AI（DeepSeek）

## 目录结构

- `frontend/`: Vue 前端
- `backend/`: Spring Boot 后端
- `SQL/`: 放置教师提供的 SQL 文件

## 核心接口

- `POST /api/chat/send`
  - 请求: `{ "sessionId": "可空", "message": "问题文本" }`
  - 返回: `{ "sessionId": "xxx", "answer": "...", "source": "teacher-sql|deepseek" }`
- `GET /api/chat/sessions`: 获取左侧会话列表
- `GET /api/chat/sessions/{sessionId}/messages`: 获取会话消息

## 数据来源逻辑

1. 先到 `knowledge_entry` 表做关键字匹配查询
2. 若 SQL 中无合适答案，则调用 DeepSeek

## 后端配置

编辑 `backend/src/main/resources/application.yml`:

1. 配置老师数据库
- 将 `spring.datasource.url/username/password` 改成老师提供数据库
- 默认是 H2 内存库，仅用于演示

2. 配置 DeepSeek
- 使用环境变量 `DEEPSEEK_API_KEY`
- 已预置接口地址 `https://api.deepseek.com/chat/completions`
- 模型默认 `deepseek-chat`

## SQL 准备

1. 默认演示表结构在 `backend/src/main/resources/schema.sql`
2. 演示数据在 `backend/src/main/resources/data.sql`
3. 你可将老师给的 SQL 导入到同名表 `knowledge_entry`

推荐字段:

- `id bigint`
- `question varchar`
- `answer varchar`
- `keywords varchar`

## 运行方式

### 启动后端

方式 A（推荐）: 在 IDE 中打开 `backend/` 作为 Maven 工程，运行 `AssistantBackendApplication`。

方式 B（命令行）:

```bash
cd backend
mvn spring-boot:run
```

如果提示 `mvn` 不存在，请先安装 Maven 或使用 IntelliJ IDEA/VS Code Java 扩展直接运行。

### 启动前端

```bash
cd frontend
npm install
npm run dev
```

打开 `http://localhost:5173`。

## 你需要做的最小修改

1. 在 `application.yml` 里替换老师数据库连接
2. 设置环境变量 `DEEPSEEK_API_KEY`
3. 将老师 SQL 数据导入到 `knowledge_entry` 表

完成后即满足“SQL 优先，联网 AI 补充”的课程问答网站需求。
