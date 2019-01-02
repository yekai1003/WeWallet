export default {
  getApiList: {
    url: '/api/getList',
    require: ['token', 'clientid']
  },
  getApiByUri: {
    url: '/api/getApiByUri',
    require: ['token', 'api', 'clientid']
  },
  getApi: {
    url: '/api/getApi',
    require: ['token', 'clientid', 'id']
  },
  updateApi: {
    url: '/api/updateApi',
    require: ['token', 'clientid', 'id']
  },
  createApi: {
    url: '/api/createApi',
    require: ['token', 'clientid']
  }
}
