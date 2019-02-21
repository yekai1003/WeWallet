// 错误处理器
module.exports = {
  doAction:function(res,apiParams){
    if (res.errCode) {
      if (res.errCode === 'token_is_invalid' || res.errCode === 'token_is_empty') {
        // 需要登录
        const app=getApp()
        if(app){
          // 保存 失败的apiParams
          app.failedApis.push(apiParams)
          app.login.connect(app)
        }
      } else {
        wx.showModal({
          title: 'error',
          content: res.errMsg,
        })
      }
    }else{
      wx.showModal({
        title: 'error',
        content: res,
      })
    }
  },
  clearToken:function(){
    
  }
}