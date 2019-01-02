export default {
  getPowerGroups: {
    url: '/power/getGroups',
    require: ['token', 'clientid']
  },
  setUserApi: {
    url: '/power/setUserApi',
    require: ['token', 'clientid', 'apiId', 'type', 'userId']
  },
  setUserClient: {
    url: '/power/setUserClient',
    require: ['token', 'clientid', 'cId', 'type', 'userId']
  },
  setUserCompany: {
    url: '/power/setUserCompany',
    require: ['token', 'clientid', 'userId', 'companyId', 'type']
  }
}
