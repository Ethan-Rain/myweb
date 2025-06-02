# 媒体服务 API 文档

## 服务概述
这是一个媒体资源管理服务，提供随机图片和视频的获取功能。所有资源都按分类进行管理。

## API 列表

### 1. 同步缓存接口
```
POST /api/cache/sync
```
**描述**：同步所有图片和视频资源到 Redis 缓存

**请求参数**：无

**返回值**：
```json
{
    "success": true,
    "message": "缓存同步成功"
}
```

### 2. 获取随机图片
```
GET /random/image?category={categoryId}
```
**描述**：获取指定分类下的随机图片

**请求参数**：
- `category` (Long, 必填) - 分类ID

**返回值**：
```json
{
    "success": true,
    "data": {
        "imagePath": "图片路径"
    }
}
```

### 3. 获取随机视频
```
GET /random/video?category={categoryId}
```
**描述**：获取指定分类下的随机视频

**请求参数**：
- `category` (Long, 必填) - 分类ID

**返回值**：
```json
{
    "success": true,
    "data": {
        "videoPath": "视频路径"
    }
}
```

## 错误码说明
- `400` - 参数错误
- `500` - 服务器内部错误
- `404` - 资源未找到

## 示例代码

### JavaScript 示例
```javascript
// 获取随机图片
async function getRandomImage(categoryId) {
    try {
        const response = await fetch(`/random/image?category=${categoryId}`);
        const data = await response.json();
        return data.data.imagePath;
    } catch (error) {
        console.error('获取随机图片失败:', error);
        return null;
    }
}

// 获取随机视频
async function getRandomVideo(categoryId) {
    try {
        const response = await fetch(`/random/video?category=${categoryId}`);
        const data = await response.json();
        return data.data.videoPath;
    } catch (error) {
        console.error('获取随机视频失败:', error);
        return null;
    }
}

// 同步缓存
async function syncCache() {
    try {
        const response = await fetch('/api/cache/sync', {
            method: 'POST'
        });
        const data = await response.json();
        return data.success;
    } catch (error) {
        console.error('同步缓存失败:', error);
        return false;
    }
}
```

### TypeScript 接口定义
```typescript
interface ApiResponse<T> {
    success: boolean;
    data?: T;
    message?: string;
}

interface ImageResponse {
    imagePath: string;
}

interface VideoResponse {
    videoPath: string;
}

// API 调用函数
async function getRandomImage(categoryId: number): Promise<ApiResponse<ImageResponse>>;
async function getRandomVideo(categoryId: number): Promise<ApiResponse<VideoResponse>>;
async function syncCache(): Promise<ApiResponse<void>>;
```

## 注意事项
1. 所有接口都支持跨域访问
2. 建议在应用启动时调用一次 `/api/cache/sync` 接口
3. 缓存默认1小时过期，过期后会自动从数据库重新加载
4. 如果返回空数组，表示该分类下没有可用资源
5. 建议前端添加错误处理和重试机制

## 调用示例
```bash
# 同步缓存
curl -X POST http://localhost:8082/api/cache/sync

# 获取随机图片
curl "http://localhost:8082/random/image?category=1"

# 获取随机视频
curl "http://localhost:8082/random/video?category=1"
```
