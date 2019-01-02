import Vue from 'vue'
import Vuex from 'vuex'
import {getCurrentMenu, getSessionKey, generateMenuFromMenuData} from '../common/utils'
import menuList from '@/assets/menu'
import types from './mutation-types'
import * as getters from './getters'
import * as actions from './actions'
import * as mutations from './mutations'
import apiModule from './modules/apiModule'

Vue.use(Vuex)

const store = new Vuex.Store({
  strict: true,
  state: {
    fullScreenLoading: false,
    sidebar: {
      collapsed: getSessionKey('state.sidebar.collapsed', 'false') === 'true',
      show: getSessionKey('state.sidebar.show', 'true') === 'true'
    },
    device: {
      isMobile: false
    },
    menuList: [],
    currentMenus: [],
    clientId: 1, // 这是固定值,表示客户端
    token: null,
    userInfo: null,
    customerInfo: null
  },
  getters,
  modules: {apiModule},
  actions: {
    // 异步的函数
    toggleLoading: ({commit}) => commit(types.TOGGLE_LOADING),
    loadMenuList: ({commit}) => {
      // 从menuList生成一个新的menu ，否者 vuex 报异常
      commit(types.LOAD_MENU, generateMenuFromMenuData(menuList))
    },
    changeCurrentMenu: ({state, commit}, {path, matched, fullPath}) => {
      const a = getCurrentMenu(fullPath, state.menuList)
      commit(types.LOAD_CURRENT_MENU, a.reverse())
    },
    ...actions
  },
  mutations: {
    // 只能同步的函数
    [types.TOGGLE_DEVICE] (state, isMobile) {
      state.device.isMobile = isMobile
    },
    [types.TOGGLE_LOADING] (state) {
      state.loading = !state.loading
    },
    [types.LOAD_MENU] (state, menu) {
      state.menuList = menu
    },
    [types.LOAD_CURRENT_MENU] (state, menu) {
      state.currentMenus = menu
    },
    [types.SET_USER_INFO] (state, info) {
      state.userInfo = info
    },
    [types.SET_CUSTOMER_INFO] (state, info) {
      state.customerInfo = info
    },
    [types.SET_TOKEN] (state, payload) {
      state.token = payload.token
    },
    [types.TOGGLE_SIDEBAR] (state, collapsed) {
      if (collapsed == null) collapsed = !state.sidebar.collapsed
      state.sidebar.collapsed = collapsed
      window.sessionStorage.setItem('state.sidebar.collapsed', collapsed)
    },
    [types.TOGGLE_SIDEBAR_SHOW] (state, show) {
      if (show == null) state.sidebar.show = !state.sidebar.show
      else {
        state.sidebar.show = show
      }
      window.sessionStorage.setItem('state.sidebar.show', state.sidebar.show)
    },
    ...mutations
  }
})

export default store
