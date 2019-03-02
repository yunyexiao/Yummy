// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import Vuetify from 'vuetify'
import App from './App'
import router from './router'
import axios from 'axios'
import 'vuetify/dist/vuetify.min.css'
import 'material-design-icons-iconfont/dist/material-design-icons.css'

axios.defaults.baseURL = 'http://localhost:8081/Yummy/api'
axios.defaults.withCredentials = true
Vue.prototype.$ajax = axios
Vue.config.productionTip = false
Vue.use(Vuetify, {
  iconfont: 'md'
})

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  components: { App },
  template: '<App/>'
})
