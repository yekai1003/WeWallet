import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

Vue.config.productionTip = false

import Vuetify from 'vuetify'

import Vuelidate from 'vuelidate'

import 'vuetify/dist/vuetify.min.css'
import '@/assets/css/reset.css'
import 'material-design-icons-iconfont/dist/material-design-icons.css'

Vue.use(Vuetify)
Vue.use(Vuelidate)

new Vue({
    router,
    store,
    render: h => h(App)
}).$mount('#app')

