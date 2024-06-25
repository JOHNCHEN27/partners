import { createApp } from 'vue'
import App from './App.vue'
//import { NavBar,Button,Icon,Tabbar, TabbarItem,Toast } from 'vant';
import * as VueRouter from 'vue-router'
import routes from "./config/route.ts";
import Vant from 'vant'


const app = createApp(App)
app.use(Vant)

//
// app.use(NavBar)
// app.use(Button)
// app.use(Icon)
// app.use(Tabbar)
// app.use(TabbarItem)
// app.use(Toast)


//创建路由实例并传递 routes 配置
const router = VueRouter.createRouter({
    //模式用： hash
    history: VueRouter.createWebHashHistory(),
    routes,  //routes 配置
})
app.use(router)

app.mount('#app')



