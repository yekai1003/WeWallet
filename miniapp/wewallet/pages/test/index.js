// test index.js
const strutil = require('../../utils/stringutil.js')
const sdk = require('../../sdk/index.js')
//获取应用实例
const app = getApp()

Page({
  data: {
    //账户列表
    accounts: [],
    selEnv:null,
  },
  //事件处理函数
  itemToggle: function(e) {
    //console.log(e)
    const id= parseInt(e.currentTarget.id)
    const list = this.data.accounts
    for (let i = 0, len = list.length; i < len; ++i) {
      if (list[i].id === id) {
        list[i].open = !list[i].open
        // 如果打开，判断下是否有有token，没有的话，去服务器请求下
        if (list[i].open){
          // 全局保存 当前打开 项目
          app.globalData.indexOpenAcctId = list[i].id// 
          if (!list[i].tokens)// 已经存在就不用了 空数组也算
            this.loadTokens(list[i].id,this.data.selEnv.val)
        }
      } else {
        list[i].open = false
      }
    }
    this.setData({
      accounts:list
    })
  },
  // 显示交易 列表
  showTransactionsEvt : function(e){
    wx.navigateTo({
      url: '/pages/transactions/list?acctId='+e.currentTarget.dataset.id,
    })
  },
  // 编辑 按钮
  editAcctEvt:function(e){
    console.log(e)
    wx.navigateTo({
      url: '/pages/account/edit?acctId='+e.currentTarget.dataset.id,
    })
    //测试
    // wx.getClipboardData({
    //   success:res=>{
    //     console.log(res.data)
    //   }
    // })
  },
  // 拷贝按钮
  copyAddrEvt: function (e) {
    console.log(e)
    let acct = this.data.accounts.find(a=>a.id===e.currentTarget.dataset.id)
    console.log(acct)
    if(acct)
      wx.setClipboardData({
        data: '0x'+acct.addr,
      })
  },
  // deposit 按钮
  depositEvt:function(e){

  },
  // send 按钮
  sendEvt : function(e) {
    if(e.currentTarget.dataset.type === "account")
      wx.navigateTo({
        url: '/pages/send/send?acctId=' + e.currentTarget.dataset.id,
      })
    else
      wx.navigateTo({
        url: '/pages/send/send?tokenId=' + e.currentTarget.dataset.id,
      })
  },
  // 刷新 账号
  refreshAcctEvt: function(e){
    let acctId = e.currentTarget.dataset.acctid
    this.refreshAcct(parseInt(acctId)) // 转成int 好比较
  },
  refreshAcct:function(acctId){
    let page = this
    wx.showLoading({
      title: 'refreshing...',
    })
    sdk.fetch({
      api: '/core/wallet/refresh',
      data: { id: acctId },
      success: res => {
        console.log(res)
        if (res.balances[this.data.selEnv.val].isRefreshing) {
          // 如果在刷新状态中，则再次获取
          setTimeout((aId)=>{
            sdk.fetch({
              api:'/core/wallet/get',
              data:{id:aId},
              success: res2 => {
                console.log(res2)
                let aIndex = page.data.accounts.findIndex(a=> a.id === aId)
                if(aIndex >= 0){
                  // page.data.accounts[aIndex].balances[page.data.selEnv.val] = res2.balances[page.data.selEnv.val]
                  page.setData({
                    ["accounts[" + aIndex + "].balances." + page.data.selEnv.val]: res2.balances[page.data.selEnv.val]
                  })
                }
              },
              fail:err2=>{
                console.log("----get failed",err)
              },
              complete: e => {
                wx.hideLoading()
              }
            })
          },2000,acctId)//2秒后
        }else{
          //直接更新
          page.setData({
            ["accounts[" + aIndex + "].balances." + page.data.selEnv.val]: res.balances[page.data.selEnv.val]
          })
        }
      },
      fail:err=>{
        console.log(err)
        wx.hideLoading()
      }
    })
  },
  // 刷新 token
  refreshTokenEvt: function (e) {
    let acctId = e.currentTarget.dataset.acctid // 账户ID 为了更快找到目标token，所以提供
    let tokenId = e.currentTarget.dataset.tokenid
    this.refreshToken(parseInt(acctId),parseInt(tokenId))
  },
  refreshToken: function (acctId, tokenId) {
    let page = this
    // 先找到 acct 索引 和 token索引
    let acctidx = this.data.accounts.findIndex(a=> a.id === acctId)
    let tokenidx = this.data.accounts[acctidx].tokens.findIndex(t => t.id === tokenId)
    wx.showLoading({
      title: 'refreshing...',
    })
    sdk.fetch({
      api:'/core/token/refresh',
      data:{tokenId:tokenId},
      success:res=>{
        // console.log(res)
        if (res.isRefreshing){
          setTimeout((tId) => {
            sdk.fetch({
              api: '/core/token/get',
              data: { id: tId },
              success: res2 => {
                // console.log(res2)
                page.setData({
                  ["accounts[" + acctidx + "].tokens[" + tokenidx + "]"]: res2
                })
              },
              fail: err2 => {
                console.log(err2)
              },
              complete: e => {
                wx.hideLoading()
              }
            })
          },5000,tokenId)
        }else{
          page.setData({
            ["accounts["+acctidx+"].tokens["+tokenidx+"]"]:res
          })
        }
      },
      fail:err=>{
        wx.hideLoading()
      }
    })
  },
  // 环境发生变化
  envChanged(e) {
    console.log(e.detail)
    this.setData({
      selEnv: e.detail.env
    })
    //环境发生变化，清空所有账户的 tokens
    this.reloadTokens()
  },
  // 清空 所有token，并请求打开状态的account 的token
  reloadTokens(){
    const list = this.data.accounts
    for (let i = 0; i < list.length; ++i){
      list[i].tokens = null
      if(list[i].open)
        this.loadTokens(list[i].id,app.globalData.currEnv)
    }
  },
  loadTokens(acctId,env){
    let idx = this.data.accounts.findIndex(a=> a.id === acctId)
    console.log("--idx:"+idx)
    sdk.fetch({
      api: '/core/token/list',
      data: { accountId: this.data.accounts[idx].id, env },
      success: res => {
        this.data.accounts[idx].tokens = res
        this.setData({
          ["accounts[" + idx + "]"]: this.data.accounts[idx] //局部刷新
        })
      },
      fail: err => {
        console.log(err)
      }
    })
  },
  // 新建账户页面导航
  newAcctEvt:function(e){
    wx.navigateTo({
      url: '/pages/account/new',
    })
  },
  // 添加token
  addTokenEvt:function(e){
    console.log(e)
    wx.navigateTo({
      url: '/pages/account/addToken?acctId='+e.currentTarget.dataset.acctid,
    })
  },
  init:function(){
    console.log("----init----")
    // 加载 用户账户列表
    sdk.fetch({
      api:'/core/wallet/listAccounts',
      success:res=>{
        console.log(res)
        if (app.globalData.indexOpenAcctId){
          // 循环列表 打开 指定项，并获取tokens
          console.log(typeof app.globalData.indexOpenAcctId)
          let openIndex = res.findIndex(r => r.id == app.globalData.indexOpenAcctId)
          console.log('---found index:' + openIndex)
          if(openIndex>=0){//found
            res[openIndex].open=true
            this.loadTokens(res[openIndex].id,app.globalData.currEnv)
          }
        }
        this.setData({
          accounts:res
        })
      }
    })
  },
  onLoad: function () {
    // 判断是否已经login
    if(app.globalData.logined){
      this.init()
    }else{
      app.loginedCallback.push(res=>{
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
  onShow: function(e){
    console.log('---on show--',e)
    if(app.globalData.indexRefresh){
      app.globalData.indexRefresh=false
      this.init()
    }
  },
  onPullDownRefresh: function () {
    // do somthing
    this.init()
    wx.stopPullDownRefresh()
  },
})
