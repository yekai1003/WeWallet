export default {
  getUserGroupList: {
    url: '/usergroup/getAll',
    require: ['token', 'clientid']
  },
  getUserGroup: {
    url: '/usergroup/get',
    require: ['token', 'clientid']
  },
  createUserGroup: {
    url: '/usergroup/create',
    require: ['token', 'clientid']
  },
  updateUserGroup: {
    url: '/usergroup/update',
    require: ['token', 'clientid']
  },
  deleteUserGroup: {
    url: '/usergroup/delete',
    require: ['token', 'clientid', 'groupId']
  },
  setUserGroupApi: {
    url: '/usergroup/setUserApi',
    require: ['token', 'clientid', 'apiId']
  },
  setUserGroupClient: {
    url: '/usergroup/setUserClient',
    require: ['token', 'clientid', 'clientId']
  },
  setUserGroupCompany: {
    url: '/usergroup/setUserCompany',
    require: ['token', 'clientid', 'companyId']
  }
}
