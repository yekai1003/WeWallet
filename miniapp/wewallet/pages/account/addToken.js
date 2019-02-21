// pages/account/addToken.js
const app = getApp()
const sdk = require('../../sdk/index.js')

Page({

  /**
   * 页面的初始数据
   */
  data: {
    acctId:null,
    env:null,
    tokens:[]
  },
  addSubmit: function(e){
    console.log(e)
    // 检查数据
    let data = {
      accountId:this.data.acctId,
      env:this.data.env,
      tokenSymbol: e.detail.value.tokenSymbol
      // name 其实是可以获取到的，这里就不用了
    }
    if (e.detail.value.tokenAddr.length == 40 || e.detail.value.tokenAddr.length == 42)
      data.contractAddr = e.detail.value.tokenAddr.length == 40 ? e.detail.value.tokenAddr : e.detail.value.tokenAddr.substring(2)
    else{
      wx.showModal({
        title: 'error',
        content: 'address format error',
      })
      return
    }
    if (/^[0-9]+$/.test(e.detail.value.decimals))
      data.decimals = parseInt(e.detail.value.decimals)
    else {
      wx.showModal({
        title: 'error',
        content: 'decimals must be integer',
      })
      return
    }
    wx.showLoading({
      title: 'waiting...',
    })
    // 提交
    sdk.fetch({
      api:'/core/token/addToken',
      data,
      success:res=>{
        // 一个token值
        // 返回 上一页，此时应该刷新的是 token
        app.globalData.indexRefresh = true // 让首页刷新的时候刷新当前打开的卡
        app.globalData.indexOpenAcctId = this.data.acctId // 刷新 tokens
        wx.navigateBack({
          delta: 1
        })
      },
      complete: e => {
        wx.hideLoading()
      }
    })
  },
  /**
   * 切换 环境 事件
   */
  envChanged: function (e) {
    console.log(e)
    this.data.env = e.detail.env.val // 本地维护有没有必要？
    //更新 当前环境 的tokens (太麻烦，不要了)
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.data.acctId = parseInt(options.acctId)//必须有 传过来默认是字符串，贼麻烦
    // console.log(app.globalData)
    this.data.env = app.globalData.currEnv
  },
  cancelEvt: function (e) {
    wx.navigateBack({
      delta: 1
    })
  },
  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

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