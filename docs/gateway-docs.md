# API Gateway 服务文档

## 1. 服务概述
API Gateway 服务作为系统的入口，负责将请求路由到相应的微服务。它使用 Spring Cloud Gateway 实现，并支持服务发现和负载均衡。

## 2. 服务配置

### 2.1 基本配置
```yaml
server:
  port: 8080  # 网关服务端口

spring:
  application:
    name: gateway-service
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true  # 启用服务发现
          lower-case-service-id: true  # 服务名小写
      routes:
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/api/user/**
          filters:
            - StripPrefix=1

        - id: media-service
          uri: lb://media-service
          predicates:
            - Path=/api/media/**
          filters:
            - RewritePath=/api/media/(?<segment>.*), /$\{segment}

        - id: security-service
          uri: lb://security
          predicates:
            - Path=/api/security/**
          filters:
            - StripPrefix=1
```

### 2.2 Nacos 配置
```yaml
spring:
  cloud:
    nacos:
      discovery:
        enabled: true
        server-addr: 192.168.31.106:8848
        user-name: nacos
        password: nacos
        naming-load-cache-at-start: true
        register-enabled: true
        namespace: 0
        group: DEFAULT_GROUP
```

## 3. 路由规则

### 3.1 媒体服务路由
- 基础路径: `/api/media/**`
- 重写规则: `/api/media/` -> `/`
- 服务地址: `lb://media-service`
- 支持的接口:
  - 随机图片: `GET /api/media/image?category={categoryId}`
  - 随机视频: `GET /api/media/video?category={categoryId}`
  - 缓存同步: `POST /api/media/api/cache/sync`

### 3.2 用户服务路由
- 基础路径: `/api/user/**`
- 重写规则: 移除 `/api/user/` 前缀
- 服务地址: `lb://user-service`

### 3.3 安全服务路由
- 基础路径: `/api/security/**`
- 重写规则: 移除 `/api/security/` 前缀
- 服务地址: `lb://security`

## 4. 请求示例

### 4.1 获取随机图片
```bash
curl "http://localhost:8080/api/media/image?category=1"
```

### 4.2 获取随机视频
```bash
curl "http://localhost:8080/api/media/video?category=1"
```

### 4.3 同步缓存
```bash
curl -X POST http://localhost:8080/api/media/api/cache/sync
```

## 5. 注意事项
1. 所有请求都必须通过网关服务访问
2. 网关服务会自动处理负载均衡
3. 请求路径必须以 `/api` 开头
4. 媒体服务的接口路径需要去掉 `/api/media` 前缀
5. 建议在应用启动时调用缓存同步接口

## 6. 错误处理
- `400` - 参数错误
- `500` - 服务器内部错误
- `404` - 路由未找到或资源未找到

## 7. 日志配置
```yaml
logging:
  level:
    org.springframework.cloud.gateway: info
    reactor.netty: warn
    org.springframework.cloud.gateway.filter: info
    org.springframework.cloud.gateway.filter.factory: info
    org.springframework.cloud.gateway.route: info
```
