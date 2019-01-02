export default {
  getSelfInfo: {// 获取自己用户数据
    url: '/user/getSelfInfo',
    require: ['token', 'clientid']
  },
  setUserPhone: {
    url: '/user/bindingPhone',
    require: ['token', 'clientid', 'phone']
  },
  getUser: {
    url: '/user/getUser',
    require: ['token', 'clientid', 'uid']
  },
  setUserGroup: {
    url: '/user/setGroup',
    require: ['token', 'clientid', 'groupId']
  },
  // member
  getMember: {// 获取账户，mid
    url: '/user/getMember',
    require: ['token', 'clientid']
  },
  getMembers: {// 获取账户列表，keywords,pageNo
    url: '/user/getMembers',
    require: ['token', 'clientid']
  },
  createMember: {// 添加账户
    url: '/user/addMember',
    require: ['token', 'clientid', 'groupId', 'username', 'loginname', 'loginpwd']// phone(选)
  },
  updateMember: {// 更新账户
    url: '/user/updateMember',
    require: ['token', 'clientid', 'memberId']// username(选、,loginname(选)、loginpwd（选，loginname未设置被忽略）、groupId(选)、phone(选)
  },
  enableMember: {// 设置账户是否可用
    url: '/user/enableMember',
    require: ['token', 'clientid', 'memberId']// enable(true,false)
  },
  updateMemberWeixin: {// 更新微信账户
    url: '/user/updateMemberWeixin',
    require: ['token', 'clientid', 'encryptedData']// iv(json数组),rawData
  },
  createMemberAddress: {// 添加账户地址
    url: '/user/createMemberAddress',
    require: ['token', 'clientid', 'memberId']// cityName、countyName、detailInfo、nationalCode、postalCode、provinceName、telNumber、userName
  },
  delMemberAddress: {// 删除账户地址
    url: '/user/createMemberAddress',
    require: ['token', 'clientid', 'memberAddressId']//
  },
  createCustomerInMember: {// 管理员给指定member创建customer，参数：memberId,name ,gender ,phone,vcode
    url: '/user/createCustomerForMemberByAdmin',
    require: ['token', 'clientid', 'memberId', 'name', 'gender', 'phone', 'vcode']// phone
  },
  bindCustomerInMember: {// 管理员给用户绑定已经存在的客户信息，参数： memberId、phone , vcode (只有绑定过手机的用户才可以)
    url: '/user/bindingCustomerByAdmin',
    require: ['token', 'clientid', 'memberId', 'phone', 'vcode']
  },
  unbindingCustomerByAdmin: {// 管理员给member解绑customer，参数： memberId、phone , vcode (只有绑定过手机的用户才可以)
    url: '/user/unbindingCustomerByAdmin',
    require: ['token', 'clientid', 'memberId', 'phone', 'vcode']
  },
  /// user/unbindingCustomerByAdmin
  // customer
  createCustomer: {// 管理员创建用户
    url: '/user/createCustomerByAdmin',
    require: ['token', 'clientid', 'name', 'gender', 'phone']// birthday(选)、email(选)、isMarried(选)
  },
  getCustomer: {// 获取用户，uid
    url: '/user/getCustomer',
    require: ['token', 'clientid']
  },
  getCustomerList: {// 查询用户，keywords,groupId
    url: '/user/getCustomers',
    require: ['token', 'clientid']
  },

  updateCustomerData: {// 管理员更新用户data
    url: '/user/updateDataByAdmin',
    require: ['token', 'clientid']// customerId
  },
  updateCustomer: {// 管理员更新customer
    url: '/user/updateCustomerByAdmin',
    require: ['token', 'clientid', 'id']// birthday,gender,isMarried,name,avatarUrl,phone,email
  },
  getSelfCustomers: {// 获取自己关联的客户列表
    url: '/user/getSelfCustomers',
    require: ['token', 'clientid']
  },
  getCustomerByPhone: {// 通过手机号获取客户列表，参数： phone
    url: '/user/getCustomerByPhone',
    require: ['token', 'clientid']
  }
}
