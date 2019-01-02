<template>
  <el-col :span="16">
    <el-form ref="form" :model="editingApi" label-width="120px">
      <el-form-item label="接口地址">
        <el-input v-model="editingApi.uri"></el-input>
      </el-form-item>
      <el-form-item label="接口描述">
        <el-input v-model="editingApi.description"></el-input>
      </el-form-item>
      <!--<el-form-item label="必要参数">-->
      <!--<el-select-->
      <!--style="width: 100%"-->
      <!--v-model="editingApi.requirements"-->
      <!--multiple-->
      <!--filterable-->
      <!--allow-create-->
      <!--placeholder="选择或者写必要的参数">-->
      <!--<el-option-->
      <!--style="width: 100%;margin:0;box-sizing: border-box;display: block"-->
      <!--v-for="item in requirementsOptions"-->
      <!--:key="item.value"-->
      <!--:label="item.label"-->
      <!--:value="item.value">-->
      <!--</el-option>-->
      <!--</el-select>-->
      <!--</el-form-item>-->
      <el-form-item label="需要Token">
        <el-switch active-text="是" inactive-text="否" v-model="editingApi.needAuthorization"></el-switch>
      </el-form-item>
      <el-form-item label="是否公用">
        <el-switch active-text="是" inactive-text="否" v-model="editingApi.isPublic"></el-switch>
      </el-form-item>
      <el-form-item label="是否可用">
        <el-switch active-text="是" inactive-text="否" v-model="editingApi.isEnable"></el-switch>
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
  name: 'edit-api-form',
  props: ['api'],
  computed: {
    editingApi: function () {
      return this.api
    }
  },
  methods: {
    submit () {
      this.$emit('triggerSubmitEditApi', this.editingApi)
    },
    cancel () {
      this.$emit('triggerReset')
    }
  }
}
</script>
