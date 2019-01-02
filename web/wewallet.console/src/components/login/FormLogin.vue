<template>
  <el-form size="small">
    <el-form-item prop="account">
      <el-input type="text" v-model="loginForm.account" auto-complete="off" placeholder="账号"
                @keyup.native.enter="login"></el-input>
    </el-form-item>
    <el-form-item prop="checkPass">
      <el-input type="password" v-model="loginForm.password" auto-complete="off" placeholder="密码"
                @keyup.native.enter="login"></el-input>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click.native.prevent="login">登陆</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { mapActions } from 'vuex'
export default {
  name: 'form-login',
  data () {
    return {
      loginForm: {
        account: '',
        password: ''
      }
    }
  },
  methods: {
    ...mapActions({fetch: 'fetch'}),
    checkLoginForm (form) {
      if (!form.account) {
        this.$message({
          message: '账号不能为空',
          type: 'error'
        })
        return false
      } else if (!form.password) {
        this.$message({
          message: '密码不能为空',
          type: 'error'
        })
        return false
      } else {
        return true
      }
    },
    login () {
      if (this.checkLoginForm(this.loginForm)) {
        this.$store.commit('enableFullScreenLoading')
        this.fetch({
          name: 'loginByAccount',
          data: {
            username: this.loginForm.account,
            password: this.loginForm.password
          },
          method: 'post'
        }).then(res => {
          this.$store.commit('disableFullScreenLoading')
          if (res.errCode) {
            this.$message({
              message: res.errMsg,
              type: 'error'
            })
          } else if (res.token) {
            if (this.debug) console.log(res)
            this.$emit('loginDone', {type: 'account', data: res})
          } else {
            this.$message({
              message: '登陆失败',
              type: 'error'
            })
          }
        })
          .catch(error => {
            console.log(error)
            this.$store.commit('disableFullScreenLoading')
            this.$message.error('连不上服务器')
          })
      }
    },
    listenEvent (value) {
      this.$emit('changeLoginMethod', value)
    }
  }
}
</script>

<style scoped>

</style>
