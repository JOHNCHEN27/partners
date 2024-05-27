/// <reference types="vite/client" />
// 解决typescript 只能理解 .ts 文件，无法理解 .vue文件问题
declare module '*.vue' {
    import { ComponentOptions } from 'vue'
    const componentOptions: ComponentOptions
    export default componentOptions
}
