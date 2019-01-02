export default {
  /* @params */
  getClientList: {
    url: '/client/list',
    require: ['token', 'clientid']
  },
  getClientByCompany: {
    url: '/client/getByCompany',
    require: ['token', 'clientid', 'companyId']
  },
  getClientByApp: {
    url: '/client/getByApp',
    require: ['token', 'clientid', 'appId']
  },
  getClient: {
    url: '/client/get',
    require: ['token', 'clientid']
  },
  createClient: {
    url: '/client/create',
    require: ['token', 'clientid']
  },
  updateClient: {
    url: '/client/update',
    require: ['token', 'clientid', 'id']
  },
  deleteClient: {
    url: '/client/delete',
    require: ['token', 'clientid']
  },
  getClientTypeList: {
    url: '/clientType/list',
    require: ['token', 'clientid']
  }
}
