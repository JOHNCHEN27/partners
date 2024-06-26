import axios from "axios"

const myAxios = axios.create({
    baseURL: "http://localhost:8080/api"
})

myAxios.defaults.withCredentials =true; //表示向后端发送请求时携带凭证（cookie）

//请求拦截器
myAxios.interceptors.request.use(function (config){
    console.log("发送请求")
    return config;
}, function (error){
    return Promise.reject(error);
})

//响应拦截器
myAxios.interceptors.response.use(function (response){
    return response.data;
}, function (error) {
    return Promise.reject(error)
})

export default myAxios