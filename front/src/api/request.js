import axios from "axios";
import { ElMessage } from "element-plus";
import router from "@/router"; // 引入路由实例用于跳转登录页

const request = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 30000
});

// 请求拦截器
request.interceptors.request.use(
    config => {
        // 设置默认请求头
        config.headers['Content-Type'] = 'application/json;charset=UTF-8';
        
        // 从localStorage获取token - 添加调试日志
        const token = localStorage.getItem('token');
        console.log('当前token:', token);  // 调试用
        
        // 如果token存在，添加到请求头
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
            console.log('已添加Authorization头:', config.headers);  // 调试用
        } else {
            console.warn('未找到token，请求将不携带认证信息');
        }
        
        return config;
    },
    error => {
        ElMessage.error("请求发送失败，请检查网络");
        return Promise.reject(error);
    }
);

// 响应拦截器
request.interceptors.response.use(
    response => {
        let res = response.data;
        
        // 处理字符串响应
        if (typeof res === 'string') {
            try {
                res = JSON.parse(res);
            } catch (e) {
                ElMessage.error("响应数据格式异常");
                return Promise.reject(new Error("无效的响应格式"));
            }
        }

        // 统一处理业务状态码
        if (res.code === '200') {
            return res.data; // 直接返回业务数据
        } else {
            const errMsg = res.message || '请求失败';
            ElMessage.error(errMsg);
            return Promise.reject(new Error(errMsg));
        }
    },
    error => {
        // 网络/HTTP错误处理
        if (error.response) {
            const status = error.response.status;
            let errMsg = '请求失败';
            
            switch (status) {
                case 401: 
                    errMsg = '未授权，请重新登录';
                    // 清除本地token和用户信息
                    localStorage.removeItem('token');
                    localStorage.removeItem('userInfo');
                    // 跳转到登录页
                    router.push('/login');
                    break;
                case 403: 
                    errMsg = '禁止访问'; 
                    break;
                case 404: 
                    errMsg = '请求地址不存在'; 
                    break;
                case 500: 
                    errMsg = '服务器错误'; 
                    break;
                default: 
                    errMsg = `网络错误 (${status})`;
            }
            
            ElMessage.error(errMsg);
        } else {
            ElMessage.error("网络连接异常，请检查网络");
        }
        
        return Promise.reject(error);
    }
);

export default request;