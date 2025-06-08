# 媒体服务API前端集成指南

## 1. 接口概览

后端提供了两个主要接口用于获取随机媒体资源：

1. **获取随机图片**
   - 路径: `GET /random/image`
   - 参数: 
     - `category` (可选, 默认值: 1) - 分类ID
   - 响应: 图片二进制流

2. **获取随机视频**
   - 路径: `GET /random/video`
   - 参数:
     - `category` (可选, 默认值: 1) - 分类ID
   - 响应: 视频二进制流

## 2. 基础请求函数

```javascript
const API_BASE_URL = 'http://your-api-gateway'; // 替换为实际的API网关地址

/**
 * 获取随机媒体资源
 * @param {string} type - 媒体类型 'image' 或 'video'
 * @param {number} category - 分类ID，默认为1
 * @returns {Promise<Response>} 返回fetch的Promise对象
 */
async function fetchRandomMedia(type, category = 1) {
    const url = new URL(`${API_BASE_URL}/random/${type}`);
    url.searchParams.append('category', category);
    
    try {
        const response = await fetch(url.toString(), {
            method: 'GET',
            headers: {
                'Accept': type === 'image' ? 'image/*' : 'video/*'
            }
        });
        
        if (!response.ok) {
            const error = await response.json().catch(() => ({}));
            throw new Error(error.error || `获取随机${type}失败: ${response.statusText}`);
        }
        
        return response;
    } catch (error) {
        console.error(`获取随机${type}出错:`, error);
        throw error;
    }
}
```

## 3. 图片处理

```javascript
/**
 * 获取并显示随机图片
 * @param {HTMLElement} container - 图片容器元素
 * @param {number} category - 分类ID
 */
async function loadAndDisplayRandomImage(container, category = 1) {
    container.innerHTML = '<div class="loading">加载中...</div>';
    
    try {
        const response = await fetchRandomMedia('image', category);
        const blob = await response.blob();
        const imageUrl = URL.createObjectURL(blob);
        
        // 创建图片元素
        const img = new Image();
        img.onload = () => {
            URL.revokeObjectURL(imageUrl); // 释放URL对象
            container.innerHTML = ''; // 清空加载状态
            container.appendChild(img);
        };
        img.onerror = () => {
            container.innerHTML = '<div class="error">图片加载失败</div>';
        };
        img.src = imageUrl;
        img.style.maxWidth = '100%';
        img.style.height = 'auto';
        
    } catch (error) {
        container.innerHTML = `<div class="error">${error.message}</div>`;
    }
}
```

## 4. 视频处理

```javascript
/**
 * 获取并显示随机视频
 * @param {HTMLElement} container - 视频容器元素
 * @param {number} category - 分类ID
 */
async function loadAndDisplayRandomVideo(container, category = 1) {
    container.innerHTML = '<div class="loading">加载中...</div>';
    
    try {
        const response = await fetchRandomMedia('video', category);
        const videoBlob = await response.blob();
        const videoUrl = URL.createObjectURL(videoBlob);
        
        // 创建视频元素
        const video = document.createElement('video');
        video.controls = true;
        video.autoplay = true;
        video.muted = true; // 静音自动播放
        video.playsInline = true; // 在移动端内联播放
        video.style.maxWidth = '100%';
        video.style.maxHeight = '80vh';
        
        const source = document.createElement('source');
        source.src = videoUrl;
        source.type = response.headers.get('content-type') || 'video/mp4';
        
        video.appendChild(source);
        
        // 清理旧的视频元素
        container.innerHTML = '';
        container.appendChild(video);
        
        // 播放处理
        const playPromise = video.play();
        if (playPromise !== undefined) {
            playPromise.catch(error => {
                console.error('视频自动播放失败:', error);
                // 可以在这里添加用户交互后重试的逻辑
            });
        }
        
        // 监听视频结束，释放URL对象
        video.addEventListener('ended', () => {
            URL.revokeObjectURL(videoUrl);
        }, { once: true });
        
    } catch (error) {
        container.innerHTML = `<div class="error">${error.message}</div>`;
    }
}
```

## 5. 使用示例

```html
<div class="media-container">
    <div id="mediaContent"></div>
    <div class="controls">
        <button onclick="loadRandomImage()">随机图片</button>
        <button onclick="loadRandomVideo()">随机视频</button>
        <select id="categorySelect">
            <option value="1">分类1</option>
            <option value="2">分类2</option>
            <option value="3">分类3</option>
        </select>
    </div>
</div>

<script>
    const mediaContainer = document.getElementById('mediaContent');
    const categorySelect = document.getElementById('categorySelect');
    
    // 加载随机图片
    async function loadRandomImage() {
        const category = parseInt(categorySelect.value);
        await loadAndDisplayRandomImage(mediaContainer, category);
    }
    
    // 加载随机视频
    async function loadRandomVideo() {
        const category = parseInt(categorySelect.value);
        await loadAndDisplayRandomVideo(mediaContainer, category);
    }
    
    // 初始加载一张图片
    loadRandomImage();
</script>
```

## 6. 错误处理

```javascript
// 全局错误处理
window.addEventListener('unhandledrejection', (event) => {
    console.error('未处理的Promise拒绝:', event.reason);
    showErrorToast('发生错误: ' + (event.reason?.message || '未知错误'));
});

// 显示错误提示
function showErrorToast(message) {
    const toast = document.createElement('div');
    toast.className = 'error-toast';
    toast.textContent = message;
    document.body.appendChild(toast);
    
    setTimeout(() => {
        toast.remove();
    }, 5000);
}
```

## 7. 样式建议

```css
.media-container {
    max-width: 100%;
    margin: 0 auto;
    padding: 20px;
    text-align: center;
}

.loading, .error {
    padding: 20px;
    margin: 20px 0;
    border-radius: 4px;
}

.loading {
    background-color: #f0f0f0;
    color: #666;
}

.error {
    background-color: #ffebee;
    color: #c62828;
}

.controls {
    margin-top: 20px;
    display: flex;
    gap: 10px;
    justify-content: center;
    flex-wrap: wrap;
}

button {
    padding: 8px 16px;
    background-color: #2196f3;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
}

button:hover {
    background-color: #1976d2;
}

select {
    padding: 8px;
    border-radius: 4px;
    border: 1px solid #ddd;
}

.error-toast {
    position: fixed;
    bottom: 20px;
    left: 50%;
    transform: translateX(-50%);
    background-color: #ff5252;
    color: white;
      padding: 12px 24px;
      border-radius: 4px;
      box-shadow: 0 2px 5px rgba(0,0,0,0.2);
      z-index: 1000;
      animation: slideIn 0.3s ease-out;
}

@keyframes slideIn {
    from {
        transform: translate(-50%, 100%);
        opacity: 0;
    }
    to {
        transform: translate(-50%, 0);
      opacity: 1;
    }
}
```

## 8. 优化建议

1. **预加载**：在用户浏览当前内容时预加载下一个媒体资源
2. **缓存策略**：使用Service Worker缓存已加载的媒体资源
3. **错误重试**：添加重试机制，当加载失败时自动重试
4. **加载状态**：显示加载进度条或加载动画
5. **内存管理**：及时释放不再使用的URL对象，避免内存泄漏
6. **响应式设计**：确保在不同设备上都有良好的显示效果

## 9. 注意事项

1. 确保在HTTPS环境下使用，特别是视频播放功能
2. 处理跨域问题，确保服务器配置了正确的CORS头
3. 考虑添加加载超时处理
4. 移动端适配时注意触摸事件的处理
5. 视频自动播放策略：大多数浏览器要求视频静音才能自动播放

## 10. 扩展功能

1. **分类管理**：动态加载可用的分类
2. **收藏功能**：允许用户收藏喜欢的媒体
3. **分享功能**：分享当前查看的媒体
4. **下载功能**：允许用户下载媒体文件
5. **幻灯片模式**：自动轮播媒体内容
