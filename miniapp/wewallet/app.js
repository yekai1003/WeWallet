//app.js
const sdk = require('sdk/index.js')
const login = require('utils/login.js')
App({
  login,
  onLaunch: function () {
    // 连接到服务器，也就是 wx.login
    this.login.connect(this)
    // 获取用户信息
    wx.getSetting({
      success: res => {
        if (res.authSetting['scope.userInfo']) {
          // 已经授权，可以直接调用 getUserInfo 获取头像昵称，不会弹框
          wx.getUserInfo({
            success: res => {
              // 可以将 res 发送给后台解码出 unionId
              this.globalData.userInfo = res.userInfo

              // 由于 getUserInfo 是网络请求，可能会在 Page.onLoad 之后才返回
              // 所以此处加入 callback 以防止这种情况
              if (this.userInfoReadyCallback) {
                this.userInfoReadyCallback(res)
              }
            }
          })
        }
      }
    })
  },
  // 先定义，防止子页面多次初始化
  loginedCallback:[],
  // 保存直行失败的 apis
  failedApis:[],
  globalData: {
    userInfo: null,
    logined:false,
    indexRefresh:false,
    indexOpenAcctId:null,
    currEnv:null,
  }
})