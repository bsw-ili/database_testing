import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import './assets/main.css'
import axios from 'axios'
import * as ElIconList from '@element-plus/icons'
import DataVVue3 from '@kjgl77/datav-vue3'

const app = createApp(App)


for (const name in ElIconList) {
  app.component(name, ElIconList[name])
}

axios.defaults.baseURL = "http://localhost:8080"
app.config.globalProperties.$axios = axios

// 前置拦截
axios.interceptors.request.use(config => {
    return config;
})

axios.interceptors.response.use(response => {
        let res = response.data;

        console.log("=================")
        console.log(res)
        console.log("=================")

        return response;
    },
)


app.use(router)
app.use(DataVVue3)
app.use(ElementPlus, { size: 'small' }); 

app.mount('#app')

// Vue.prototype.$scrollTo = (x = 0, y = 0, type = 'smooth') => {
//   window.scrollTo({
//       top: x,
//       left: y,
//       behavior: type // 滚动行为：smooth平滑滚动，instant瞬间滚动，默认值auto，等同于instant
//   })
// }


// this.$scrollTo()
