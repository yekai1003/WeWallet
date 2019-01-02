<template>
  <el-row>
    <h3>用户组</h3>
    <el-col :span="24">
      <el-select v-model="user.group.id" placeholder="选择用户组">
        <el-option
          v-for="group in userGroups"
          :key="group.id"
          :label="group.groupName"
          :value="group.id">
        </el-option>
      </el-select>
      <el-button type="primary" @click="updateGroup">选择</el-button>
    </el-col>
  </el-row>
</template>

<script>
export default {
  props: ['userGroups', 'user'],
  data () {
    return {}
  },
  methods: {
    updateGroup () {
      this.$store.dispatch({
        type: 'fetch',
        name: 'setUserGroup',
        data: {
          groupId: this.user.group.id,
          uid: this.user.id
        }
      }).then(res => {
        if (res.errCode) {
          this.$message({
            message: res.errMsg,
            type: 'error'
          })
        } else {
          this.$message({
            message: '更新成功',
            type: 'success'
          })
        }
      }).catch(error => {
        this.$message({
          message: '连不上服务器',
          type: 'error'
        })
      })
    }
  }
}
</script>
