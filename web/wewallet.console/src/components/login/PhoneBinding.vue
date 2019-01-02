<template>
  <el-form>
    <h3 class="title">请输入您的电话号码</h3>
    <el-form-item>
      <el-input type="tel" placeholder="手机号码" v-model="binding.phoneNumber" @keyup.native.enter="requestPhoneCode">
        <el-button slot="append" @click.native="requestPhoneCode">发送验证</el-button>
      </el-input>
    </el-form-item>
    <el-form-item>
      <el-input type="text" v-model="binding.verificationCode" placeholder="验证码"
                @keyup.native.enter="submit"></el-input>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click.native.prevent="submit">提交</el-button>
      <el-button type="default" @click.native.prevent="cancel">取消</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
export default {
  data () {
    return {
      binding: {
        phoneNumber: '',
        verificationCode: ''
      }
    }
  },
  methods: {
    requestPhoneCode () {
      this.$emit('requestPhoneCode', this.binding.phoneNumber)
    },
    submit () {
      this.$emit('submit', this.binding)
    },
    cancel () {
      this.$emit('cancel')
    }
  }
}
</script>
