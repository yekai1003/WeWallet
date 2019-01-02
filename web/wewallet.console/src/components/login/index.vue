<template>
  <div style="height:100vh" v-loading.fullscreen.lock="fullScreenLoading">
    <div class="login-container">
      <section class="login-form">
        <h3 class="title">系统登录</h3>
        <!--Login by account-->
        <div style="padding:0 35px 0 35px;">
          <div class="account-login" v-if="selectedTab==='account'">
            <FormLogin
              v-on:loginDone="loginDone"></FormLogin>
          </div>
          <!--Login by phone number-->
          <div class="account-login" v-if="selectedTab==='phone'">
            <PhoneLogin
              v-on:loginDone="loginDone"></PhoneLogin>
          </div>
          <div class="account-login" v-if="selectedTab==='wechat'">
            <WechatLogin></WechatLogin>
          </div>
        </div>
        <div class="login-alter">
          <div class="login-alter-item" v-for="alt of loginAlt" :key="alt.name">
            <icon :name="alt.name" :scale="alt.scale" :class="{active:alt.key===selectedTab}" @click.native.prevent="listenEvent(alt.key)"></icon>
          </div>
        </div>
      </section>
      <!--Link the user with phone number-->
      <section class="login-form" v-if="phoneInput">
        <PhoneBinding
          v-on:requestPhoneCode="requestPhoneCode"
          v-on:cancel="resetLogin"
          v-on:submit="bindPhone"
        ></PhoneBinding>
      </section>
    </div>
  </div>
</template>

<script>
import FormLogin from './FormLogin.vue'
import PhoneLogin from './PhoneLogin.vue'
import PhoneBinding from './PhoneBinding.vue'
import types from '@/store/mutation-types'
import {mapGetters, mapActions, mapMutations} from 'vuex'
import WechatLogin from './WechatLogin'

export default {
  name: 'login',
  components: {
    WechatLogin,
    FormLogin,
    PhoneLogin,
    PhoneBinding
  },
  data: function () {
    return {
      // Login alternatives
      selectedTab: 'account',
      loginAlt: [
        {key: 'account', name: 'user', scale: '1.7'},
        {key: 'wechat', name: 'brands/weixin', scale: '1.7'},
        {key: 'phone', name: 'mobile', scale: '1.7'}
      ],
      // Phone form
      phoneInput: false,
      // SMS login
      redirect: '/index'
    }
  },
  computed: {
    ...mapGetters(['userInfo', 'fullScreenLoading'])
  },
  mounted () {
    // 看下有没有信息
    console.log(this.$route.query)
    if (this.$route.query.redirect) { this.redirect = this.$route.query.redirect }
    if (this.$route.query.code) { // 微信登陆

    }
  },
  methods: {
    ...mapMutations({
      setUserInfo: types.SET_USER_INFO,
      login: 'login'
    }),
    ...mapActions({
      fetch: 'fetch',
      loadMenu: 'loadMenuList'
    }),
    // Utilities
    listenEvent (name) {
      this.selectedTab = name
    },
    loginDone (params) {
      console.log(params)
      // 登陆只要保存 token 即可
      this.login({token: params.data.token,
        cb: () => {
        // 路由
          this.$router.push(this.redirect)
        }})
    },
    resetLogin () {
      this.phoneInput = false
      // this.$store.commit({ type: 'setToken', token: '' });
      this.login('')
    }
  }
}
</script>

<style lang="scss">
  @keyframes shrink {
    0% {
      background-size: 110% 110%;
    }
    100% {
      background-size: 100% 100%;
    }
  }

  .login-container {
    position: relative;
    height: 100%;
    /*background-image: url('/static/img/login-bg.jpg');*/
    background-repeat: no-repeat;
    background-size: cover;
    /*animation: shrink 10s infinite alternate;*/
  }

  .login-form {
    position: absolute;
    width: 33%;
    min-width: 200px;
    left: 50%;
    top: 40%;
    transform: translate(-50%, -50%);
    /*box-shadow: 0 0px 8px 0 rgba(0, 0, 0, 0.06), 0 1px 0px 0 rgba(0, 0, 0, 0.02);*/
    -webkit-border-radius: 5px;
    border-radius: 5px;
    -moz-border-radius: 5px;
    background-clip: padding-box;
    padding: 35px 35px 15px 35px;
    background: #fff;
    border: 1px solid #eaeaea;
    box-shadow: 0 0 25px #cac6c6;
    .title {
      margin: 0px auto 35px auto;
      text-align: center;
      color: #505458;
      font-size: 18px;
    }
    .login-alter {
      bottom:35px;
      right:45px;
      position: absolute;
      .login-alter-item {
        padding: 0 8px;
        cursor: pointer;
        display: inline-block;
        width: 35px;
        color:#a4a4a4;
      }
      .active{
        color:#3a3a3a;
      }
      &:after{
        clear:both;
      }
    }
  }
</style>
