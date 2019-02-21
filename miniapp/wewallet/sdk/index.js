const config = require('./config.js')
const errHandler = require('./errorHandler.js')

/*
  1、请求方法定义
  2、系统参数定义
 */
function Sdk(){
  this.token=null
  this.fetch = (params)=> {
    //1、api 名称
    let that =this
    try {
      this.checkParams(params)
      let sysParams = { clientid: config.clientid }
      if(this.token){
        sysParams.token = this.token
      }
      if(params.token){
        sysParams.token = params.token
        // console.log(params)
      }
      wx.request({
        url: config.url + params.api,
        method: config.method,
        header: {
          "content-type": "application/x-www-form-urlencoded"
        },
        data: {
          ...sysParams,
          ...params.data
        },
        dataType: 'json',
        success: function (e) {
          that.handlerError(e,params).then((res)=>{
            if (params.success)
              params.success(res)
          }).catch((err) => {
            if (params.fail)
              params.fail(err)
          })
        },
        fail: function (err) {
          if (params.fail)
            params.fail(err)
        },
        complete: function (e) {
          if (params.complete)
            params.complete(e)
        }
      })  
    }catch(err){
      if(params.error)
        params.error(err)
      else{
        if(params.fail)
          params.fail(err)
      }
    } 
  }
  this.uploadFile = (params) => {
    let that = this
    try {
      this.checkParams(params)
      let sysParams = { clientid: config.clientid }
      if (this.token) {
        sysParams.token = this.token
      }
      if (params.token) {
        sysParams.token = params.token
      }
      let formData = {
        ...sysParams,
        ...params.data}
      //formdata to query
      let query = this.objToQueryString(formData)
      wx.uploadFile({
        url: config.url + params.api+query,
        filePath: params.filePath,
        name: 'file',
        success: function (e) {
          that.handlerError(e, params).then((res) => {
            if (params.success)
              params.success(JSON.parse(res))//程序返回的是字符串
          }).catch((err) => {
            if (params.fail)
              params.fail(JSON.parse(err))
          })
        },
        fail: function (err) {
          if (params.fail)
            params.fail(JSON.parse(err))
        },
        complete: function (e) {
          if (params.complete)
            params.complete(e)
        }
      })
    } catch (err) {
      if (params.error)
        params.error(err)
      else {
        if (params.fail)
          params.fail(err)
      }
    }
  }
  this.objToQueryString=(obj)=>{
    return '?'+Object.keys(obj).map((key)=>{
      return encodeURIComponent(key) + '=' +
        encodeURIComponent(obj[key]);
    }).join('&')
  }
  this.handlerError=(res,params) => {
    return new Promise((resolve, reject) => {
      if(res.data.errCode){
        errHandler.doAction(res.data,params)
        reject(res.data)
      }else{
        resolve(res.data)
      }
    })
    
  }
  this.checkParams= (params) => {
    if (!params.api) throw new Error("api没有设置")
    //2、method get,post
    params.method = params.method ? config.method : params.method
    //other
    //token检查
    let tokenObj = wx.getStorageSync('token')
    
    if (tokenObj) {
      //判断下过期时间
      this.token = tokenObj.token
    }
    //if(!this.token) throw new Error("未能获取token")
    //console.log(this.token)
    return true
  }
}

module.exports = new Sdk()
