import { createApp } from 'vue'
import App from './App.vue'
import { NavBar,Button,Icon,Tabbar, TabbarItem,Toast } from 'vant';

const app = createApp(App)

app.use(NavBar)
app.use(Button)
app.use(Icon)
app.use(Tabbar)
app.use(TabbarItem)
app.use(Toast)


app.mount('#app')
