// components/header/header.js
//获取应用实例
const app = getApp()
const sdk = require('../../sdk/index.js')

Component({
  /**
   * 组件的属性列表
   */
  properties: {
    enable: {
      type: Boolean,
      observer: function (newVal, oldVal, changedPath) {
        this.setData({
          envEnable:newVal
        })
      }
    },
    currEnv:{
      type:String,
      observer: function (newVal, oldVal, changedPath) {
        console.log("--------prop env",newVal)
        const tmp = this.data.ethEnvs.find(e=>e.val === newVal)
        this.setData({
          selEnv: tmp
        })
      }
    }
  },
  ready: function (e) {
    // 判断是否已经login
    if (app.globalData.logined) {
      this.init()
    } else {
      app.loginedCallback.push(res => {
        this.init()
      })
    }
    if (app.globalData.userInfo) {
      this.setData({
        userInfo: app.globalData.userInfo,
        hasUserInfo: true
      })
    } else if (this.data.canIUse) {
      // 由于 getUserInfo 是网络请求，可能会在 Page.onLoad 之后才返回
      // 所以此处加入 callback 以防止这种情况
      app.userInfoReadyCallback = res => {
        this.setData({
          userInfo: res.userInfo,
          hasUserInfo: true
        })
      }
    } else {
      // 在没有 open-type=getUserInfo 版本的兼容处理
      wx.getUserInfo({
        success: res => {
          app.globalData.userInfo = res.userInfo
          this.setData({
            userInfo: res.userInfo,
            hasUserInfo: true
          })
        }
      })
    }
  },
  /**
   * 组件的初始数据
   */
  data: {
    envEnable: true,
    userInfo: {},
    hasUserInfo: false,
    canIUse: wx.canIUse('button.open-type.getUserInfo'),
    ethEnvs: [
      { label: "Main Ethereum Network", val: "mainnet" },
      { label: "Ropsten Test Network", val: "ropsten" },
      { label: "Kovan Test Network", val: "kovan" },
      { label: "Rinkeby Test Network", val: "pinkeby" }
    ],
    selEnv: { label: "network...", val: null },

  },

  /**
   * 组件的方法列表
   */
  methods: {
    getUserInfo: function (e) {
      console.log(e)
      app.globalData.userInfo = e.detail.userInfo
      this.setData({
        userInfo: e.detail.userInfo,
        hasUserInfo: true
      })
    },
    init: function () {
      //获取全局 的 网络环境设置
      if (app.globalData.currEnv) {
        console.log("--------ready env", app.globalData.currEnv)
        // 查找下
        const tmpEnv = this.data.ethEnvs.find(e => e.val === app.globalData.currEnv)
        this.setData({
          selEnv: tmpEnv
        })
        // 通知事件
        this.trigger()
      } else {
        // 看下服务器端是否有保存 
        let page = this
        sdk.fetch({
          api: '/user/currEnv',
          success: res => {
            console.log('----success---',res)
            const tmpEnv = page.data.ethEnvs.find(e => e.val === res.toLowerCase())
            console.log('tmp', tmpEnv)
            // 保存到全局变量
            app.globalData.currEnv = tmpEnv.val//字符串
            this.setData({
              selEnv: tmpEnv
            })
            // 通知事件
            this.trigger()
          },
          fail: err => {
            console.log('---failed---')
            // 选择主网络,并告知服务器端保存
            page.setEnvData(page.data.ethEnvs[0])
          }
        })
      }
    },
    // 环境发生变化
    bindEnvPickerChange:function(e) {
      const env = this.data.ethEnvs[e.detail.value]
      this.setEnvData(env)
    },
    trigger: function () {
      // 通知事件
      const envEventDetail = { env: this.data.selEnv }// detail对象，提供给事件监听函数
      const envEventOption = {} // 触发事件的选项
      this.triggerEvent('changed', envEventDetail, envEventOption)
    },
    setEnvData: function (env) {
      wx.showLoading({
        title: 'waiting...',
      })
      // 通知服务器 
      sdk.fetch({
        api: '/user/setEnv',
        data: { env: env.val },
        success: res2 => {
          console.log(res2)
          this.setData({
            selEnv: env
          })
          // 保存到全局变量
          app.globalData.currEnv = this.data.selEnv.val//字符串
          // 通知事件
          this.trigger()
        },
        fail:err=>{//通知服务器失败，重新设置成原来的
          let tmp = this.data.ethEnvs.find(e => e.val === app.globalData.currEnv)
          if(tmp)
            this.setData({
              selEnv: tmp
            })
        },
        complete: e => {
          wx.hideLoading()
        }
      })
    }
  }
})
