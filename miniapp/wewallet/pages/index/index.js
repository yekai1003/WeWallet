// pages/index/index.js
const utils = require('../../utils/util.js')
const sdk = require('../../sdk/index.js')
const counter = require('../../utils/counter.js')
const lzString = require('../../utils/lz-string.js')
const app = getApp()
Page({

  /**
   * 页面的初始数据
   */
  data: {
    userInfo:{},
    hasUserInfo: false,
    canIUse: wx.canIUse('button.open-type.getUserInfo'),
    date: utils.formatTime(new Date()).split(' ')[0],
    time: utils.formatTime(new Date()).split(' ')[1],
    mark:'',
    amount:'',
    days:[],
    currMonth: {
      inM: 0.0,
      outM: 0.0,
      year:0,
      month:0,
      hasPrev:false,
      hasNext:false
    },
    allMonth:null,
    isRecording:false
  },
  getUserInfo: function (e) {
    console.log(e)
    app.globalData.userInfo = e.detail.userInfo
    this.setData({
      userInfo: e.detail.userInfo,
      hasUserInfo: true
    })
    // 提交 到服务器
    sdk.fetch({
      api: '/user/updateMemberWeixin',
      data: { encryptedData: e.detail.encryptedData, iv: e.detail.iv },
      success:res=>{
        console.log(res)
      },
      fail:err=>{
        console.log(err)
      }
    })
  },
  newRecordEvt: function(e){
    console.log(e)
    this.setData({
      isRecording:true
    })
  },
  itemToggle:function(e){
    console.log(e)
    const id = e.currentTarget.id
    const list = this.data.days
    for (let i = 0, len = list.length; i < len; ++i) {
      if (list[i].d === id) {
        list[i].open = !list[i].open
      } else {
        list[i].open = false
      }
    }
    this.setData({
      days:list
    })
    //wx.reportAnalytics('click_view_programmatically', {})
  },
  // 支出 提交
  outSubmit: function (e) {
    if (e.detail.value.amount === '') {
      wx.showToast({
        title: '必须输入金额',
      })
      return
    }
    let amount = parseFloat(e.detail.value.amount)
    //console.log(amount)
    if ( !(amount >0.0 || amount<0.0) ){
      wx.showToast({
        title: '金额不能为 0',
      })
      return
    }
    let data={
      d:e.detail.value.d,
      t:e.detail.value.t,
      mark: e.detail.value.mark === '' ? '默认' : e.detail.value.mark,
      amount: amount > 0 ? -amount : amount
    }
    this.record(data)
  },
  // 收入 提交
  inSubmit: function (e) {
    if (e.detail.value.amount === '') {
      wx.showToast({
        title: '必须输入金额',
      })
      return
    }
    let amount = parseFloat(e.detail.value.amount)
    //console.log(amount)
    if (!(amount > 0.0 || amount < 0.0)) {
      wx.showToast({
        title: '金额不能为 0',
      })
      return
    }
    let data = {
      d: e.detail.value.d,
      t: e.detail.value.t,
      mark: e.detail.value.mark === '' ? '默认' : e.detail.value.mark,
      amount: amount > 0 ? amount : -amount
    }
    this.record(data)
  },
  record:function(data){
    wx.showLoading({
      title: '保存...',
    })
    // 先分析数据结构
    // 所有数据都是days
    // 查询 当前day 存不存在,循环遍历（不合理），保存结构为，年月日的方式，保存map结构，而后重新转换成列表数据
    /**
     * years:{
     *  month1:{
     *      day1:[{time,mark,amont},{time,mark,amont}],
     *      day2:{time,mark,amont}
     *  }
     * }
     */
    let page = this
    // 切割 d 
    let dArr = data.d.split('-')
    // 加载数据 同步加载，因为可能为空
    let records = wx.getStorageSync('records')
    if(!records)
      records={}
    // 获取年数据
    let yd = records[dArr[0]]
    console.log(yd,!!yd)
    if (!yd) {//不存在这新建
      yd = {}
      records[dArr[0]] = yd
    }
    // 获取 月
    let md = yd[dArr[1]]
    if (!md) {// 不存在创建
      md = {}
      yd[dArr[1]] = md
    }
    // 获取 日
    let dd = md[dArr[2]]
    if (!dd) {
      dd = [] // 日的结构应该是数组,因为这是最后一级树结构
      md[dArr[2]] = dd
    }
    // 加入数据
    dd.push({ time: data.t, mark: data.mark, amount: data.amount })
    // 保存回去
    wx.setStorage({
      key: 'records',
      data: records,
      complete: obj => {
        wx.hideLoading()
        page.setData({
          isRecording: false
        })
        // let tmp1 = lzString.compressToEncodedURIComponent(JSON.stringify(res.data))
        // console.log('tmp1', tmp1)
        // let tmp2 = lzString.decompressFromEncodedURIComponent(tmp1)
        // console.log('tmp2', tmp2)
        // 提交服务器
        sdk.fetch({
          api:'/core/wallet/saveAccountBook',
          data: { data: lzString.compressToEncodedURIComponent(JSON.stringify(records))},
          success:res=>{
            console.log(res)
          },
          fail:err=>{
            console.log(err)
          }
        })
        // 重新加载显示数据
        page.loadData()
      }
    })
  },
  cancelEvt: function(e){
    this.setData({
      isRecording: false
    })
  },
  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {
    // do somthing
    // 打开 记账
    this.setData({
      isRecording: true,
      date: utils.formatTime(new Date()).split(' ')[0],
      time: utils.formatTime(new Date()).split(' ')[1],
      mark:'',
      amount:''
    })
    wx.stopPullDownRefresh()
  },
  loadData: function (dArr){// [2019,01,30] //直接数组
    let page = this
    if (!dArr) {
      let tmpDate = utils.formatTime(new Date()).split(' ')[0]
      dArr = tmpDate.split('-')
    }
    wx.showLoading({
      title: 'loading...',
    })
    wx.getStorage({
      key: 'records',
      success: res => {
        // console.log(res.data)
        // 加载所有月份 信息 dArr 加入是因为可能 当前日期不在数据里
        this.loadAllMonth(dArr,res.data)
        // 解析数据，并显示
        // 获取当前年月 的数据
        let yd = res.data[dArr[0]]
        if (!yd) yd = {}
        let md = yd[dArr[1]]
        if (!md) md = {}
        console.log(md)
        // 组装
        let days = []
        let inM = 0.0
        let outM = 0.0
        for (let d in md) {
          // 这里会得到一天 的数据量，d是 日
          let day = {
            d: dArr[0] + '-' + dArr[1] + '-' + d,
            records: md[d],// 这里本来就是那个数组
            inM: 0.0,
            outM: 0.0
          }
          // 计算 收入，支出
          md[d].forEach(r => {
            if (r.amount < 0)
              day.outM += r.amount
            else
              day.inM += r.amount
          })
          days.push(day)
          // 全局也加上
          outM += day.outM
          inM += day.inM
        }
        this.data.currMonth.inM = inM
        this.data.currMonth.outM = outM
        this.data.currMonth.year = dArr[0]
        this.data.currMonth.month = dArr[1]
        // 计算下 是否有下一页，是否有上一页
        let idx = this.data.allMonth.findIndex(m => m === (dArr[0] + ' - ' + dArr[1]))
        if(!idx)
          idx = 0
        let lastIdx = this.data.allMonth.length-1
        if (idx === 0) {
          this.data.currMonth.hasPrev = false
        } else {
          this.data.currMonth.hasPrev = true
        }
        if (idx == lastIdx)
          this.data.currMonth.hasNext = false
        else
          this.data.currMonth.hasNext = true
        this.setData({
          days,
          currMonth: this.data.currMonth
        })
      },
      fail:err=>{
        console.log('failed',err)
        // 加载失败之后去服务器获取下看看
        sdk.fetch({
          api:'/core/wallet/getAccountBook',
          success:res=>{
            console.log(res)
            if(res.data){
              wx.setStorageSync('records', JSON.parse(lzString.decompressFromEncodedURIComponent(res.data)))
              page.loadData(dArr)
            }
          }
        })
      },
      complete: e => {
        // 隐藏 loading...
        wx.hideLoading()
      }
    })
  },
  loadAllMonth: function(dArr,data){
    let ay = []
    let aym = {}
    for (let y in data){// 年份循环
      ay.push(parseInt(y))
      let am=[]
      for(let m in data[y]){
        am.push(m)
      }
      // 月份排序
      am.sort((m1, m2) => {
        if (parseInt(m1) < parseInt(m2)) return -1
        if (parseInt(m1) > parseInt(m2)) return 1
        return 0
      })
      aym[y] = am
    }
    // 判断 dArr
    let yIdx = ay.findIndex(y=>y===parseInt(dArr[0]))
    if (yIdx < 0) ay.push(parseInt(dArr[0]))
    if(!aym[dArr[0]]){
      aym[dArr[0]] = []
    }
    let mIdx = aym[dArr[0]].findIndex(m=>m===dArr[1])
    if (mIdx < 0) aym[dArr[0]].push(dArr[1])
    //年份排序
    ay = ay.sort((y1,y2)=>{
      if(y1<y2) return -1
      if(y1>y2) return 1
      return 0
    })
    // 组装数据
    let allMonth=[]
    for(let y of ay){
      for(let m of aym[y+'']){
        allMonth.push(y+' - '+m)
      }
    }
    // 设置数据
    this.setData({
      allMonth
    })
  },
  prevEvt: function (e) {
    let idx = this.data.allMonth.findIndex(m => m === this.data.currMonth.year + ' - ' + this.data.currMonth.month)
    let dArr = this.data.allMonth[idx - 1].split(' - ')
    this.loadData(dArr)
  },
  nextEvt:function(e){
    let idx = this.data.allMonth.findIndex(m=>m === this.data.currMonth.year+' - '+this.data.currMonth.month)
    let dArr = this.data.allMonth[idx+1].split(' - ')
    this.loadData(dArr)
  },
  dateChange:function(e){
    // console.log(e)
    this.setData({
      date:e.detail.value
    })
  },
  timeChange: function (e) {
    // console.log(e)
    this.setData({
      time: e.detail.value
    })
  },
  init: function() {
    // 加载数据
    let tmpDate = utils.formatTime(new Date()).split(' ')[0]
    let dArr = tmpDate.split('-')
    // 设置下初始化时间
    this.data.currMonth.year = dArr[0]
    this.data.currMonth.month = dArr[1]
    this.setData({
      currMonth: this.data.currMonth
    })
    this.loadData(dArr)
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    // wx.navigateTo({
    //   url: '/pages/test/index',
    // })
    // 判断是否已经login
    if (app.globalData.logined) {
      this.init()
    } else {
      app.loginedCallback.push(res => {
        //console.log(res)
        wx.hideLoading()
        this.init()
      })
      // 显示 loading
      wx.showLoading({
        title: 'Loading...',
      })
    }
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
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  },
  headerEvt:function(e){
    // do somting
    counter.click()
  }
})