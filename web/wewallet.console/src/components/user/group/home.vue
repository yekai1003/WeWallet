<template>
  <section>
    <el-button size="mini" type="primary" @click="newGroupBtn">新用户组</el-button>

    <el-table :data="groupList" border style="margin-top: 10px">
      <el-table-column type="index" label="序号"/>
      <el-table-column label="ID" width="80" prop="id"/>
      <el-table-column label="名称" width="200" prop="groupName"/>
      <el-table-column label="描述" prop="description"/>
      <el-table-column label="权限">
        <template slot-scope="scope" >
          <el-button size="mini" type="text" @click="resourcesEditBtn(scope.row)">设置</el-button>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180px">
        <template slot-scope="scope" >
          <el-button size="mini" type="primary" @click="editBtn(scope.row)">编辑</el-button>
          <el-button size="mini" type="danger" @click="delBtn(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="currGroup.id?'编辑':'新建'" :visible.sync="showEditDialog" v-if="showEditDialog">
      <edit-group  :group="currGroup" v-on:confirm="confirmEditDialog"></edit-group>
    </el-dialog>
    <el-dialog title="编辑权限" :visible.sync="groupResources" v-if="groupResources" width="80%">
      <group-resources :group="currGroup" ></group-resources>
    </el-dialog>
  </section>
</template>

<script>
import editGroup from './editGroup'
import groupResources from './groupResources'
import errHandler from '@/assets/errHandler.js'
import { mapActions } from 'vuex'
export default {
  name: 'home',
  components: {groupResources, editGroup},
  data () {
    return {
      groupList: null,
      groupResources: false,
      showEditDialog: false,
      currGroup: null
    }
  },
  created () {
    this.getGroups()
  },
  methods: {
    ...mapActions({
      fetch: 'fetch'
    }),
    getGroups () {
      this.fetch({name: 'getUserGroupList'})
        .then(res => {
          console.log(res)
          this.groupList = res
        })
        .catch(err => {
          console.log(err)
          errHandler.handle(this, err)
        })
    },
    newGroupBtn () {
      this.currGroup = {groupName: '', description: ''}
      this.showEditDialog = true
    },
    editBtn (row) {
      this.currGroup = {id: row.id, groupName: row.groupName, description: row.description}
      this.showEditDialog = true
    },
    delBtn (row) {
      this.$confirm('确认删除？')
        .then(() => {
          this.fetch({name: 'deleteUserGroup', data: {groupId: row.id}})
            .then(res => {
              this.getGroups()
            })
            .catch(err => {
              console.log(err)
              errHandler.handle(this, err)
            })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
          this.loading = false
        })
    },
    confirmEditDialog (e) {
      console.log('dialog——————————', e)
      if (e.id) {
        // 保存
        this.updateUserGroup(e)
      } else {
        // 新建
        this.createUserGroup(e)
      }
    },
    createUserGroup (data) {
      this.fetch({name: 'createUserGroup', data})
        .then(res => {
          console.log(res)
          this.getGroups()
          this.showEditDialog = false
        })
        .catch(err => {
          console.log(err)
          errHandler.handle(this, err)
        })
    },
    updateUserGroup (data) {
      this.fetch({name: 'updateUserGroup', data})
        .then(res => {
          console.log(res)
          this.getGroups()
          this.showEditDialog = false
        })
        .catch(err => {
          console.log(err)
          errHandler.handle(this, err)
        })
    },
    resourcesEditBtn (row) {
      this.currGroup = row
      this.groupResources = true
    }
  }
}
</script>

<style scoped>

</style>
