// pages/account/new.js
const app = getApp()
const sdk = require('../../sdk/index.js')

Page({

  /**
   * 页面的初始数据
   */
  data: {
    importTypes:["Private Key","Mnemonic","Keystore"],
    importType:0,
    randomWords:'',
  },
  importTypeEvt: function(e){
    this.setData({
      importType:parseInt(e.detail.value)
    })
  },
  cancelEvt: function(e){
    wx.navigateBack({
      delta: 1
    })
  },
  generateEvt: function(e){
    sdk.fetch({
      api:'/core/wallet/randomMnemonic',
      success:res=>{
        let rw = res.words.join(' ')
        this.setData({
          randomWords:rw
        })
        // 设置粘贴板
        wx.setClipboardData({
          data: rw,
        })
      }
    })
  },
  createSubmit: function(e){
    let req = {
      api:'/core/wallet/createAccount',
      data: {
        accountType: 60,// 以太坊账号
        mark: e.detail.value.acctName,
        pwd: e.detail.value.pwd
      },
      success: res => {
        console.log(res)
        app.globalData.indexRefresh = true
        wx.navigateBack({ delta: 1 })// 退回前一页
      },
      fail:err=>{
        console.log(err)
      },
      complete:e=>{
        wx.hideLoading()
      }
    }
    // e.detail.value.acctName | pwd
    // console.log(e)
    // show loading
    wx.showLoading({
      title: 'waiting...',
    })
    sdk.fetch(req)
  },
  importSubmit: function(e){
    // public params
    // e.detail.value.acctName
    let req = {
      data: {
        accountType:60,// 以太坊账号
        mark: e.detail.value.acctName
      },
      success: res => {
        console.log(res)
        app.globalData.indexRefresh = true
        wx.navigateBack({ delta: 1 })// 退回前一页
      },
      fail: err => {
        console.log(err)
      },
      complete: e => {
        wx.hideLoading()
      }
    }
    console.log('import type:'+this.data.importType)
    if (this.data.importType === 0) {
      req.api = '/core/wallet/createAccountByPrivateKey'
      // e.detail.value.pwd | privateKey
      req.data.pwd = e.detail.value.pwd
      req.data.privateKey = e.detail.value.privateKey
    } else if (this.data.importType === 1) {
      req.api = '/core/wallet/createAccountByMnemonic'
      // e.detail.value.pwd | mnemonic  
      req.data.pwd = e.detail.value.pwd
      req.data.mnemonic = e.detail.value.mnemonic
    } else if (this.data.importType === 2) {
      req.api = '/core/wallet/createAccountByKeystore'
      // e.detail.value.keystore
      req.data.keystore = e.detail.value.keystore
    }
    console.log(req)
    wx.showLoading({
      title: 'waiting',
    })
    sdk.fetch(req) // 请求
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
  
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