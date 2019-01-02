<template>
  <el-col :span="16">
    <el-form ref="form" :model="newApi" label-width="120px">
      <el-form-item label="接口地址">
        <el-input v-model="newApi.uri"></el-input>
      </el-form-item>
      <el-form-item label="接口描述">
        <el-input v-model="newApi.description"></el-input>
      </el-form-item>
      <el-form-item label="需要Token">
        <el-switch active-text="是" inactive-text="否" v-model="newApi.needAuthorization"></el-switch>
      </el-form-item>
      <el-form-item label="是否公用">
        <el-switch active-text="是" inactive-text="否" v-model="newApi.isPublic"></el-switch>
      </el-form-item>
      <el-form-item label="是否可用">
        <el-switch active-text="是" inactive-text="否" v-model="newApi.isEnable"></el-switch>
      </el-form-item>
      <el-form-item>
        <el-button type="default" @click="submit">提交</el-button>
        <el-button type="default" @click="cancel()">取消</el-button>
      </el-form-item>
    </el-form>
  </el-col>
</template>

<script>
export default {
  name: 'new-api-form',
  data () {
    return {
      newApi: {
        uri: '',
        description: '',
        requirements: [],
        needAuthorization: true,
        isPublic: false,
        isEnable: true
      }
    }
  },
  methods: {
    cancel () {
      this.$emit('triggerReset')
    },
    submit () {
      this.$emit('triggerSubmitNewApi', this.newApi)
    }
  }
}
</script>
