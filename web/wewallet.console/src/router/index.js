import Vue from 'vue'
import Router from 'vue-router'
import Frame from '@/components/frame'
import menuList from '@/assets/menu'
import store from '@/store'
// 引入nprogress
import NProgress from 'nprogress'
import 'nprogress/nprogress.css' // 这个样式必须引入
import { generateRoutesFromMenu } from '@/common/utils'
import auth from '@/common/auth'

// 简单配置
NProgress.inc(0.2)
NProgress.configure({ easing: 'ease', speed: 500, showSpinner: false })

Vue.use(Router)

// const {state} = store

const routes = generateRoutesFromMenu(menuList)
const router = new Router({
  mode: 'history',
  routes: routes
})

router.beforeEach((to, from, next) => {
  NProgress.start()// 加载进度条

  if (!auth.loggedIn() && to.path !== '/login') {
    next({
      path: '/login',
      query: {redirect: to.fullPath}
    })
  } else { // 已经登陆，意味着 有token
    // 检查是否刷新加载
    if (to.path !== '/login' && to.path !== '/') { store.dispatch('changeCurrentMenu', to) }// 获取当前菜单,login页面没必要
    next()
  }
})
router.afterEach(() => {
  NProgress.done()
})

export default router
