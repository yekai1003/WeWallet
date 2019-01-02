export default {
  loginByAccount: {
    url: '/authorize/login',
    require: ['username', 'password', 'clientid']
  },
  loginByPhone: {
    url: '/authorize/phoneLogin',
    require: ['phone', 'vcode', 'clientid']
  },
  getUserByWxCode: {
    url: '/authorize/wxLogin',
    require: ['code', 'clientid']
  },
  phoneVCode: {
    url: '/authorize/sendVerificationCode',
    require: ['phone', 'clientid']
  },
  sendVCByBindingCustomer: {
    url: '/authorize/sendVCByBindingCustomer',
    require: ['phone', 'clientid', 'customerId', 'phone']
  }
}
