# 用户认证API文档

## 1. 登录接口

### 接口路径
POST `/auth/login`

### 请求参数
```json
{
    "username": "string",      // 用户名
    "password": "string"       // 密码
}
```

### 响应示例
```json
{
    "token": "string",         // JWT令牌
    "message": "登录成功"
}
```

### 错误情况
- 401 Unauthorized: 用户名或密码错误
- 500 Internal Server Error: 服务器内部错误

## 2. 注销接口

### 接口路径
POST `/auth/logout`

### 请求参数
无

### 响应示例
```json
{
    "message": "注销成功"
}
```

### 错误情况
- 500 Internal Server Error: 服务器内部错误

## 3. 注册接口

### 接口路径
POST `/auth/register`

### 请求参数
```json
{
    "username": "string",          // 用户名
    "password": "string",          // 密码
    "confirmPassword": "string",   // 确认密码
    "email": "string"              // 邮箱
}
```

### 响应示例
```json
{
    "userId": "number",           // 用户ID
    "message": "注册成功"
}
```

### 错误情况
- 400 Bad Request: 
  - 密码不匹配
  - 用户名已存在
  - 参数格式错误
- 500 Internal Server Error: 服务器内部错误

## 3. 认证机制说明

### JWT令牌
- 令牌类型: Bearer Token
- 令牌有效期: 24小时
- 存储位置: localStorage

### 请求头格式
```http
Authorization: Bearer <your-token>
```

## 4. 前端实现建议

### 页面结构
1. 登录页面
   - 表单字段:
     - 用户名输入框
     - 密码输入框
     - 登录按钮
     - 注册链接
   - 功能:
     - 表单验证
     - 错误提示显示
     - 成功后跳转到主页

2. 注册页面
   - 表单字段:
     - 用户名输入框
     - 密码输入框
     - 确认密码输入框
     - 邮箱输入框
     - 注册按钮
     - 返回登录链接
   - 功能:
     - 密码匹配验证
     - 邮箱格式验证
     - 错误提示显示
     - 成功后跳转到登录页

### API调用示例

#### 登录示例
```javascript
// 登录请求
const login = async (username, password) => {
    try {
        const response = await fetch('/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                username,
                password
            })
        });
        const data = await response.json();
        if (response.ok) {
            // 保存token
            localStorage.setItem('token', data.token);
            return data;
        } else {
            throw new Error(data.message || '登录失败');
        }
    } catch (error) {
        console.error('登录失败:', error);
        throw error;
    }
};

// 带认证的请求拦截器
const fetchWithAuth = async (url, options = {}) => {
    const token = localStorage.getItem('token');
    if (token) {
        options.headers = {
            ...options.headers,
            'Authorization': `Bearer ${token}`
        };
    }
    return fetch(url, options);
};
```

#### 注册示例
```javascript
// 注册请求
const register = async (userData) => {
    try {
        const response = await fetch('/auth/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(userData)
        });
        const data = await response.json();
        if (response.ok) {
            return data;
        } else {
            throw new Error(data.message || '注册失败');
        }
    } catch (error) {
        console.error('注册失败:', error);
        throw error;
    }
};
```

### 错误处理建议
1. 表单验证错误:
   - 显示错误提示
   - 高亮错误字段
   - 禁用提交按钮

2. API错误:
   - 显示友好的错误信息
   - 提供重试选项
   - 记录错误日志

3. 网络错误:
   - 显示离线提示
   - 自动重试机制
   - 保存未完成的操作
