# BackPro 项目

## 项目简介

BackPro是一个基于Spring Boot的后端项目,主要用于设备数据管理和监控。该项目提供了设备数据的采集、存储、查询和警报功能,同时还包含用户管理和认证功能。项目采用RESTful API设计,并使用WebSocket实现实时警报推送。

## 技术栈

- Java 8+
- Spring Boot 2.x
- Spring Data JPA
- MySQL
- WebSocket
- Maven

## 项目结构

项目采用标准的Maven项目结构,主要包含以下几个部分:

```
src/main/java/org/example/backpro/
├── BackProApplication.java (应用程序入口点)
├── config/
│   ├── CorsConfig.java (跨域配置)
│   └── WebSocketConfig.java (WebSocket配置)
├── controller/
│   ├── AlertController.java (警报相关API)
│   ├── DataGenerationController.java (数据生成相关API)
│   ├── DataUpdateController.java (数据更新相关API)
│   ├── DeviceController.java (设备相关API)
│   └── UserController.java (用户相关API)
├── entity/
│   ├── Alert.java (警报实体)
│   ├── Device.java (设备实体)
│   ├── DeviceData.java (设备数据实体)
│   └── User.java (用户实体)
├── exception/
│   ├── GlobalExceptionHandler.java (全局异常处理)
│   └── ResourceNotFoundException.java (资源未找到异常)
├── repository/
│   ├── AlertRepository.java (警报数据访问接口)
│   ├── DeviceDataRepository.java (设备数据访问接口)
│   ├── DeviceRepository.java (设备数据访问接口)
│   └── UserRepository.java (用户数据访问接口)
├── service/
│   ├── AlertService.java (警报业务逻辑)
│   ├── DataGenerationService.java (数据生成业务逻辑)
│   ├── DataUpdateService.java (数据更新业务逻辑)
│   ├── DeviceService.java (设备业务逻辑)
│   └── UserService.java (用户业务逻辑)
└── websocket/
    └── AlertWebSocketHandler.java (WebSocket处理器)
```

## 核心功能详解

1. 用户管理
   - 用户注册: 通过UserController和UserService实现,支持用户名和密码的注册。
   - 用户登录: 实现基本的用户名和密码验证。
   - 修改密码: 允许用户修改自己的密码。

2. 设备管理
   - 添加设备: 通过DeviceController和DeviceService实现,支持添加新的设备信息。
   - 删除设备: 允许删除指定ID的设备。
   - 更新设备信息: 支持更新设备的名称、MAC地址等信息。
   - 设置设备阈值: 可以为设备设置数据阈值,用于触发警报。
   - 切换设备状态: 支持开启/关闭设备。

3. 数据管理
   - 采集设备数据: 通过DataUpdateService实现,支持从外部API获取设备数据并保存。
   - 查询设备数据: 支持按时间范围查询设备数据。
   - 生成模拟数据: 通过DataGenerationService实现,可以生成模拟的设备数据用于测试。

4. 警报管理
   - 设置阈值警报: 当设备数据超过预设阈值时,自动生成警报。
   - 实时推送警报信息: 使用WebSocket技术,实时向客户端推送警报信息。
   - 查询历史警报: 支持按设备和时间范围查询历史警报信息。

## 关键类说明

1. BackProApplication.java
   - 项目的入口点,包含main方法。
   - 使用@SpringBootApplication注解,自动配置Spring Boot应用。
   - 使用@EnableAsync注解,启用异步方法支持。

2. DeviceController.java
   - 处理所有与设备相关的HTTP请求。
   - 提供了添加、删除、更新、查询设备的API端点。
   - 还包括设置设备阈值、切换设备状态等功能。

3. DataGenerationService.java
   - 负责生成模拟的设备数据。
   - 使用@Async注解实现异步数据生成。
   - 支持定时生成数据,并可以手动停止数据生成任务。

4. AlertWebSocketHandler.java
   - 处理WebSocket连接,用于实时推送警报信息。
   - 维护了一个活跃的WebSocket会话列表。
   - 提供了向所有连接的客户端发送警报的方法。

5. GlobalExceptionHandler.java
   - 全局异常处理器,统一处理应用中抛出的异常。
   - 为不同类型的异常定义了相应的HTTP响应。

## 数据库设计

项目使用MySQL数据库,主要包含以下几个表:

1. users: 存储用户信息
   - id: 主键
   - username: 用户名(唯一)
   - password: 密码

2. devices: 存储设备信息
   - id: 主键
   - name: 设备名称
   - mac_address: MAC地址
   - communication_channel: 通信通道
   - threshold: 阈值
   - is_on: 设备状态

3. device_data: 存储设备采集的数据
   - id: 主键
   - device_id: 外键,关联devices表
   - value: 数据值
   - record_time: 记录时间

4. alerts: 存储警报信息
   - id: 主键
   - device_id: 外键,关联devices表
   - message: 警报消息
   - timestamp: 警报时间

## API文档

### 基础URL

所有API都以 `http://localhost:8080/api` 为基础URL。

### 用户相关API

#### 1. 用户注册

- **URL**: `/users/register`

- **方法**: POST

- **描述**: 注册新用户

- **请求体**:

  ```json
  {
    "username": "newuser",
    "password": "password123"
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
   curl -X POST http://localhost:8080/api/users/register -H "Content-Type: application/json" -d '{"username":"newuser","password":"password123"}'
   ```

2. 尝试注册已存在的用户名:

   ```
   curl -X POST http://localhost:8080/api/users/register -H "Content-Type: application/json" -d '{"username":"existinguser","password":"password123"}'
   ```

#### 2. 用户登录

- **URL**: `/users/login`

- **方法**: POST

- **描述**: 用户登录

- **请求体**:

  ```json
  {
    "username": "existinguser",
    "password": "password123"
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
   curl -X POST http://localhost:8080/api/users/login -H "Content-Type: application/json" -d '{"username":"existinguser","password":"password123"}'
   ```

2. 错误的密码:

   ```
   curl -X POST http://localhost:8080/api/users/login -H "Content-Type: application/json" -d '{"username":"existinguser","password":"wrongpassword"}'
   ```

#### 3. 修改用户密码

- **URL**: `/users/{id}/password`
- **方法**: PUT
- **描述**: 修改用户密码
- **路径参数**: 
  - `id`: 用户ID
- **请求参数**:
  - `oldPassword`: 旧密码
  - `newPassword`: 新密码
- **成功响应**: 
  - 状态码: 200 OK
  - 响应体: 更新后的用户信息
- **错误响应**:
  - 状态码: 400 Bad Request (密码错误)
  - 状态码: 404 Not Found (用户不存在)

**测试用例**:

1. 正确的旧密码:

   ```
   curl -X PUT "http://localhost:8080/api/users/1/password?oldPassword=password123&newPassword=newpassword123"
   ```

2. 错误的旧密码:

   ```
   curl -X PUT "http://localhost:8080/api/users/1/password?oldPassword=wrongpassword&newPassword=newpassword123"
   ```

3. 不存在的用户ID:

   ```
   curl -X PUT "http://localhost:8080/api/users/999/password?oldPassword=password123&newPassword=newpassword123"
   ```

### 设备相关API

#### 1. 添加设备

- **URL**: `/devices`

- **方法**: POST

- **描述**: 添加新设备

- **请求体**:

  ```json
  {
    "name": "New Device",
    "macAddress": "00:11:22:33:44:55",
    "communicationChannel": "WiFi"
  }
  ```

- **成功响应**: 

  - 状态码: 200 OK
  - 响应体: 添加成功的设备信息

**测试用例**:

1. 添加新设备:

   ```
   curl -X POST http://localhost:8080/api/devices -H "Content-Type: application/json" -d '{"name":"New Device","macAddress":"00:11:22:33:44:55","communicationChannel":"WiFi"}'
   ```

#### 2. 删除设备

- **URL**: `/devices/{id}`
- **方法**: DELETE
- **描述**: 删除指定ID的设备
- **路径参数**: 
  - `id`: 设备ID
- **成功响应**: 
  - 状态码: 200 OK

**测试用例**:

1. 删除存在的设备:

   ```
   curl -X DELETE http://localhost:8080/api/devices/1
   ```

2. 删除不存在的设备:

   ```
   curl -X DELETE http://localhost:8080/api/devices/999
   ```

#### 3. 获取所有设备（分页）

- **URL**: `/devices`
- **方法**: GET
- **描述**: 获取所有设备，支持分页
- **请求参数**:
  - `page`: 页码 (默认值: 0)
  - `size`: 每页大小 (默认值: 10)
- **成功响应**: 
  - 状态码: 200 OK
  - 响应体: 设备列表和分页信息

**测试用例**:

1. 获取第一页设备:

   ```
   curl http://localhost:8080/api/devices?page=0&size=10
   ```

2. 获取第二页设备:

   ```
   curl http://localhost:8080/api/devices?page=1&size=10
   ```

#### 4. 获取单个设备

- **URL**: `/devices/{id}`
- **方法**: GET
- **描述**: 获取指定ID的设备信息
- **路径参数**: 
  - `id`: 设备ID
- **成功响应**: 
  - 状态码: 200 OK
  - 响应体: 设备信息
- **错误响应**:
  - 状态码: 404 Not Found

**测试用例**:

1. 获取存在的设备:

   ```
   curl http://localhost:8080/api/devices/1
   ```

2. 获取不存在的设备:

   ```
   curl http://localhost:8080/api/devices/999
   ```

#### 5. 更新设备信息

- **URL**: `/devices/{id}`

- **方法**: PUT

- **描述**: 更新指定ID的设备信息

- **路径参数**: 

  - `id`: 设备ID

- **请求体**:

  ```json
  {
    "name": "Updated Device",
    "macAddress": "55:44:33:22:11:00",
    "communicationChannel": "Bluetooth"
  }
  ```

- **成功响应**: 

  - 状态码: 200 OK
  - 响应体: 更新后的设备信息

- **错误响应**:

  - 状态码: 404 Not Found

**测试用例**:

1. 更新存在的设备:

   ```
   curl -X PUT http://localhost:8080/api/devices/1 -H "Content-Type: application/json" -d '{"name":"Updated Device","macAddress":"55:44:33:22:11:00","communicationChannel":"Bluetooth"}'
   ```

2. 更新不存在的设备:

   ```
   curl -X PUT http://localhost:8080/api/devices/999 -H "Content-Type: application/json" -d '{"name":"Updated Device","macAddress":"55:44:33:22:11:00","communicationChannel":"Bluetooth"}'
   ```

#### 6. 设置设备阈值

- **URL**: `/devices/{id}/threshold`
- **方法**: PUT
- **描述**: 设置指定ID设备的阈值
- **路径参数**: 
  - `id`: 设备ID
- **请求参数**:
  - `threshold`: 新的阈值
- **成功响应**: 
  - 状态码: 200 OK
  - 响应体: 更新后的设备信息
- **错误响应**:
  - 状态码: 404 Not Found

**测试用例**:

1. 设置存在设备的阈值:

   ```
   curl -X PUT "http://localhost:8080/api/devices/1/threshold?threshold=50.5"
   ```

2. 设置不存在设备的阈值:

   ```
   curl -X PUT "http://localhost:8080/api/devices/999/threshold?threshold=50.5"
   ```

#### 7. 切换设备开关状态

- **URL**: `/devices/{id}/toggle`
- **方法**: PUT
- **描述**: 切换指定ID设备的开关状态
- **路径参数**: 
  - `id`: 设备ID
- **成功响应**: 
  - 状态码: 200 OK
  - 响应体: 更新后的设备信息
- **错误响应**:
  - 状态码: 404 Not Found

**测试用例**:

1. 切换存在设备的状态:

   ```
   curl -X PUT http://localhost:8080/api/devices/1/toggle
   ```

2. 切换不存在设备的状态:

   ```
   curl -X PUT http://localhost:8080/api/devices/999/toggle
   ```

#### 8. 获取设备数据（分页）

- **URL**: `/devices/{id}/data`
- **方法**: GET
- **描述**: 获取指定ID设备在指定时间范围内的数据，支持分页
- **路径参数**: 
  - `id`: 设备ID
- **请求参数**:
  - `startTime`: 开始时间 (格式: yyyy-MM-dd HH:mm:ss)
  - `endTime`: 结束时间 (格式: yyyy-MM-dd HH:mm:ss)
  - `page`: 页码 (默认值: 0)
  - `size`: 每页大小 (默认值: 10)
- **成功响应**: 
  - 状态码: 200 OK
  - 响应体: 设备数据列表和分页信息
- **错误响应**:
  - 状态码: 404 Not Found

**测试用例**:

1. 获取存在设备的数据:

   ```
   curl "http://localhost:8080/api/devices/1/data?startTime=2023-01-01%2000:00:00&endTime=2023-12-31%2023:59:59&page=0&size=10"
   ```

2. 获取不存在设备的数据:

   ```
   curl "http://localhost:8080/api/devices/999/data?startTime=2023-01-01%2000:00:00&endTime=2023-12-31%2023:59:59&page=0&size=10"
   ```

#### 9. 添加设备数据

- **URL**: `/devices/{id}/data`
- **方法**: POST
- **描述**: 为指定ID的设备添加数据
- **路径参数**: 
  - `id`: 设备ID
- **请求参数**:
  - `recordTime`: 记录时间 (格式: yyyy-MM-dd HH:mm:ss)
  - `value`: 数据值
- **成功响应**: 
  - 状态码: 200 OK
  - 响应体: 添加的设备数据信息
- **错误响应**:
  - 状态码: 404 Not Found

**测试用例**:

1. 为存在的设备添加数据:

   ```
   curl -X POST "http://localhost:8080/api/devices/1/data?recordTime=2023-06-01%2012:00:00&value=75.5"
   ```

2. 为不存在的设备添加数据:

   ```
   curl -X POST "http://localhost:8080/api/devices/999/data?recordTime=2023-06-01%2012:00:00&value=75.5"
   ```

#### 10. 获取超过阈值的设备数据

- **URL**: `/devices/{id}/data/above-threshold`
- **方法**: GET
- **描述**: 获取指定ID设备超过阈值的所有数据
- **路径参数**: 
  - `id`: 设备ID
- **成功响应**: 
  - 状态码: 200 OK
  - 响应体: 超过阈值的设备数据列表
- **错误响应**:
  - 状态码: 404 Not Found

**测试用例**:

1. 获取存在设备的超阈值数据:

   ```
   curl http://localhost:8080/api/devices/1/data/above-threshold
   ```

2. 获取不存在设备的超阈值数据:

   ```
   curl http://localhost:8080/api/devices/999/data/above-threshold
   ```

#### 11. 获取所有设备列表（不分页）

- **URL**: `/devices/list`
- **方法**: GET
- **描述**: 获取所有设备的列表，不分页
- **成功响应**: 
  - 状态码: 200 OK
  - 响应体: 所有设备的列表

**测试用例**:

1. 获取所有设备列表:

   ```
   curl http://localhost:8080/api/devices/list
   ```

### 数据生成相关API

#### 1. 开始数据生成

- **URL**: `/api/data-generation/start`
- **方法**: POST
- **描述**: 开始为指定设备生成数据
- **请求参数**:
  - `deviceId`: 设备ID
  - `durationMinutes`: 生成数据的持续时间（分钟）
  - `intervalSeconds`: 数据生成的时间间隔（秒）
  - `minValue`: 生成数据的最小值
  - `maxValue`: 生成数据的最大值
  - `startTime`: 开始生成数据的时间（格式：yyyy-MM-dd HH:mm:ss）
- **成功响应**: 
  - 状态码: 200 OK
  - 响应体: "数据生成任务已启动"
- **错误响应**:
  - 状态码: 404 Not Found

**测试用例**:

1. 开始为存在的设备生成数据:

   ```
   curl -X POST "http://localhost:8080/api/data-generation/start?deviceId=1&durationMinutes=60&intervalSeconds=30&minValue=0&maxValue=100&startTime=2023-06-01%2012:00:00"
   ```

2. 尝试为不存在的设备生成数据:

   ```
   curl -X POST "http://localhost:8080/api/data-generation/start?deviceId=999&durationMinutes=60&intervalSeconds=30&minValue=0&maxValue=100&startTime=2023-06-01%2012:00:00"
   ```

#### 2. 停止数据生成

- **URL**: `/api/data-generation/stop`
- **方法**: POST
- **描述**: 停止指定设备的数据生成
- **请求参数**:
  - `deviceId`: 设备ID
- **成功响应**: 
  - 状态码: 200 OK
  - 响应体: "数据生成任务已停止"
- **错误响应**:
  - 状态码: 500 Internal Server Error

**测试用例**:

1. 停止存在设备的数据生成:

   ```
   curl -X POST "http://localhost:8080/api/data-generation/stop?deviceId=1"
   ```

2. 尝试停止不存在设备的数据生成:

   ```
   curl -X POST "http://localhost:8080/api/data-generation/stop?deviceId=999"
   ```

#### 3. 测试数据保存

- **URL**: `/api/data-generation/test-save`
- **方法**: POST
- **描述**: 测试为指定设备保存一条数据
- **请求参数**:
  - `deviceId`: 设备ID
- **成功响应**: 
  - 状态码: 200 OK
  - 响应体: "测试数据保存完成，请检查日志和数据库"

**测试用例**:

1. 为存在的设备测试保存数据:

   ```
   curl -X POST "http://localhost:8080/api/data-generation/test-save?deviceId=1"
   ```

2. 为不存在的设备测试保存数据:

   ```
   curl -X POST "http://localhost:8080/api/data-generation/test-save?deviceId=999"
   ```

### 数据更新相关API

#### 1. 更新设备数据

- **URL**: `/api/data/update`
- **方法**: POST
- **描述**: 从外部API获取并更新设备数据
- **请求参数**:
  - `macAddress`: 设备MAC地址
  - `channel`: 通信通道
  - `duration`: 数据持续时间
- **成功响应**: 
  - 状态码: 200 OK
  - 响应体: "数据更新成功"

**测试用例**:

1. 更新存在设备的数据:

   ```
   curl -X POST "http://localhost:8080/api/data/update?macAddress=00:11:22:33:44:55&channel=WiFi&duration=1h"
   ```

2. 尝试更新不存在设备的数据:

   ```
   curl -X POST "http://localhost:8080/api/data/update?macAddress=99:99:99:99:99:99&channel=WiFi&duration=1h"
   ```

### 警报相关API

#### 1. 获取所有警报

- **URL**: `/api/alerts`
- **方法**: GET
- **描述**: 获取所有警报，支持分页
- **请求参数**:
  - `page`: 页码 (默认值: 0)
  - `size`: 每页大小 (默认值: 10)
- **成功响应**: 
  - 状态码: 200 OK
  - 响应体: 警报列表和分页信息

**测试用例**:

1. 获取第一页警报:

   ```
   curl "http://localhost:8080/api/alerts?page=0&size=10"
   ```

2. 获取第二页警报:

   ```
   curl "http://localhost:8080/api/alerts?page=1&size=10"
   ```

#### 2. 获取指定设备的警报

- **URL**: `/api/alerts/device/{deviceId}`
- **方法**: GET
- **描述**: 获取指定设备的所有警报，支持分页
- **路径参数**: 
  - `deviceId`: 设备ID
- **请求参数**:
  - `page`: 页码 (默认值: 0)
  - `size`: 每页大小 (默认值: 10)
- **成功响应**: 
  - 状态码: 200 OK
  - 响应体: 指定设备的警报列表和分页信息
- **错误响应**:
  - 状态码: 404 Not Found

**测试用例**:

1. 获取存在设备的警报:

   ```
   curl "http://localhost:8080/api/alerts/device/1?page=0&size=10"
   ```

2. 获取不存在设备的警报:

   ```
   curl "http://localhost:8080/api/alerts/device/999?page=0&size=10"
   ```

#### 3. 获取指定时间范围内的警报

- **URL**: `/api/alerts/timerange`
- **方法**: GET
- **描述**: 获取指定时间范围内的所有警报，支持分页
- **请求参数**:
  - `startDate`: 开始时间 (格式: yyyy-MM-dd HH:mm:ss)
  - `endDate`: 结束时间 (格式: yyyy-MM-dd HH:mm:ss)
  - `page`: 页码 (默认值: 0)
  - `size`: 每页大小 (默认值: 10)
- **成功响应**: 
  - 状态码: 200 OK
  - 响应体: 指定时间范围内的警报列表和分页信息

**测试用例**:

1. 获取指定时间范围内的警报:

   ```
   curl "http://localhost:8080/api/alerts/timerange?startDate=2023-06-01%2000:00:00&endDate=2023-06-30%2023:59:59&page=0&size=10"
   ```

#### 4. 获取指定设备和时间范围内的警报

- **URL**: `/api/alerts/device/{deviceId}/timerange`
- **方法**: GET
- **描述**: 获取指定设备在指定时间范围内的所有警报，支持分页
- **路径参数**: 
  - `deviceId`: 设备ID
- **请求参数**:
  - `startDate`: 开始时间 (格式: yyyy-MM-dd HH:mm:ss)
  - `endDate`: 结束时间 (格式: yyyy-MM-dd HH:mm:ss)
  - `page`: 页码 (默认值: 0)
  - `size`: 每页大小 (默认值: 10)
- **成功响应**: 
  - 状态码: 200 OK
  - 响应体: 指定设备在指定时间范围内的警报列表和分页信息
- **错误响应**:
  - 状态码: 404 Not Found

**测试用例**:

1. 获取存在设备在指定时间范围内的警报:

   ```
   curl "http://localhost:8080/api/alerts/device/1/timerange?startDate=2023-06-01%2000:00:00&endDate=2023-06-30%2023:59:59&page=0&size=10"
   ```

2. 获取不存在设备在指定时间范围内的警报:

   ```
   curl "http://localhost:8080/api/alerts/device/999/timerange?startDate=2023-06-01%2000:00:00&endDate=2023-06-30%2023:59:59&page=0&size=10"
   ```

## 配置说明

主要配置文件为`application.properties`,包含以下关键配置:


```1:16:src/main/resources/application.properties
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


这些配置定义了数据库连接、JPA设置、时区设置和日志级别。

## 运行说明

1. 确保已安装Java 8+和Maven。
2. 克隆项目到本地。
3. 在`application.properties`中配置数据库连接信息。
4. 在项目根目录下运行`mvn spring-boot:run`。
5. 访问`http://localhost:8080`使用API。

## 项目亮点

1. 使用WebSocket实现实时警报推送,提高了系统的实时性。
2. 支持模拟数据生成,方便测试和演示。
3. 采用RESTful API设计,易于集成和扩展。
4. 使用Spring Data JPA简化数据库操作,提高开发效率。
5. 全面的异常处理和日志记录,便于问题定位和调试。
6. 支持跨域请求,便于前后端分离开发。

## 未来改进方向

1. 添加用户认证和授权机制,如JWT。
2. 优化数据库查询性能,考虑使用缓存。
3. 增加单元测试和集成测试的覆盖率。
4. 考虑使用消息队列来处理大量的设备数据。
5. 添加更多的数据分析功能,如数据可视化。

## 学习建议

1. 从`BackProApplication.java`开始,了解项目的整体结构和启动过程。
2. 仔细阅读`UserController.java`和`UserService.java`,理解基本的CRUD操作和业务逻辑实现。
3. 学习`DeviceController.java`和`DeviceService.java`,了解设备管理的实现方式。
4. 研究`DataUpdateService.java`和`DataGenerationService.java`,理解数据处理和生成的流程。
5. 分析`AlertWebSocketHandler.java`,学习WebSocket的使用方法和实时通信的实现。
6. 查看`application.properties`文件,了解项目的配置信息和数据库连接设置。
7. 尝试运行项目,使用API测试工具(如Postman)测试各个API端点。