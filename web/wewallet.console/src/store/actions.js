/* 这里是所有异步请求的方法 api请求 */
import sdk from '@/sdk';
import types from "./mutation-types"
import auth from '@/common/auth'

const utils = {
  handleErrors(commit, res) {
    // console.log(res.errCode)
    if (res.errCode) {
      if (res.errCode === 'token_is_invalid') {
        //console.log('-----------disconnect--------')
        console.log('------看看res:', res)
        commit({ type: 'disconnect' })
      }
    }
  }
}

export const fetch = ({ commit, state }, payload) => {
  console.log(payload)
  if (!payload.data) payload.data = {}
  //有可能 state.token里没有，去 session里取
  if(!state.token)
    commit({type:types.SET_TOKEN,token:auth.getToken()})
  payload.data.token = state.token
  payload.data.clientid = state.clientId
  return sdk.use(payload.name, payload).then(res => {
    const validation = utils.handleErrors(commit, res)
    if(res.errCode)
      throw res
    else
      return res
  }).catch(err => {
    if (err.response) {
      if (err.response.data) {
        utils.handleErrors(commit, err.response.data)
      }
      throw err.response.data
    } else {
      console.log(err)
      throw err
    }
  })
}
