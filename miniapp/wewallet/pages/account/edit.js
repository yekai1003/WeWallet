// pages/account/edit.js
const sdk = require('../../sdk/index.js')
const app = getApp()
Page({

  /**
   * 页面的初始数据
   */
  data: {
    account:null,
  },
  editSubmit: function(e){
    if (e.detail.value.acctName === ''){
      wx.showModal({
        title: 'error',
        content: 'account name is null',
      })
      return
    }
    wx.showLoading({
      title: 'waiting...',
    })
    sdk.fetch({
      api:'/core/wallet/updateAccount',
      data: { accountId: this.data.account.id, mark: e.detail.value.acctName},
      success: res => {
        // 标记刷新
        app.globalData.indexRefresh=true
        wx.navigateBack({ delta: 1 })
      },
      complete: e => {
        wx.hideLoading()
      }
    })
  },
  cancelEvt : function(e){
    wx.navigateBack({ delta: 1 })
  },
  loadAcct(id){
    sdk.fetch({
      api:'/core/wallet/get',
      data:{id},
      success:res=>{
        this.setData({
          account:res
        })
      }
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    if(!options.acctId)
      wx.navigateBack({ delta: 1 })
    // 保存 账户信息
    //this.data.acctId = options.acctId
    this.loadAcct(options.acctId)
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