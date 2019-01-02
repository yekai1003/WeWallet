<template>
  <div id="weixin_login"></div>
</template>

<script>
export default {
  name: 'wechat-login',
  data () {
    return {

    }
  },
  mounted () {
    const wx = new WxLogin({
      self_redirect: false,
      id: 'weixin_login',
      appid: 'wx7a4f70ab0c5439ad',
      scope: 'snsapi_login',
      redirect_uri: encodeURIComponent('http://wewallet.wzero.net/login'),
      // redirect_url: encodeURIComponent("http://192.168.10.11:8081/login"),
      state: this.clientId,
      style: 'black',
      href: 'http://wewallet.wzero.net/static/css/qrcode.css'
    })
  }
}
</script>

<style scoped>

</style>
