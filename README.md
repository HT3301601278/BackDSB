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
   git clone https://github.com/HT3301601278/BackPro.git
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

## API文档

### 1. 用户管理

#### 1.1 用户注册

- **URL**: `/api/users/register`

- **方法**: POST

- **描述**: 注册新用户

- **请求体**:

  ```json
  {
    "username": "string",
    "password": "string"
  }
  ```

- **成功响应**: 

  - 状态码: 200 OK
  - 响应体: 注册成功的用户信息

- **错误响应**:

  - 状态码: 400 Bad Request
  - 响应体: 错误信息字符串

**测试用例**:

1. 注册新用户:

   ```
   POST http://localhost:8080/api/users/register
   Content-Type: application/json
   
   {
     "username": "testuser",
     "password": "password123"
   }
   ```

   预期结果: 200 OK,返回包含用户ID的用户信息

2. 尝试注册已存在的用户名:

   ```
   POST http://localhost:8080/api/users/register
   Content-Type: application/json
   
   {
     "username": "testuser",
     "password": "anotherpassword"
   }
   ```

   预期结果: 400 Bad Request,返回"用户名已存在"错误信息

#### 1.2 用户登录

- **URL**: `/api/users/login`

- **方法**: POST

- **描述**: 用户登录

- **请求体**:

  ```json
  {
    "username": "string",
    "password": "string"
  }
  ```

- **成功响应**: 

  - 状态码: 200 OK
  - 响应体: 登录成功的用户信息

- **错误响应**:

  - 状态码: 400 Bad Request
  - 响应体: 空

**测试用例**:

1. 正确的用户名和密码:

   ```
   POST http://localhost:8080/api/users/login
   Content-Type: application/json
   
   {
     "username": "testuser",
     "password": "password123"
   }
   ```

   预期结果: 200 OK,返回用户信息

2. 错误的密码:

   ```
   POST http://localhost:8080/api/users/login
   Content-Type: application/json
   
   {
     "username": "testuser",
     "password": "wrongpassword"
   }
   ```

   预期结果: 400 Bad Request,无响应体

#### 1.3 修改密码

- **URL**: `/api/users/{id}/password`
- **方法**: PUT
- **描述**: 修改用户密码
- **路径参数**: 
  - `id`: 用户ID
- **查询参数**:
  - `oldPassword`: 旧密码
  - `newPassword`: 新密码
- **成功响应**: 
  - 状态码: 200 OK
  - 响应体: 更新后的用户信息
- **错误响应**:
  - 状态码: 400 Bad Request (旧密码不正确)
  - 状态码: 404 Not Found (用户不存在)
  - 响应体: 错误信息字符串

**测试用例**:

1. 正确修改密码:

   ```
   PUT http://localhost:8080/api/users/1/password?oldPassword=password123&newPassword=newpassword123
   ```

   预期结果: 200 OK,返回更新后的用户信息

2. 旧密码错误:

   ```
   PUT http://localhost:8080/api/users/1/password?oldPassword=wrongpassword&newPassword=newpassword123
   ```

   预期结果: 400 Bad Request,返回"旧密码不正确"错误信息

3. 用户不存在:

   ```
   PUT http://localhost:8080/api/users/999/password?oldPassword=password123&newPassword=newpassword123
   ```

   预期结果: 404 Not Found

### 2. 设备管理

#### 2.1 添加设备

- **URL**: `/api/devices`

- **方法**: POST

- **描述**: 添加新设备

- **请求体**:

  ```json
  {
    "name": "string",
    "macAddress": "string",
    "communicationChannel": "string"
  }
  ```

- **成功响应**: 

  - 状态码: 200 OK
  - 响应体: 新添加的设备信息

- **错误响应**:

  - 状态码: 400 Bad Request
  - 响应体: 错误信息字符串

**测试用例**:

1. 添加新设备:

   ```
   POST http://localhost:8080/api/devices
   Content-Type: application/json
   
   {
     "name": "Test Device",
     "macAddress": "00:11:22:33:44:55",
     "communicationChannel": "WiFi"
   }
   ```

   预期结果: 200 OK,返回新添加的设备信息

2. 添加重复的MAC地址设备:

   ```
   POST http://localhost:8080/api/devices
   Content-Type: application/json
   
   {
     "name": "Another Device",
     "macAddress": "00:11:22:33:44:55",
     "communicationChannel": "Bluetooth"
   }
   ```

   预期结果: 400 Bad Request,返回错误信息

#### 2.2 删除设备

- **URL**: `/api/devices/{id}`
- **方法**: DELETE
- **描述**: 删除指定ID的设备
- **路径参数**: 
  - `id`: 设备ID
- **成功响应**: 
  - 状态码: 200 OK
- **错误响应**:
  - 状态码: 404 Not Found
  - 响应体: 错误信息字符串

**测试用例**:

1. 删除存在的设备:

   ```
   DELETE http://localhost:8080/api/devices/1
   ```

   预期结果: 200 OK

2. 删除不存在的设备:

   ```
   DELETE http://localhost:8080/api/devices/999
   ```

   预期结果: 404 Not Found

#### 2.3 获取所有设备(分页)

- **URL**: `/api/devices`
- **方法**: GET
- **描述**: 获取所有设备信息(分页)
- **查询参数**:
  - `page`: 页码 (默认值: 0)
  - `size`: 每页大小 (默认值: 10)
- **成功响应**: 
  - 状态码: 200 OK
  - 响应体: 设备信息列表和分页信息
- **错误响应**:
  - 状态码: 400 Bad Request
  - 响应体: 错误信息字符串

**测试用例**:

1. 获取第一页设备信息:

   ```
   GET http://localhost:8080/api/devices?page=0&size=10
   ```

   预期结果: 200 OK,返回设备列表和分页信息

2. 获取第二页设备信息:

   ```
   GET http://localhost:8080/api/devices?page=1&size=5
   ```

   预期结果: 200 OK,返回设备列表和分页信息

#### 2.4 获取单个设备信息

- **URL**: `/api/devices/{id}`
- **方法**: GET
- **描述**: 获取指定ID的设备信息
- **路径参数**: 
  - `id`: 设备ID
- **成功响应**: 
  - 状态码: 200 OK
  - 响应体: 设备信息
- **错误响应**:
  - 状态码: 404 Not Found
  - 响应体: 错误信息字符串

**测试用例**:

1. 获取存在的设备信息:

   ```
   GET http://localhost:8080/api/devices/1
   ```

   预期结果: 200 OK,返回设备信息

2. 获取不存在的设备信息:

   ```
   GET http://localhost:8080/api/devices/999
   ```

   预期结果: 404 Not Found

#### 2.5 更新设备信息

- **URL**: `/api/devices/{id}`

- **方法**: PUT

- **描述**: 更新指定ID的设备信息

- **路径参数**: 

  - `id`: 设备ID

- **请求体**:

  ```json
  {
    "name": "string",
    "macAddress": "string",
    "communicationChannel": "string"
  }
  ```

- **成功响应**: 

  - 状态码: 200 OK
  - 响应体: 更新后的设备信息

- **错误响应**:

  - 状态码: 404 Not Found
  - 响应体: 错误信息字符串

**测试用例**:

1. 更新存在的设备信息:

   ```
   PUT http://localhost:8080/api/devices/1
   Content-Type: application/json
   
   {
     "name": "Updated Device",
     "macAddress": "AA:BB:CC:DD:EE:FF",
     "communicationChannel": "5G"
   }
   ```

   预期结果: 200 OK,返回更新后的设备信息

2. 更新不存在的设备信息:

   ```
   PUT http://localhost:8080/api/devices/999
   Content-Type: application/json
   
   {
     "name": "Non-existent Device",
     "macAddress": "11:22:33:44:55:66",
     "communicationChannel": "4G"
   }
   ```

   预期结果: 404 Not Found

#### 2.6 设置设备阈值

- **URL**: `/api/devices/{id}/threshold`
- **方法**: PUT
- **描述**: 设置指定ID设备的阈值
- **路径参数**: 
  - `id`: 设备ID
- **查询参数**:
  - `threshold`: 阈值
- **成功响应**: 
  - 状态码: 200 OK
  - 响应体: 更新后的设备信息
- **错误响应**:
  - 状态码: 404 Not Found
  - 响应体: 错误信息字符串

**测试用例**:

1. 设置存在设备的阈值:

   ```
   PUT http://localhost:8080/api/devices/1/threshold?threshold=50.5
   ```

   预期结果: 200 OK,返回更新后的设备信息

2. 设置不存在设备的阈值:

   ```
   PUT http://localhost:8080/api/devices/999/threshold?threshold=30.0
   ```

   预期结果: 404 Not Found

#### 2.7 切换设备开关状态

- **URL**: `/api/devices/{id}/toggle`
- **方法**: PUT
- **描述**: 切换指定ID设备的开关状态
- **路径参数**: 
  - `id`: 设备ID
- **成功响应**: 
  - 状态码: 200 OK
  - 响应体: 更新后的设备信息
- **错误响应**:
  - 状态码: 404 Not Found
  - 响应体: 错误信息字符串

**测试用例**:

1. 切换存在设备的状态:

   ```
   PUT http://localhost:8080/api/devices/1/toggle
   ```

   预期结果: 200 OK,返回更新后的设备信息

2. 切换不存在设备的状态:

   ```
   PUT http://localhost:8080/api/devices/999/toggle
   ```

   预期结果: 404 Not Found

#### 2.8 获取所有设备列表(不分页)

- **URL**: `/api/devices/list`
- **方法**: GET
- **描述**: 获取所有设备信息(不分页)
- **成功响应**: 
  - 状态码: 200 OK
  - 响应体: 所有设备信息列表
- **错误响应**:
  - 状态码: 500 Internal Server Error
  - 响应体: 错误信息字符串

**测试用例**:

1. 获取所有设备列表:

   ```
   GET http://localhost:8080/api/devices/list
   ```

   预期结果: 200 OK,返回所有设备信息列表

### 3. 设备数据管理

#### 3.1 获取设备数据(按时间范围)

- **URL**: `/api/devices/{id}/data`
- **方法**: GET
- **描述**: 获取指定ID设备在给定时间范围内的数据
- **路径参数**: 
  - `id`: 设备ID
- **查询参数**:
  - `startTime`: 开始时间 (格式: yyyy-MM-dd HH:mm:ss)
  - `endTime`: 结束时间 (格式: yyyy-MM-dd HH:mm:ss)
  - `page`: 页码 (默认值: 0)
  - `size`: 每页大小 (默认值: 10)
- **成功响应**: 
  - 状态码: 200 OK
  - 响应体: 设备数据列表和分页信息
- **错误响应**:
  - 状态码: 404 Not Found
  - 响应体: 错误信息字符串

**测试用例**:

1. 获取存在设备的数据:

   ```
   GET http://localhost:8080/api/devices/1/data?startTime=2023-01-01 00:00:00&endTime=2023-12-31 23:59:59&page=0&size=10
   ```

   预期结果: 200 OK,返回设备数据列表和分页信息

2. 获取不存在设备的数据:

   ```
   GET http://localhost:8080/api/devices/999/data?startTime=2023-01-01 00:00:00&endTime=2023-12-31 23:59:59&page=0&size=10
   ```

   预期结果: 404 Not Found

### 4. 数据更新

#### 4.1 更新设备数据

- **URL**: `/api/data/update`
- **方法**: POST
- **描述**: 从外部API获取并更新设备数据
- **查询参数**:
  - `macAddress`: 设备MAC地址
  - `channel`: 通信通道
  - `duration`: 数据持续时间
- **成功响应**: 
  - 状态码: 200 OK
  - 响应体: "数据更新成功"
- **错误响应**:
  - 状态码: 404 Not Found (设备未找到)
  - 状态码: 500 Internal Server Error (其他错误)
  - 响应体: 错误信息字符串

**测试用例**:

1. 更新存在设备的数据:

   ```
   POST http://localhost:8080/api/data/update?macAddress=00:11:22:33:44:55&channel=WiFi&duration=1h
   ```

   预期结果: 200 OK, 返回"数据更新成功"

2. 更新不存在设备的数据:

   ```
   POST http://localhost:8080/api/data/update?macAddress=99:99:99:99:99:99&channel=WiFi&duration=1h
   ```

   预期结果: 404 Not Found, 返回错误信息

### 5. 数据生成

#### 5.1 开始数据生成

- **URL**: `/api/data-generation/start`
- **方法**: POST
- **描述**: 开始为指定设备生成模拟数据
- **查询参数**:
  - `deviceId`: 设备ID
  - `durationMinutes`: 生成数据的持续时间（分钟）
  - `intervalSeconds`: 数据生成的时间间隔（秒）
  - `minValue`: 生成数据的最小值
  - `maxValue`: 生成数据的最大值
  - `startTime`: 开始生成数据的时间（格式: yyyy-MM-dd HH:mm:ss）
- **成功响应**: 
  - 状态码: 200 OK
  - 响应体: "数据生成任务已启动"
- **错误响应**:
  - 状态码: 404 Not Found (设备未找到)
  - 状态码: 500 Internal Server Error (其他错误)
  - 响应体: 错误信息字符串

**测试用例**:

1. 为存在的设备开始数据生成:

   ```
   POST http://localhost:8080/api/data-generation/start?deviceId=1&durationMinutes=60&intervalSeconds=5&minValue=0&maxValue=100&startTime=2023-06-01 10:00:00
   ```

   预期结果: 200 OK, 返回"数据生成任务已启动"

2. 为不存在的设备开始数据生成:

   ```
   POST http://localhost:8080/api/data-generation/start?deviceId=999&durationMinutes=60&intervalSeconds=5&minValue=0&maxValue=100&startTime=2023-06-01 10:00:00
   ```

   预期结果: 404 Not Found

#### 5.2 停止数据生成

- **URL**: `/api/data-generation/stop`
- **方法**: POST
- **描述**: 停止指定设备的数据生成任务
- **查询参数**:
  - `deviceId`: 设备ID
- **成功响应**: 
  - 状态码: 200 OK
  - 响应体: "数据生成任务已停止"
- **错误响应**:
  - 状态码: 500 Internal Server Error
  - 响应体: 错误信息字符串

**测试用例**:

1. 停止存在设备的数据生成:

   ```
   POST http://localhost:8080/api/data-generation/stop?deviceId=1
   ```

   预期结果: 200 OK, 返回"数据生成任务已停止"

2. 停止不存在设备的数据生成:

   ```
   POST http://localhost:8080/api/data-generation/stop?deviceId=999
   ```

   预期结果: 200 OK, 返回"数据生成任务已停止"（即使设备不存在，也不会报错）

### 6. WebSocket 警报

- **URL**: `ws://localhost:8080/ws/alerts`
- **描述**: 建立WebSocket连接以接收实时警报

**测试用例**:

1. 使用WebSocket客户端（如浏览器的JavaScript）连接到 `ws://localhost:8080/ws/alerts`
2. 触发一个警报（例如，通过数据生成使设备数值超过阈值）
3. 验证WebSocket客户端是否收到警报消息

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

| 表名 | 字段名 | 数据类型 | 描述 |
|------|--------|----------|------|
| devices | id | Long | 设备ID，主键 |
| | name | String | 设备名称 |
| | mac_address | String | 设备MAC地址 |
| | communication_channel | String | 通信通道 |
| | threshold | Double | 阈值 |
| | is_on | Boolean | 设备是否开启 |
| device_data | id | Long | 设备数据ID，主键 |
| | value | String | 数据值 |
| | record_time | Timestamp | 记录时间 |
| | device_id | Long | 关联的设备ID，外键 |
| users | id | Long | 用户ID，主键 |
| | username | String | 用户名，唯一 |
| | password | String | 密码 |

这个数据库结构反映了项目中的三个主要实体：设备（Device）、设备数据（DeviceData）和用户（User）。以下是对每个表的详细说明：

1. devices 表：
   - 存储设备的基本信息
   - 包含设备的唯一标识、名称、MAC地址、通信通道等
   - threshold 字段用于存储设备的阈值，用于数据监控
   - is_on 字段表示设备的开启状态

2. device_data 表：
   - 存储设备产生的数据
   - 每条记录包含数据值、记录时间和关联的设备ID
   - 通过 device_id 外键与 devices 表关联

3. users 表：
   - 存储用户信息
   - 包含用户的唯一标识、用户名和密码
   - username 字段设置为唯一，确保用户名不重复

这个数据库结构支持了项目中的设备管理、数据收集和用户认证功能。设备可以生成多条数据记录，而每条数据记录都与特定设备关联。用户表则用于管理系统的访问权限。

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