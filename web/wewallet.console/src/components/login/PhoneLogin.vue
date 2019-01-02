<template>
    <el-form size="small">
      <el-form-item>
        <el-input type="tel" placeholder="手机号码" v-model="phoneLogin.phoneNumber" @keyup.native.enter="requestPhoneCode">
          <el-button slot="append" @click.native="requestPhoneCode" ref="button">发送验证</el-button>
        </el-input>
      </el-form-item>
      <el-form-item>
        <el-input type="text" v-model="phoneLogin.verificationCode" auto-complete="off" placeholder="验证码"
                  @keyup.native.enter="phoneLogin"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click.native.prevent="login">提交</el-button>
      </el-form-item>
    </el-form>
</template>

<script>
export default {
  name: 'phone-login',
  data () {
    return {
      phoneLogin: {
        disabled: true,
        btnText: '发送验证',
        phoneNumber: '',
        verificationCode: ''
      }
    }
  },
  methods: {
    listenEvent (value) {
      this.$emit('changeLoginMethod', value)
    },
    submit () {
      if (!this.phoneLogin) {
        console.log('Error retrieving binding phone data')
        return false
      }
      if (!this.phoneLogin.phoneNumber) {
        this.$message({
          message: '手机号不能为空',
          type: 'error'
        })
        return false
      }
      if (!this.phoneLogin.verificationCode) {
        this.$message({
          message: '验证码不能为空',
          type: 'error'
        })
        return false
      }
      this.fetch({
        name: 'setUserPhone',
        data: {
          phone: this.phoneLogin.phoneNumber,
          vcode: this.phoneLogin.verificationCode
        },
        method: 'post'
      }).then(res => {
        if (res.errCode) {
          this.$message({
            message: res.errMsg,
            type: 'error'
          })
        } else {
          // this.$store.commit({ type: 'setUser', user: res });
          this.$emit('loginDone', {type: 'phone', data: res})
        }
      })
    },
    requestPhoneCode () {
      const btn = this.$refs.button
      if (!this.phoneLogin.phoneNumber) {
        this.$message({ message: '手机号不能为空', type: 'error' })
        return false
      } else if (!regEx.mobilePhone(this.phoneLogin.phoneNumber)) {
        this.$message({ message: '手机格式正确', type: 'error' })
      } else {
        this.fetch({
          name: 'phoneVCode',
          data: { phone: this.phoneLogin.phoneNumber },
          method: 'post'
        })
          .then(res => {
            this.$message({ message: '短信已发送', type: 'success' })
            let sec = 60
            btn.$el.children[0].innerHTML = sec + '秒'
            let timer = setInterval(() => {
              sec--
              btn.$el.children[0].innerHTML = sec + '秒'
              btn.$el.setAttribute('disabled', true)
              if (sec === -1) {
                btn.$el.children[0].innerHTML = '发送验证'
                btn.$el.removeAttribute('disabled')
                clearInterval(timer)
              }
            }, 1000)
          })
      }
    }
  }
}
</script>
