/**
 * 本项目 不会让用户长期保存，所以只存session
 */
export default {
  getToken () {
    return window.sessionStorage.getItem('token')
  },
  login (token, callback) {
    window.sessionStorage.removeItem('token')
    console.log('------new token :', token)
    window.sessionStorage.setItem('token', token)
    if (callback) callback()
  },
  logout (callback) {
    window.sessionStorage.removeItem('token')
    if (callback) callback()
  },
  loggedIn () {
    return !!this.getToken()// 登陆后会保存用户的token 到本地
  }
}
