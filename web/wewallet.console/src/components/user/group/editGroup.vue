<template>
  <section>
    <el-form label-width="100px">
      <el-form-item label="名称">
        <el-input size="mini" v-model="group.groupName" style="width: 50%"></el-input>
      </el-form-item>
      <el-form-item label="描述">
        <el-input size="mini" type="textarea" v-model="group.description" style="width: 50%"></el-input>
      </el-form-item>
      <el-form-item label="">
        <el-button size="mini" type="success" @click="confirmBtn">{{group.id?'保存':'提交'}}</el-button>
      </el-form-item>
    </el-form>
  </section>
</template>

<script>
import {mapActions} from 'vuex'
export default {
  name: 'editGroup',
  props: ['group'],
  data () {
    return {
      groupList: null
    }
  },
  created () {
    console.log(this.group)
  },
  methods: {
    ...mapActions({
      fetch: 'fetch'
    }),
    confirmBtn () {
      this.$emit('confirm', this.group)
    }
  }
}
</script>

<style scoped>

</style>
