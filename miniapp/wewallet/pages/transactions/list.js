// pages/transactions/list.js
const sdk = require('../../sdk/index.js')

Page({

  /**
   * 页面的初始数据
   */
  data: {
    transactions:[],
    acctId:null,
    currPageNo:0,
    loaded:false
  },
  envChanged:function(e){
    // 重新加载交易列表
    this.data.transactions = []//clear
    this.data.currPageNo = 0
    if(this.loaded)
      this.loadTrans(this.data.acctId,this.data.currPageNo)
  },
  //事件处理函数
  itemToggle: function (e) {
    const id = parseInt(e.currentTarget.id)
    const list = this.data.transactions
    for (let i = 0, len = list.length; i < len; ++i) {
      if (list[i].id === id) {
        list[i].open = !list[i].open
      } else {
        list[i].open = false
      }
    }
    this.setData({
      transactions: list
    })
    //wx.reportAnalytics(eventname, data) ???
  },
  loadTrans(acctId,pageNo,callback){
    sdk.fetch({
      api:'/core/tx/list',
      data:{
        page:pageNo,
        accountId: acctId
      },
      success: res => {
        this.data.transactions = this.data.transactions.concat(res.content)
        this.setData({
          transactions: this.data.transactions
        })
        if(callback)
          callback(res)
      },
      fail: err => {
        console.log(err)
      }
    })
  },
  refreshEvt: function(e){
    // console.log(e)
    // 第一层请求 刷新，第二层重新获取交易
    let page=this
    sdk.fetch({
      api:'/core/tx/refresh',
      data:{id:e.currentTarget.dataset.id},
      success:res=>{
        console
        // 发起定时任务
        if (res.code === 'success') {
          setTimeout((id) => {
            sdk.fetch({
              api:'/core/tx/get',
              data:{id},
              success:res2=>{
                let idx = page.data.transactions.findIndex(t=>t.id == res2.id)
                if(idx >=0 ){
                  page.setData({
                    ["transactions["+idx+"]"]:res2
                  })
                }
              },
              fail: err2 => {
                console.log(err2)
              }
            })
          }, 5000, e.currentTarget.dataset.id)
        }else
          wx.showModal({
            title: 'error',
            content: '有点异常！',
          })
      },
      fail: err => {
        console.log(err)
      }
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    console.log(options)
    // 清数据
    this.data.transactions = []
    this.data.acctId = options.acctId
    this.data.currPageNo=0
    // options.acctId
    console.log('----------onLoad----')
    this.loadTrans(options.acctId,this.data.currPageNo,res=>{
      this.loaded = true
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
    console.log('----------onReachBottom----')
    this.loadTrans(this.data.acctId,++this.data.currPageNo)
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  }
})