// pages/send/send.js
const app = getApp()
const sdk = require('../../sdk/index.js')
Page({

  /**
   * 页面的初始数据
   */
  data: {
    envEnable:true,
    currEnv:null,
    account:null,
    token:null
  },
  // 当可以修改的时候，修改后的网络环境
  envChanged : function(e){
    console.log('---env',e)
    this.setData({
      currEnv:e.detail.env.val //这里是用字符串的
    })
  },
  backEvt : function(e){
    wx.navigateBack({
      delta: 1
    })
  },
  transferSubmit: function(e){
    wx.showLoading({
      title: 'transfer...',
    })
    // 真实请求
    let req = {
      data:{
        env: app.globalData.currEnv,
        value : e.detail.value.amount,
        toAddress : e.detail.value.to,
        payPwd: e.detail.value.pwd
      },
      success: res => {
        wx.showModal({
          title: 'success',
          content: 'transfer job created!',
          showCancel: false,
          success: res => {
            app.globalData.indexRefresh = true // 刷下不知道会不会更新余额
            wx.navigateBack({
              delta: 1
            })
          }
        })
      },
      fail: err => {
        console.log(err)
      },
      complete: e => {
        wx.hideLoading()
      }
    }
    if(this.data.account){
      req.api = '/core/tx/transfer'
      req.data.accountId = this.data.account.id
      req.data.unit = 'ether' // default
    } else if (this.data.token) {
      req.api = '/core/tx/erc20TokenTransfer'
      req.data.tokenId = this.data.token.id
    }
    sdk.fetch(req) // 请求
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    console.log(options)
    if(options.tokenId){
      //获取 token 实体  这里是测试数据
      sdk.fetch({
        api:'/core/token/get',
        data:{id:options.tokenId},
        success: res => {
          // 直接 让 全局的currEnv 切换成 token的 env
          app.globalData.currEnv = res.env// 字符串
          this.setData({
            token:res,
            envEnable: false,
          })
        },
        fail:err=>{
          console.log(err)
        }
      })
    } else{
      // 获取 account 数据 这里是测试数据
      sdk.fetch({
        api:'/core/wallet/get',
        data:{id:options.acctId},
        success: res => {
          this.setData({
            account:res,
            envEnable: true
          })
        },
        fail: err => {
          console.log(err)
        }
      })
    }
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {
    if(app.globalData.currEnv){
      this.setData({
        currEnv: app.globalData.currEnv
      })
    }
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
  
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {
  
  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {
  
  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {
  
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {
  
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {
  
  }
})