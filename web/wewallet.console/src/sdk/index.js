import config from './config'
import axios from 'axios'
import jsonp from 'jsonp'
import apiList from './api'
import qs from 'qs'

class SDK {
  use (name, payload) {
    switch (payload.method) {
      case 'jsonp':
        return this.jsonp(name, payload)
      case 'post':
        return this.post(name, payload)
      case 'get':
      default:
        return this.get(name, payload)
    }
  };
  get (apiName, payload) { return this.fetch(apiName, payload, 'get') };
  post (apiName, payload) { return this.fetch(apiName, payload, 'post') };
  fetch (apiName, payload, requestType) {
    return new Promise((resolve, reject) => {
      const api = apiList[apiName]
      if (!api) reject({errMsg: config.errors.apiNotFound})
      const request = this.request.prepare(payload, config.serverAddress + api.url, api.require)
      let timeout = config.timeout
      if (payload.timeout) timeout = payload.timeout
      switch (requestType) {
        case 'post':
          axios({
            method: 'post',
            url: request.url,
            data: qs.stringify(request.data),
            timeout
          }).then(response => {
            resolve(response.data)
          }).catch(error => {
            reject(error)
          })
          break
        default:
          axios.get(request.url, {
            params: request.data,
            timeout: timeout
          }).then(response => {
            resolve(response.data)
          }).catch(error => {
            reject(error)
          })
          break
      }
    })
  };
	jsonp = function (apiName, payload) {
	  return new Promise((resolve, reject) => {
	    const api = apiList[apiName]
	    if (!api) reject({errMsg: config.errors.apiNotFound})
	    const request = this.request.prepare(payload, config.serverAddress + api.url, api.require)
	    jsonp(request.url, null, function (err, data) {
	      if (err) {
	        console.error(err.message)
	        reject(err)
	      } else {
	        if (data) {
	          resolve(data)
	        }
	      }
	    })
	  })
	};
	request = {
	  prepare: function (payload, url, require) {
	    const request = {token: payload.token, clientid: payload.clientid, url, ...payload}
	    const validation = this.validate(request, require)
	    if (validation) return request
	    else return false
	  },
	  validate: function (request, require) {
	    require.map(requirement => {
	      if (request.data[requirement] === null || request.data[requirement] === undefined) {
	        throw config.errors.missingParam + requirement
	      }
	    })
	    return true
	  }
	};
}
export default new SDK()
