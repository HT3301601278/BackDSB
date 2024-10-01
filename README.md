# BackPro 项目

## 项目概述

BackPro 是一个基于 Spring Boot 框架开发的后端应用程序，旨在管理用户、设备及其数据。该系统支持用户注册、登录、修改密码等功能，同时允许用户添加、删除、更新设备信息，并对设备数据进行实时监控。当设备数据超过预设阈值时，系统将通过 WebSocket 向所有连接的客户端发送警报通知。

## 主要功能

1. **用户管理**
   - 用户注册
   - 用户登录
   - 修改用户密码

2. **设备管理**
   - 添加新设备
   - 删除设备
   - 更新设备信息
   - 设置设备阈值
   - 启动/停止设备数据生成
   - 切换设备状态（开/关）

3. **数据管理**
   - 实时数据生成与存储
   - 从外部 API 更新设备数据
   - 查询设备数据（按时间范围、阈值等）

4. **实时通知**
   - 当设备数据超过设定阈值时，系统通过 WebSocket 向所有客户端发送警报消息。

## 技术栈

- **后端框架**：Spring Boot
- **数据库**：MySQL
- **ORM**：Hibernate (Spring Data JPA)
- **实时通信**：WebSocket
- **测试框架**：JUnit
- **其他**：
  - Lombok（简化 Java 代码）
  - SLF4J（日志记录）

## 项目结构

```
src
├── main
│   ├── java
│   │   └── org.example.backpro
│   │       ├── BackProApplication.java
│   │       ├── config
│   │       │   ├── CorsConfig.java
│   │       │   └── WebSocketConfig.java
│   │       ├── controller
│   │       │   ├── DataGenerationController.java
│   │       │   ├── DataUpdateController.java
│   │       │   ├── DeviceController.java
│   │       │   └── UserController.java
│   │       ├── entity
│   │       │   ├── Device.java
│   │       │   ├── DeviceData.java
│   │       │   └── User.java
│   │       ├── exception
│   │       │   └── ResourceNotFoundException.java
│   │       ├── repository
│   │       │   ├── DeviceDataRepository.java
│   │       │   ├── DeviceRepository.java
│   │       │   └── UserRepository.java
│   │       ├── service
│   │       │   ├── DataGenerationService.java
│   │       │   ├── DataUpdateService.java
│   │       │   ├── DeviceService.java
│   │       │   └── UserService.java
│   │       └── websocket
│   │           └── AlertWebSocketHandler.java
│   └── resources
│       └── application.properties
└── test
    └── java
        └── org.example.backpro
            └── BackProApplicationTests.java
```

## 安装与运行

### 前提条件

- **Java 8 或更高版本**
- **Maven**
- **MySQL 数据库**

### 步骤

1. **克隆项目**

   ```bash
   git clone https://github.com/your-repo/backpro.git
   cd backpro
   ```

2. **配置数据库**

   - 创建一个名为 `backpro` 的 MySQL 数据库。
   - 修改 `src/main/resources/application.properties` 文件，设置数据库连接信息：

     ```properties
     spring.datasource.url=jdbc:mysql://localhost:3305/backpro?useSSL=false&serverTimezone=UTC
     spring.datasource.username=root
     spring.datasource.password=123456
     spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

     spring.jpa.hibernate.ddl-auto=update
     spring.jpa.show-sql=true
     spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL57Dialect
     spring.jpa.open-in-view=true

     spring.jpa.properties.hibernate.jdbc.time_zone=Asia/Shanghai

     spring.jackson.date-format=yyyy-MM-dd HH:mm:ss
     spring.jackson.time-zone=Asia/Shanghai

     logging.level.org.example.backpro=DEBUG
     ```

3. **构建项目**

   ```bash
   mvn clean install
   ```

4. **运行应用**

   ```bash
   mvn spring-boot:run
   ```

   应用将启动在 `http://localhost:8080`。

## API 文档

### 用户相关

- **注册用户**

  ```
  POST /api/users/register
  ```

  **请求体**：

  ```json
  {
    "username": "user1",
    "password": "password123"
  }
  ```

- **登录用户**

  ```
  POST /api/users/login
  ```

  **请求体**：

  ```json
  {
    "username": "user1",
    "password": "password123"
  }
  ```

- **修改密码**

  ```
  PUT /api/users/{id}/password
  ```

  **请求参数**：

  - `id`：用户ID

  **请求参数**：

  - `oldPassword`: 旧密码
  - `newPassword`: 新密码

### 设备相关

- **添加设备**

  ```
  POST /api/devices
  ```

  **请求体**：

  ```json
  {
    "name": "Device A",
    "macAddress": "00:1B:44:11:3A:B7",
    "communicationChannel": "Channel 1",
    "threshold": 75.0,
    "isOn": true
  }
  ```

- **删除设备**

  ```
  DELETE /api/devices/{id}
  ```

- **获取所有设备**

  ```
  GET /api/devices
  ```

  **请求参数**：

  - `page`：页码（默认0）
  - `size`：每页大小（默认10）

- **获取设备详情**

  ```
  GET /api/devices/{id}
  ```

- **更新设备信息**

  ```
  PUT /api/devices/{id}
  ```

  **请求体**：

  ```json
  {
    "name": "Device B",
    "macAddress": "00:1B:44:11:3A:B8",
    "communicationChannel": "Channel 2",
    "threshold": 80.0
  }
  ```

- **设置设备阈值**

  ```
  PUT /api/devices/{id}/threshold
  ```

  **请求参数**：

  - `threshold`: 新的阈值

- **切换设备状态**

  ```
  PUT /api/devices/{id}/toggle
  ```

### 数据生成与更新

- **启动数据生成**

  ```
  POST /api/data-generation/start
  ```

  **请求参数**：

  - `deviceId`: 设备ID
  - `durationMinutes`: 持续时间（分钟）
  - `intervalSeconds`: 数据生成间隔（秒）
  - `minValue`: 最小值
  - `maxValue`: 最大值
  - `startTime`: 开始时间（格式：yyyy-MM-dd HH:mm:ss）

- **停止数据生成**

  ```
  POST /api/data-generation/stop
  ```

  **请求参数**：

  - `deviceId`: 设备ID

- **更新数据**

  ```
  POST /api/data/update
  ```

  **请求参数**：

  - `macAddress`: 设备MAC地址
  - `channel`: 通信通道
  - `duration`: 持续时间

### 数据查询

- **按时间范围查询设备数据**

  ```
  GET /api/devices/{id}/data
  ```

  **请求参数**：

  - `startTime`: 开始时间（格式：yyyy-MM-dd HH:mm:ss）
  - `endTime`: 结束时间（格式：yyyy-MM-dd HH:mm:ss）
  - `page`: 页码（默认0）
  - `size`: 每页大小（默认10）

- **添加设备数据**

  ```
  POST /api/devices/{id}/data
  ```

  **请求参数**：

  - `recordTime`: 记录时间（格式：yyyy-MM-dd HH:mm:ss）
  - `value`: 数据值

- **查询超过阈值的设备数据**

  ```
  GET /api/devices/{id}/data/above-threshold
  ```

## WebSocket 实时通知

系统通过 WebSocket 服务器向所有连接的客户端发送警报消息。当设备数据超过设定的阈值时，客户端将接收到警报。

- **WebSocket 端点**

  ```
  ws://localhost:8080/ws/alerts
  ```

- **连接示例**

  ```javascript
  const socket = new WebSocket('ws://localhost:8080/ws/alerts');

  socket.onmessage = function(event) {
      console.log('警报消息:', event.data);
  };

  socket.onopen = function() {
      console.log('已连接到 WebSocket 服务器');
  };

  socket.onclose = function() {
      console.log('WebSocket 连接已关闭');
  };
  ```

## 数据库设计

### 用户表 `users`

| 字段       | 类型          | 描述     |
| ---------- | ------------- | -------- |
| id         | BIGINT        | 主键，自增 |
| username   | VARCHAR(255)  | 用户名，唯一 |
| password   | VARCHAR(255)  | 密码     |

### 设备表 `devices`

| 字段               | 类型          | 描述              |
| ------------------ | ------------- | ----------------- |
| id                 | BIGINT        | 主键，自增        |
| name               | VARCHAR(255)  | 设备名称          |
| macAddress         | VARCHAR(255)  | 设备 MAC 地址，唯一 |
| communicationChannel | VARCHAR(255) | 通信通道          |
| threshold          | DOUBLE        | 数据阈值          |
| isOn               | BOOLEAN       | 设备状态（开/关）  |

### 设备数据表 `device_data`

| 字段        | 类型            | 描述           |
| ----------- | --------------- | -------------- |
| id          | BIGINT          | 主键，自增     |
| value       | VARCHAR(255)    | 数据值         |
| recordTime  | TIMESTAMP       | 记录时间       |
| device_id   | BIGINT          | 外键，关联设备 |

## 测试

项目包含基本的单元测试，使用 JUnit 进行测试。

运行测试命令：

```bash
mvn test
```

## 配置说明

在 `src/main/resources/application.properties` 文件中，可以配置以下参数：

- **数据库配置**

  ```properties
  spring.datasource.url=jdbc:mysql://localhost:3305/backpro?useSSL=false&serverTimezone=UTC
  spring.datasource.username=root
  spring.datasource.password=123456
  spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
  ```

- **JPA 配置**

  ```properties
  spring.jpa.hibernate.ddl-auto=update
  spring.jpa.show-sql=true
  spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL57Dialect
  spring.jpa.open-in-view=true
  spring.jpa.properties.hibernate.jdbc.time_zone=Asia/Shanghai
  ```

- **Jackson 配置**

  ```properties
  spring.jackson.date-format=yyyy-MM-dd HH:mm:ss
  spring.jackson.time-zone=Asia/Shanghai
  ```

- **日志配置**

  ```properties
  logging.level.org.example.backpro=DEBUG
  ```

## 使用示例

### 注册与登录

1. **注册用户**

   发送 `POST` 请求到 `/api/users/register`，包含用户名和密码。

2. **登录用户**

   发送 `POST` 请求到 `/api/users/login`，包含用户名和密码，成功后可进行后续操作。

### 设备管理

1. **添加设备**

   发送 `POST` 请求到 `/api/devices`，包含设备名称、MAC 地址、通信通道等信息。

2. **启动数据生成**

   发送 `POST` 请求到 `/api/data-generation/start`，指定设备ID、持续时间、间隔时间等参数，系统将开始生成并存储设备数据。

3. **实时接收警报**

   客户端通过 WebSocket 连接到 `ws://localhost:8080/ws/alerts`，监听实时警报消息。
