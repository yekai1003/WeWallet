const sdk = require('../sdk/index.js')
module.exports = {
  connect:function(app){
    if(!app)
      app = getApp()
    app.globalData.logined = false// 标记下未登录
    // console.log(sdk)
    wx.showLoading({
      title: 'connecting...',
    })
    wx.removeStorage({
      key: 'token',
      success: res => {
        // 登录
        wx.login({
          success: res => {
            console.log(res)
            // 发送 res.code 到后台换取 openId, sessionKey, unionId
            sdk.fetch({
              api: "/authorize/wxLogin",
              data: { code: res.code },
              success: res => {
                wx.setStorageSync('token', res)// 这里同步实现，否则还是有时间误差
                wx.hideLoading()//关闭loading
                // 标记下已经登陆
                app.globalData.logined = true
                if (app.loginedCallback && app.loginedCallback.length > 0){
                  app.loginedCallback.forEach(fun => {
                    fun(res)
                  })
                  //清空
                  app.loginedCallback=[]
                }
                // 上次失败的 api，调用下
                if (app.failedApis && app.failedApis.length>0){
                  app.failedApis.forEach(api=>{
                    sdk.fetch(api)
                  })
                  app.failedApis = []
                }
              },
              fail: err => {
                wx.hideLoading()
                console.log(err)
                wx.showModal({
                  title: 'failed',
                  content: 'connect to server failed!',
                  success: res => {
                    // 重新连接
                    setTimeout(this.connect, 1000)
                  }
                })
              }
            })
          }
        })
      },
      fail: err => {
        wx.showModal({
          title: 'error',
          content: err,
        })
        // 如果loading 打开，则关闭
        wx.hideLoading()
      }
    })
  }
}