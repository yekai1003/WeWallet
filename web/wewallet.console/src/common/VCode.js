var regBox = function () {
  var tmp = {
    regEmail: /^([a-z0-9_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6})$/, // 邮箱
    regName: /^[a-z0-9_-]{3,16}$/, // 用户名
    regMobile: /^0?1[3|4|5|8][0-9]\d{8}$/, // 手机
    regTel: /^0[\d]{2,3}-[\d]{7,8}$/
  }
  return tmp
}

export default{
  judgeMobile (mobile) {
    var regBox = {
      regEmail: /^([a-z0-9_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6})$/, // 邮箱
      regName: /^[a-z0-9_-]{3,16}$/, // 用户名
      regMobile: /^0?1[3|4|5|8][0-9]\d{8}$/, // 手机
      regTel: /^0[\d]{2,3}-[\d]{7,8}$/
    }

    var mflag = regBox.regMobile.test(mobile)
    console.log(regBox.regMobile)
    if (!(mflag && tflag)) {
      console.log('手机或者电话有误！')
    } else {
      console.log('信息正确！')
    }
  },
  judgeEmail (date) {
    var mflag = regBox().regMobile.test(date)
    // if (!mflag) {
    //   console.log("手机或者电话有误！")
    // }else{
    //   console.log("信息正确！")
    // }
    return mflag
  }
}
