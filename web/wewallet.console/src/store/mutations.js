import auth from '@/common/auth'
import router from '../router'

export const enableFullScreenLoading = state=> {state.fullScreenLoading = true }
export const disableFullScreenLoading = state=> {state.fullScreenLoading = false }
export const login = (state,payload) => {
  auth.login(payload.token,()=>{
    state.token = payload.token
    if(payload.cb) payload.cb()
  })
}
export const disconnect = (state,payload)=> {
  //先清空session 而后 store 数据
  auth.logout(()=>{
    state.userInfo=null
    state.token = null
    //如果token失效，导航到登陆页面
    if(payload.callback)payload.callback()
    else
      router.push("/login")
  })
}
export const setClients = (state,payload)=>{
  state.clients = payload.clients;
};
